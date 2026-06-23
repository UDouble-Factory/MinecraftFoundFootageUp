package com.sp.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sp.SPBRevampedClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {

    //@ModifyVariable(method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("STORE"), ordinal = 1, argsOnly = true)
//    private float injected(float value){
//        return 0;
//    }

    @ModifyArg(method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"), index = 1)
    private float injected(float value) {
        if (!SPBRevampedClient.shouldRenderCameraEffect()) {
            return value;
        }
        return 0.03f;
    }

    @ModifyArg(method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotation(F)Lorg/joml/Quaternionf;"), index = 0)
    private float injected2(float value) {
        if (!SPBRevampedClient.shouldRenderCameraEffect()) {
            return value;
        }
        return 0;
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V"))
    private void makeFlat(ItemEntity itemEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            float groundRotation = itemEntity.getYRot();

            matrixStack.mulPose(Axis.XP.rotationDegrees(90));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(groundRotation + 180.0f));
        }
    }

}
