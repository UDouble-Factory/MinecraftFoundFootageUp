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
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void bobView(PoseStack matrices, float tickDelta){
        if (this.minecraft.getCameraEntity() instanceof Player) {
            Player playerEntity = (Player)this.minecraft.getCameraEntity();
            float f = playerEntity.walkDist - playerEntity.walkDistO;
            float g = -(playerEntity.walkDist + f * tickDelta);
            float h = Mth.lerp(tickDelta, playerEntity.oBob, playerEntity.bob);

            Vector3f cameraBob = new Vector3f(Mth.sin(g * (float) Math.PI) * h * 0.5F, -Math.abs(Mth.cos(g * (float) Math.PI) * h), 0.0F);
            matrices.translate(cameraBob.x, cameraBob.y, cameraBob.z);
            SPBRevampedClient.cameraBobOffset = new Vector3f(cameraBob);

            matrices.mulPose(Axis.ZP.rotationDegrees(Mth.sin(g * (float) Math.PI) * h * 3.0F));
            float multiplier = 5.0f;
            if (ConfigStuff.enableRealCamera) {
                multiplier = 10.0f;
            }
            matrices.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(g * (float) Math.PI - 0.2F) * h) * multiplier));
        }
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
