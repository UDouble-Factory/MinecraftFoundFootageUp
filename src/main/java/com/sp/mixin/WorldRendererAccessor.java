package com.sp.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface WorldRendererAccessor {

    @Invoker("renderSectionLayer")
    void invokeRenderLayer(RenderType renderType, double d, double e, double f, Matrix4f matrix4f, Matrix4f matrix4f2);

    @Invoker("setupRender")
    void invokeSetupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator);

    @Invoker("renderEntity")
    void invokeRenderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers);

    @Accessor("cullingFrustum")
    Frustum getFrustum();

    @Accessor("entityRenderDispatcher")
    EntityRenderDispatcher getEntityRenderDispatcher();

    @Accessor("cullingFrustum")
    void setFrustum(Frustum frustum);

    @Accessor("renderBuffers")
    RenderBuffers getBufferBuilders();
}
