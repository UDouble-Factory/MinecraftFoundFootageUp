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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
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
    private static final ResourceLocation warpEntity = new ResourceLocation("spbrevamped", "warp_player");

    @Unique
    private float smoothPitch = 0.0f;

    @Unique
    private float smoothYaw = 0.0f;

    @Shadow @Final Minecraft minecraft;


    @Shadow public abstract void setRenderBlockOutline(boolean blockOutlineEnabled);
    @Shadow public abstract void tick();
    @Shadow protected abstract void renderItemInHand(PoseStack matrices, Camera camera, float tickDelta);

    @Shadow private static @Nullable ShaderInstance rendertypeEntityTranslucentShader;

    @Shadow public abstract void render(float tickDelta, long startTime, boolean tick);

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;bobHurt(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    public void renderWorld(float tickDelta, long limitTime, PoseStack matrices, CallbackInfo ci) {
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            Player player = this.minecraft.player;
            this.setRenderBlockOutline(true);

            if (player != null) {
                CutsceneManager cutsceneManager = SPBRevampedClient.getCutsceneManager();

                if (ConfigStuff.enableRealCamera && !cutsceneManager.isPlaying && minecraft.getCameraEntity() == player) {
                    matrices.mulPose(Axis.ZP.rotationDegrees(CameraRoll.doCameraRoll(player, tickDelta)));
                } else if (cutsceneManager.isPlaying) {
                    matrices.mulPose(Axis.YP.rotationDegrees(cutsceneManager.cameraRotZ));
                }
            }
        }
    }

    @ModifyArg(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 2))
    private float smoothPitch(float deg) {
        if (!SPBRevampedClient.shouldRenderCameraEffect()) {
            return deg;
        }

        Player player = this.minecraft.player;

        if(player != null && ConfigStuff.enableSmoothCamera && minecraft.options.getCameraType() == CameraType.FIRST_PERSON){
            this.smoothYaw = MathStuff.Lerp(this.smoothYaw, deg, ConfigStuff.cameraSmoothing, minecraft.getDeltaFrameTime());
            return this.smoothYaw;
        } else {
            this.smoothYaw = deg;
        }

        return deg;
    }

    @ModifyArg(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 3))
    private float smoothYaw(float deg) {
        if (!SPBRevampedClient.shouldRenderCameraEffect()) {
            return deg;
        }

        Player player = this.minecraft.player;

        if(player != null && ConfigStuff.enableSmoothCamera && minecraft.options.getCameraType() == CameraType.FIRST_PERSON){
            this.smoothPitch = MathStuff.Lerp(this.smoothPitch, deg, ConfigStuff.cameraSmoothing, minecraft.getDeltaFrameTime());
            return this.smoothPitch;
        } else {
            this.smoothPitch = deg;
        }

        return deg;
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

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;F)V"))
    private void redirect(GameRenderer instance, PoseStack matrices, Camera camera, float tickDelta) {
        if(!SPBRevampedClient.getCutsceneManager().isPlaying){
            this.renderItemInHand(matrices, camera, tickDelta);
        }
    }

    @Redirect(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPickRange()F"))
    private float increaseReach(MultiPlayerGameMode instance){
        return 6;
    }

    @ModifyConstant(method = "pick", constant = @Constant(doubleValue = 9.0))
    private double increaseReach(double constant){
        return 36;
    }
}
