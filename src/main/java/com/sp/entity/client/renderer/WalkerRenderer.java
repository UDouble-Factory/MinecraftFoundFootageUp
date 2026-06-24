package com.sp.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sp.SPBRevamped;
import com.sp.entity.client.debug.IKDebugRenderLayer;
import com.sp.entity.client.model.WalkerModel;
import com.sp.entity.custom.WalkerEntity;
import com.sp.entity.ik.model.GeckoLib.GeoModelAccessor;
import com.sp.entity.ik.model.GeckoLib.MowzieGeoBone;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.specialty.DynamicGeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtil;

public class WalkerRenderer extends DynamicGeoEntityRenderer<WalkerEntity> {
    private final ResourceLocation EYES_TEXTURE = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "textures/entity/walker/walker.png");

    public WalkerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WalkerModel());
        this.addRenderLayer(new IKDebugRenderLayer<>(this));
    }

    @Override
    public void render(WalkerEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        entity.getModelPositions(entity, new GeoModelAccessor(this.model));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, WalkerEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone == null) return;
        poseStack.pushPose();
        /*
        if (Objects.equals(bone.getName(), "root") && bone instanceof MowzieGeoBone mowzieGeoBone) {
            mowzieGeoBone.setForceMatrixTransform(true);
            bone.updateRotation((float) -Math.toRadians(animatable.getPitch() + 90), (float) -Math.toRadians(animatable.getYaw()), 0);
        }

         */

        if (bone instanceof MowzieGeoBone mowzieGeoBone && mowzieGeoBone.isForceMatrixTransform() && animatable != null) {
            PoseStack.Pose last = poseStack.last();
            double d0 = animatable.getX();
            double d1 = animatable.getY();
            double d2 = animatable.getZ();
            Matrix4f matrix4f = new Matrix4f();
            matrix4f = matrix4f.translate(0, -0.01f, 0);
            matrix4f = matrix4f.translate((float) -d0, (float) -d1, (float) -d2);
            matrix4f = matrix4f.mul(bone.getWorldSpaceMatrix());
            last.pose().mul(matrix4f);
            last.normal().mul(bone.getWorldSpaceNormal());

            RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
        } else {
            boolean rotOverride = false;
            if (bone instanceof MowzieGeoBone mowzieGeoBone) {
                rotOverride = mowzieGeoBone.rotationOverride != null;
            }

            RenderUtil.translateMatrixToBone(poseStack, bone);
            RenderUtil.translateToPivotPoint(poseStack, bone);

            if (bone instanceof MowzieGeoBone mowzieGeoBone) {
                if (!mowzieGeoBone.inheritRotation && !mowzieGeoBone.inheritTranslation) {
                    poseStack.last().pose().identity();
                    poseStack.last().pose().mul(this.entityRenderTranslations);
                } else if (!mowzieGeoBone.inheritRotation) {
                    Vector4f t = new Vector4f().mul(poseStack.last().pose());
                    poseStack.last().pose().identity();
                    poseStack.translate(t.x, t.y, t.z);
                } else if (!mowzieGeoBone.inheritTranslation) {
                    MowzieGeoBone.removeMatrixTranslation(poseStack.last().pose());
                    poseStack.last().pose().mul(this.entityRenderTranslations);
                }
            }

            if (rotOverride) {
                MowzieGeoBone mowzieGeoBone = (MowzieGeoBone) bone;
                poseStack.last().pose().mul(mowzieGeoBone.rotationOverride);
                poseStack.last().normal().mul(new Matrix3f(mowzieGeoBone.rotationOverride));
            } else {
                RenderUtil.rotateMatrixAroundBone(poseStack, bone);
            }

            RenderUtil.scaleMatrixForBone(poseStack, bone);

            if (bone.isTrackingMatrices()) {
                Matrix4f poseState = new Matrix4f(poseStack.last().pose());
                Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

                bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
                localMatrix.translate(new Vector3f(getRenderOffset(this.animatable, 1).toVector3f()));
                bone.setLocalSpaceMatrix(localMatrix);
                bone.setWorldSpaceMatrix(RenderUtil.translateMatrix(new Matrix4f(localMatrix), this.animatable.position().toVector3f()));
            }

            RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
        }


        if(!this.boneRenderOverride(animatable, poseStack, bone, bufferSource))
            super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
        if (!isReRender)
            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        poseStack.popPose();
    }

    @Override
    public void renderChildBones(PoseStack poseStack, WalkerEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        for (GeoBone childBone : bone.getChildBones()) {
            if (!bone.isHidingChildren() || (childBone instanceof MowzieGeoBone mowzieGeoBone && mowzieGeoBone.isDynamicJoint())) {
                renderRecursively(poseStack, animatable, childBone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
            }
        }
    }

    protected boolean boneRenderOverride(WalkerEntity animatable, PoseStack poseStack, GeoBone bone, MultiBufferSource bufferSource) {
        if (bone.getName().equals("eyes")) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(EYES_TEXTURE));

            if (bone.isHidden())
                return false;

            for (GeoCube cube : bone.getCubes()) {
                poseStack.pushPose();
                renderCube(poseStack, cube, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(255, 255, 255, 255));
                poseStack.popPose();
            }

            return true;
        }

        return false;
    }
}
