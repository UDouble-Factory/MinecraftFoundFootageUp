package com.sp.entity.client.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.util.PrAnCommonClass;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class IKDebugRenderLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
    ///summon projectnublar:tyrannosaurus_rex ~ ~ ~ {NoAI:1b}

    public static int getArgb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public IKDebugRenderLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!PrAnCommonClass.shouldRenderDebugLegs || !(animatable instanceof IKAnimatable ikAnimatable)) {
            return;
        }

        ikAnimatable.renderDebug(poseStack, ikAnimatable, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

}
