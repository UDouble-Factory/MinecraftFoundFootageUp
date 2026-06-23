package com.sp.entity.ik.components.debug_renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sp.entity.custom.WalkerEntity;
import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKLegComponent;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.ik_chains.EntityLeg;
import com.sp.entity.ik.parts.ik_chains.EntityLegWithFoot;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.entity.ik.util.MathUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class WalkerLegDebugRenderer<E extends IKAnimatable<E>, C extends IKChain> extends IKChainDebugRenderer<E, IKLegComponent<C, E>> {
    @Override
    public void renderDebug(IKLegComponent<C, E> component, E animatable, PoseStack poseStack, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.renderDebug(component, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        if (!(animatable instanceof WalkerEntity entity)) {
            return;
        }


        double hitAmount = 0;

        Vec3 hitAverage = new Vec3(0.0, 0.0, 0.0);

        List<Vec3> testPositions = new ArrayList<>();



        testPositions.add(new Vec3(1, 0, 0).normalize());
        testPositions.add(new Vec3(-1, 0, 0).normalize());

        testPositions.add(new Vec3(1, 1, 0).normalize());
        testPositions.add(new Vec3(-1, -1, 0).normalize());
        testPositions.add(new Vec3(1, -1, 0).normalize());
        testPositions.add(new Vec3(-1, 1, 0).normalize());

        testPositions.add(new Vec3(1, 1, 1).normalize());
        testPositions.add(new Vec3(-1, -1, -1).normalize());
        testPositions.add(new Vec3(1, 1, -1).normalize());
        testPositions.add(new Vec3(-1, -1, 1).normalize());

        testPositions.add(new Vec3(1, -1, -1).normalize());
        testPositions.add(new Vec3(-1, 1, 1).normalize());
        testPositions.add(new Vec3(1, -1, 1).normalize());
        testPositions.add(new Vec3(-1, 1, -1).normalize());

        testPositions.add(new Vec3(0, 1, 0).normalize());
        testPositions.add(new Vec3(0, -1, 0).normalize());

        testPositions.add(new Vec3(0, 1, 1).normalize());
        testPositions.add(new Vec3(0, -1, -1).normalize());
        testPositions.add(new Vec3(0, 1, -1).normalize());
        testPositions.add(new Vec3(0, -1, 1).normalize());


        testPositions.add(new Vec3(0, 0, 1).normalize());
        testPositions.add(new Vec3(0, 0, -1).normalize());

        testPositions.add(new Vec3(1, 0, 1).normalize());
        testPositions.add(new Vec3(-1, 0, -1).normalize());
        testPositions.add(new Vec3(-1, 0, 1).normalize());
        testPositions.add(new Vec3(1, 0, -1).normalize());

        for (Vec3 testPosition : testPositions) {
            Vec3 targetPos = entity.position().add(testPosition.scale(WalkerEntity.TILTING_TEST_RANGE));

            boolean hit = entity.level().clip(new ClipContext(entity.position(), targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.BLOCK;

            if (hit) {
                hitAverage = hitAverage.add(testPosition);
                hitAmount++;
            }

            IKDebugRenderer.drawLine(poseStack, bufferSource, entity.position(), entity.position(), targetPos, hit ? 0 : 255, hit ? 255 : 0, 0, 127);
        }

        hitAverage = new Vec3(hitAverage.x / hitAmount, hitAverage.y / hitAmount, hitAverage.z / hitAmount);

        Vec3 entityPos = entity.position();

        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.position().add(entity.getRightDirection()), 255, 0, 0, 127);
        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.position().add(entity.getRightDirection().reverse()), 255, 0, 0, 127);

        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.position().add(entity.getUpStationaryDirection()), 255, 0, 0, 127);
        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.position().add(entity.getUpStationaryDirection().reverse()), 255, 0, 0, 127);

        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.position().add(entity.getLookAngle()), 255, 0, 0, 127);

        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.position().add(hitAverage.normalize()), 0, 0, 255, 127);
        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.position().add(entity.getUpDirection().normalize()), 0, 125, 125, 127);


        for (C limb : component.getLimbs()) {
            //renderLeg(poseStack, bufferSource, limb, entity);

            for (ServerLimb endPoint : component.getEndPoints()) {

                Vec3 limbOffset = Vec3.ZERO;
                Vec3 limbOffsetMultiplier = endPoint.baseOffset.scale(component.getScale());

                limbOffset = limbOffset.add(entity.getUpDirection().cross(entity.getLookAngle()).scale(limbOffsetMultiplier.x));

                limbOffset = limbOffset.add(entity.getUpDirection().scale(limbOffsetMultiplier.y));

                limbOffset = limbOffset.add(entity.getLookAngle().scale(limbOffsetMultiplier.z));

                if (component.hasMovedOverLastTick(entity)) {
                    limbOffset = limbOffset.add(0, 0, component.getSettings().get(0).stepInFront() * component.getScale());
                }

                Vec3 rotatedLimbOffset = limbOffset.add(entity.position());
                Vec3 upPoint = rotatedLimbOffset.add(entity.getUpDirection().scale(1));
                HitResult baseRayCastResult = entity.level().clip(new ClipContext(upPoint, rotatedLimbOffset.add(entity.getUpDirection().scale(-10)), ClipContext.Block.COLLIDER, component.getSettings().get(0).fluid(), entity));

                Vec3 bestHit = baseRayCastResult.getLocation();
                double bestDistance = baseRayCastResult.getType() == HitResult.Type.MISS ? Double.MAX_VALUE : 0.5;

                List<Vec3> upDirs = new ArrayList<>();
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(0, 1, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(0, -1, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(1, 0, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(-1, 0, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(-0.5, 0.5, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(0.5, 0.5, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(-0.5, -0.5, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(0.5, -0.5, -2).normalize())));

                for (Vec3 upDir : upDirs) {
                    BlockHitResult rayCastResult = entity.level().clip(new ClipContext(upPoint, upPoint.add(upDir.scale(-10)), ClipContext.Block.COLLIDER, component.getSettings().get(0).fluid(), entity));
                    IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, upPoint, rayCastResult.getLocation(), 0, 0, 255, 127);

                    if (rayCastResult.getType() == BlockHitResult.Type.MISS) {
                        continue;
                    }

                    if (rayCastResult.getLocation().distanceToSqr(baseRayCastResult.getLocation()) < bestDistance) {
                        bestDistance = rayCastResult.getLocation().distanceToSqr(baseRayCastResult.getLocation());
                        bestHit = rayCastResult.getLocation();
                    }
                }

                IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, upPoint, bestHit, 255, 0, 0, 127);

                double distance = endPoint.target.distanceTo(bestHit);

                if (distance < 0.1) distance = 0;

                if (distance != 0) {
                    IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, endPoint.getPos(), endPoint.target, 255, 100, 255, 127);
                }

                IKDebugRenderer.drawBox(poseStack, bufferSource, endPoint.getPos(), entity, endPoint.isGrounded() ? 0 : 255, endPoint.isGrounded() ? 255 : 0, 0, 127);
                IKDebugRenderer.drawBox(poseStack, bufferSource, endPoint.oldTarget, entity, 0, 255, 255, 127);
                IKDebugRenderer.drawBox(poseStack, bufferSource, bestHit, entity, 0, 0, 255, 127);
            }
        }
    }

    private void renderLeg(PoseStack poseStack, MultiBufferSource bufferSource, C chain, Entity entity) {
        Vec3 entityPos = entity.position();

        for (int i = 0; i < chain.getJoints().size() - 1; i++) {
            if (i > 0) {
                this.drawAngleConstraints(i, chain, entity, poseStack, bufferSource);
                continue;
            }
            this.drawAngleConstraintsForBase(chain, entity, poseStack, bufferSource);
        }

        if (chain instanceof EntityLegWithFoot entityLegWithFoot) {
            Vec3 footPos = entityLegWithFoot.foot.getPosition();
            IKDebugRenderer.drawLineToBox(poseStack, bufferSource, entityPos, chain.endJoint, footPos, entity, 255, 165, 0, 127);

            Vec3 angleConstraint = entityLegWithFoot.getFootPosition(entityLegWithFoot.foot.angleSize);

            Vec3 referencePoint = entityLegWithFoot.getFootPosition(0);

            IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, chain.endJoint, angleConstraint, 255, 0, 0, 127);
            IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, chain.endJoint, referencePoint, 0, 255, 0, 127);
        }
    }

    private void drawAngleConstraintsForBase(C chain, Entity entity, PoseStack matrices, MultiBufferSource vertexConsumers) {
        if (!(chain instanceof EntityLeg entityLeg)) {
            return;
        }
        Vec3 entityPos = entity.position();

        Vec3 base = entityLeg.getFirst().getPosition();

        Vec3 referencePoint = entityLeg.rotatePointOnLegPlane(base.add(entityLeg.getDownNormalOnLegPlane()), base, chain.getFirst().angleOffset);

        Vec3 dotBaseDir = referencePoint.subtract(base).normalize();
        Vec3 dotTargetDir = chain.get(1).getPosition().subtract(base).normalize();

        double angle = Math.toDegrees(Math.acos(dotBaseDir.dot(dotTargetDir)));

        double angleDifference = chain.getFirst().angleSize - angle;

        Vec3 rotatedPos = MathUtil.rotatePointOnAPlaneAround(chain.getFirst().getPosition().add(entityLeg.getDownNormalOnLegPlane()), chain.getFirst().getPosition(), chain.getFirst().angleSize, entityLeg.getLegPlane());
        Vec3 rotatedPos2 = MathUtil.rotatePointOnAPlaneAround(chain.getFirst().getPosition().add(entityLeg.getDownNormalOnLegPlane()), chain.getFirst().getPosition(), -chain.getFirst().angleSize, entityLeg.getLegPlane());
        Vec3 newPos = MathUtil.rotatePointOnAPlaneAround(chain.get(1).getPosition(), chain.getFirst().getPosition(), angleDifference, entityLeg.getLegPlane());

        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), rotatedPos, 255, 0, 0, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), rotatedPos2, 0, 255, 0, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), newPos, 0, 0, 255, 127);

        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), chain.getFirst().getPosition().add(entityLeg.getDownNormalOnLegPlane()), 180, 180, 180, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), chain.getFirst().getPosition().add(entityLeg.getLegPlane()), 12, 12, 12, 127);
    }

    private void drawAngleConstraints(int i, C chain, Entity entity, PoseStack matrices, MultiBufferSource vertexConsumers) {
        if (!(chain instanceof EntityLeg entityLeg)) {
            return;
        }

        Vec3 entityPos = entity.position();

        Segment currentSegment = chain.get(i);

        List<Vec3> positions = this.getConstrainedPositions(chain.get(i - 1).getPosition(), currentSegment, chain.getJoints().get(i + 1), entityLeg);

        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), positions.get(0), 255, 0, 0, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), positions.get(1), 180, 180, 180, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), positions.get(2), 0, 255, 0, 127);
    }

    private List<Vec3> getConstrainedPositions(Vec3 reference, Segment middle, Vec3 endpoint, EntityLeg chain) {
        //Vec3d normal = MathUtil.getClosestNormalRelativeToEntity(endpoint, middle.getPosition(), reference, entity);

        Vec3 normal = chain.getLegPlane();

        Vec3 referencePoint = MathUtil.rotatePointOnAPlaneAround(reference, middle.getPosition(), middle.angleOffset, normal);

        double angle = Math.toDegrees(MathUtil.calculateAngle(middle.getPosition(), endpoint, referencePoint));
        double angleDelta = middle.angleSize - angle;

        Vec3 newPos = MathUtil.rotatePointOnAPlaneAround(endpoint, middle.getPosition(), angleDelta, normal);
        Vec3 otherNewPos = MathUtil.rotatePointOnAPlaneAround(endpoint, middle.getPosition(), (angleDelta - (middle.angleSize * 2)), normal);
        Vec3 middlePos = MathUtil.rotatePointOnAPlaneAround(endpoint, middle.getPosition(), (angleDelta - middle.angleSize), normal);

        return List.of(newPos, middlePos, otherNewPos);
    }
}
