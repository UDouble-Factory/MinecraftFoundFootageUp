package com.sp.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevampedClient;
import com.sp.render.pbr.BlockIdMap;
import com.sp.world.levels.BackroomsLevel;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11C.*;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 9, shift = At.Shift.AFTER))
    private void wireFrame(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci){
        Minecraft client = Minecraft.getInstance();
        if(client.wireframe && FabricLoader.getInstance().isDevelopmentEnvironment()) {
            RenderSystem.polygonMode(GL_FRONT, GL_LINE);
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal = 2, shift = At.Shift.BEFORE))
    private void wireFrame2(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci){
        Minecraft client = Minecraft.getInstance();
        if(client.wireframe) {
            RenderSystem.polygonMode(GL_FRONT, GL_FILL);
        }
    }


    @Inject(method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    public void renderSky(Matrix4f matrix4f, Matrix4f matrix4f2, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        if (!SPBRevampedClient.getCurrentBackroomsLevel().map(BackroomsLevel::rendersSky).orElse(true)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void initBlockIDs(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        if (!BlockIdMap.init) {
            BlockIdMap.init();
        }
    }

    @Inject(method = "renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
    public void renderClouds(com.mojang.blaze3d.vertex.PoseStack poseStack, Matrix4f matrix4f, Matrix4f matrix4f2, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (!SPBRevampedClient.getCurrentBackroomsLevel().map(BackroomsLevel::rendersClouds).orElse(true)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 10, shift = At.Shift.AFTER))
    public void shouldRenderWarp(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci){
        SPBRevampedClient.shouldRenderWarp = true;
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 11, shift = At.Shift.BEFORE))
    public void shouldStopRenderingWarp(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci){
        SPBRevampedClient.shouldRenderWarp = false;
    }


    @Redirect(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    private void stopTravelSound(SoundManager instance, SoundInstance sound){

    }

}
