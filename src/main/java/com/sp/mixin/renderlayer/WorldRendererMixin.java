package com.sp.mixin.renderlayer;

import com.sp.render.RenderLayers;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
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

    @Shadow private void renderSectionLayer(RenderType renderType, double d, double e, double f, Matrix4f matrix4f, Matrix4f matrix4f2) {}

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", ordinal = 0))
    private void renderRenderLayer(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci){
        Vec3 cameraPos = camera.getPosition();
        this.renderSectionLayer(RenderLayers.getPoolroomsSky(), cameraPos.x(), cameraPos.y(), cameraPos.z(), matrix4f, matrix4f2);
        this.renderSectionLayer(RenderLayers.getPbrLayer(), cameraPos.x(), cameraPos.y(), cameraPos.z(), matrix4f, matrix4f2);
    }

}
