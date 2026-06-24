package com.sp.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sp.SPBRevamped;
import com.sp.SPBRevampedClient;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.render.ShadowMapRenderer;
import com.sp.render.camera.CameraRoll;
import com.sp.render.camera.CutsceneManager;
import com.sp.util.MathStuff;
import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = GameRenderer.class)
public abstract class GameRendererMixin {
    @Unique
    Entity newCamera;
    @Unique
    private static final ResourceLocation shadowSolid = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "shadowmap/rendertype_solid");

    @Unique
    private static final ResourceLocation shadowEntity = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "shadowmap/rendertype_entity");

    @Unique
    private static final ResourceLocation warpEntity = ResourceLocation.fromNamespaceAndPath("spbrevamped", "warp_player");

    @Unique
    private float smoothPitch = 0.0f;

    @Unique
    private float smoothYaw = 0.0f;

    @Shadow @Final Minecraft minecraft;

    @Shadow public abstract void setRenderBlockOutline(boolean blockOutlineEnabled);
    @Shadow public abstract void tick();

    @Shadow private static @Nullable ShaderInstance rendertypeEntityTranslucentShader;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;bobHurt(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    public void renderWorld(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            Player player = this.minecraft.player;
            this.setRenderBlockOutline(true);
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;rotation()Lorg/joml/Quaternionf;"))
    private void applyCameraEffects(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            Player player = this.minecraft.player;
            float tickDelta = deltaTracker.getGameTimeDeltaPartialTick(true);

            if (player != null) {
                CutsceneManager cutsceneManager = SPBRevampedClient.getCutsceneManager();

                if (ConfigStuff.enableSmoothCamera && !cutsceneManager.isPlaying && minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
                    float xRot = player.getViewXRot(tickDelta);
                    float yRot = player.getViewYRot(tickDelta) + 180.0f;
                    this.smoothPitch = MathStuff.Lerp(this.smoothPitch, xRot, ConfigStuff.cameraSmoothing, minecraft.getTimer().getRealtimeDeltaTicks());
                    this.smoothYaw = MathStuff.Lerp(this.smoothYaw, yRot, ConfigStuff.cameraSmoothing, minecraft.getTimer().getRealtimeDeltaTicks());
                }
            }
        }
    }

    @ModifyVariable(method = "renderLevel", at = @At(value = "STORE"), ordinal = 1)
    private Matrix4f modifyViewMatrix(Matrix4f matrix4f2) {
        if (!SPBRevampedClient.shouldRenderCameraEffect()) {
            return matrix4f2;
        }

        Player player = this.minecraft.player;
        if (player != null) {
            CutsceneManager cutsceneManager = SPBRevampedClient.getCutsceneManager();
            float tickDelta = minecraft.getTimer().getGameTimeDeltaPartialTick(true);

            if (ConfigStuff.enableRealCamera && !cutsceneManager.isPlaying && minecraft.getCameraEntity() == player) {
                matrix4f2.rotate(Axis.ZP.rotationDegrees(CameraRoll.doCameraRoll(player, tickDelta)));
            } else if (cutsceneManager.isPlaying) {
                matrix4f2.rotate(Axis.YP.rotationDegrees(cutsceneManager.cameraRotZ));
            }

            if (ConfigStuff.enableSmoothCamera && !cutsceneManager.isPlaying && minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
                Quaternionf smoothRotation = new Quaternionf();
                smoothRotation.rotationYXZ(
                        (float) Math.toRadians(this.smoothYaw),
                        (float) Math.toRadians(this.smoothPitch),
                        0.0f
                );
                matrix4f2.set(new Matrix4f().rotation(smoothRotation.conjugate()));
            }
        }

        return matrix4f2;
    }

    /// Why are we doing this. Why are we overwriting this method? Space please tell me? best of wishes -Chaos

    /// Dearest Chaos, Becasue I can -SpacePotato

    /// Don't worry guys, I fixed them -Mr.W

    @ModifyArgs(
            method = "bobView",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V")
    )
    private void spb$captureBobOffset(Args args) {
        float x = args.get(0);
        float y = args.get(1);
        float z = args.get(2);
        SPBRevampedClient.cameraBobOffset = new Vector3f(x, y, z);
    }

    @Redirect(
            method = "bobView",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 1)
    )
    private void spb$modifyXRotation(PoseStack poseStack, Quaternionf original) {
        if (ConfigStuff.enableRealCamera) {
            Player player = (Player) this.minecraft.getCameraEntity();
            float f = player.walkDist - player.walkDistO;
            float g = -(player.walkDist + f * spb$lastBobTickDelta);
            float h = Mth.lerp(spb$lastBobTickDelta, player.oBob, player.bob);
            poseStack.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(g * (float) Math.PI - 0.2F) * h) * 10.0F));
        } else {
            poseStack.mulPose(original);
        }
    }

    @Unique private float spb$lastBobTickDelta;

    @Inject(method = "bobView", at = @At("HEAD"))
    private void spb$captureBobTickDelta(PoseStack poseStack, float tickDelta, CallbackInfo ci) {
        this.spb$lastBobTickDelta = tickDelta;
    }


    @Inject(method = {
            "getRendertypeSolidShader",
            "getRendertypeCutoutShader",
            "getRendertypeCutoutMippedShader"
    }, at = @At("HEAD"), cancellable = true)
    private static void setSolidShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if(ShadowMapRenderer.isRenderingShadowMap()) {
            foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.renderer().getShaderManager().getShader(shadowSolid);
            if (shader == null) {
                return;
            }
            cir.setReturnValue(shader.toShaderInstance());
        }
    }

    @Inject(method = {
            "getRendertypeEntityTranslucentShader",
            "getRendertypeEntitySolidShader",
            "getRendertypeEntityCutoutShader",
            "getRendertypeEntityCutoutNoCullShader",
            "getRendertypeEntityTranslucentCullShader"
    }, at = @At("TAIL"), cancellable = true)
    private static void setPlayerShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if(ShadowMapRenderer.isRenderingShadowMap()) {
            foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.renderer().getShaderManager().getShader(shadowEntity);
            if (shader == null) {
                return;
            }
            cir.setReturnValue(shader.toShaderInstance());
        }
    }

    @Inject(method = {
            "getRendertypeEntityTranslucentShader"
    }, at = @At("TAIL"), cancellable = true)
    private static void setPlayerWarpShader(CallbackInfoReturnable<ShaderInstance> cir) {
        foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.renderer().getShaderManager().getShader(warpEntity);
        if (shader == null || !SPBRevampedClient.shouldRenderWarp) {
            cir.setReturnValue(rendertypeEntityTranslucentShader);
            return;
        }
        cir.setReturnValue(shader.toShaderInstance());
    }

    @Shadow private void renderItemInHand(Camera camera, float f, Matrix4f matrix4f) {}

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V"))
    private void redirectRenderItemInHand(GameRenderer instance, Camera camera, float tickDelta, Matrix4f matrix4f) {
        if(!SPBRevampedClient.getCutsceneManager().isPlaying){
            this.renderItemInHand(camera, tickDelta, matrix4f);
        }
    }

    @Redirect(method = "pick(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;blockInteractionRange()D"))
    private double increaseBlockReach(net.minecraft.client.player.LocalPlayer instance){
        return 6.0;
    }

    @Redirect(method = "pick(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;entityInteractionRange()D"))
    private double increaseEntityReach(net.minecraft.client.player.LocalPlayer instance){
        return 6.0;
    }
}
