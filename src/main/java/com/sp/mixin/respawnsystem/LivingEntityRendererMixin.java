package com.sp.mixin.respawnsystem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.util.Timer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easings;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin <T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    @Unique Timer staticTimer;
    @Shadow public abstract M getModel();

    @Shadow protected M model;

    @Shadow protected abstract void scale(T entity, PoseStack matrices, float amount);
    @Unique ShaderProgram shader;
    @Unique ResourceLocation SHADER_LOCATION = new ResourceLocation("spbrevamped", "warp_player");

    protected LivingEntityRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;", shift = At.Shift.BEFORE))
    private void setShaderAndUniforms(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci){
        if (livingEntity instanceof AbstractClientPlayer){
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(livingEntity);

            shader = VeilRenderSystem.setShader(SHADER_LOCATION);
            if(shader == null){
                return;
            }

            if(playerComponent.isShouldDoStatic()) {
                if (this.staticTimer == null) {
                    this.staticTimer = new Timer(2000, Easings.Easing.linear);
                    this.staticTimer.startTimer();
                }

                if(this.staticTimer.isDone()){
                    this.staticTimer = null;
                    shader.setFloat("StaticTimer", 2.0f);
                } else {
                    shader.setFloat("StaticTimer", this.staticTimer.getCurrentTime());
                }

            } else {
                shader.setFloat("StaticTimer", 2.0f);
            }

        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", shift = At.Shift.BEFORE))
    private void bind(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci){
        if (livingEntity instanceof AbstractClientPlayer){
            shader.bind();
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))
    private void unbind(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci){
        if(livingEntity instanceof AbstractClientPlayer){
            ShaderProgram.unbind();
        }
    }

}
