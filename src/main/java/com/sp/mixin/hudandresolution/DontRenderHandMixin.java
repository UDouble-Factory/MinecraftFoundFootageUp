package com.sp.mixin.hudandresolution;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sp.compat.modmenu.ConfigStuff;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
public abstract class DontRenderHandMixin {

    @Shadow protected abstract void renderPlayerArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float equipProgress, float swingProgress, HumanoidArm arm);

    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderPlayerArm(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IFFLnet/minecraft/world/entity/HumanoidArm;)V"))
    private void cancel(ItemInHandRenderer instance, PoseStack matrices, MultiBufferSource vertexConsumers, int light, float equipProgress, float swingProgress, HumanoidArm arm){
        if (!ConfigStuff.showHands) {
            return;
        }

        this.renderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
    }
}
