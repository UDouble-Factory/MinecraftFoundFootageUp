package com.sp.mixin.pbr;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.sp.mixininterfaces.BlockMaterial;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SectionCompiler.class)
public class RebuildTaskMixin {

    @Inject(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;)V"))
    private void setCurrentBlock(CallbackInfoReturnable<SectionCompiler.Results> cir, @Local BufferBuilder bufferBuilder, @Local(ordinal = 0) BlockState blockState) {
        if (bufferBuilder instanceof BlockMaterial) {
            ((BlockMaterial) bufferBuilder).setCurrentBlock(blockState.getBlock());
        }
    }

    @Inject(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;)V", shift = At.Shift.AFTER))
    private void setCurrentBlock2(CallbackInfoReturnable<SectionCompiler.Results> cir, @Local BufferBuilder bufferBuilder) {
        if (bufferBuilder instanceof BlockMaterial) {
            ((BlockMaterial) bufferBuilder).setCurrentBlock(null);
        }
    }
}
