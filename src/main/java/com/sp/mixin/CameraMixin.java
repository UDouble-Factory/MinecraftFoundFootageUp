package com.sp.mixin;

import com.sp.SPBRevampedClient;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {

    @Inject(method = "setup", at = @At("TAIL"))
    private void cameraShake(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            SPBRevampedClient.getCameraShake().tick((Camera) (Object) this);
        }
    }

}
