package com.sp.mixin.renderlayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sp.render.RenderLayers;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow protected abstract void renderChunkLayer(RenderType renderLayer, PoseStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix);

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V", ordinal = 0))
    private void renderRenderLayer(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci){
        Vec3 cameraPos = camera.getPosition();
        this.renderChunkLayer(RenderLayers.getPoolroomsSky(), matrices, cameraPos.x(), cameraPos.y(), cameraPos.z(), projectionMatrix);
        this.renderChunkLayer(RenderLayers.getPbrLayer(), matrices, cameraPos.x(), cameraPos.y(), cameraPos.z(), projectionMatrix);
    }

}
