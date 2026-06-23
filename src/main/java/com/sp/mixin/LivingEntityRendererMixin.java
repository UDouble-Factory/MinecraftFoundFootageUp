package com.sp.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SmilerComponent;
import com.sp.entity.custom.SmilerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    private LivingEntity entity;

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void getEntity(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci){
        this.entity = livingEntity;
    }

    @ModifyConstant(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            constant = {
                @Constant(floatValue = 1.0f, ordinal = 3),
                @Constant(floatValue = 1.0f, ordinal = 4),
                @Constant(floatValue = 1.0f, ordinal = 5),
                @Constant(floatValue = 1.0f, ordinal = 6)
            })
    private float setOpacity(float constant){
        if(entity instanceof SmilerEntity) {
            SmilerComponent component = InitializeComponents.SMILER.get(entity);
            return component.getOpacity();
        }

        return constant;
    }

}
