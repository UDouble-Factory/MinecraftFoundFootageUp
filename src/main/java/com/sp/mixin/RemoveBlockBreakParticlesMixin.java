package com.sp.mixin;

import com.sp.SPBRevampedClient;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public class RemoveBlockBreakParticlesMixin {

    @Inject(method = "crack", at = @At("HEAD"), cancellable = true)
    private void cancel(BlockPos pos, Direction direction, CallbackInfo ci) {
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            ci.cancel();
        }
    }
}
