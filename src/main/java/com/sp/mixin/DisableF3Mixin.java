package com.sp.mixin;

import com.sp.SPBRevampedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenOverlay.class)
public class DisableF3Mixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void disableF3(CallbackInfo ci){
        Player player = Minecraft.getInstance().player;

        if(player != null) {
            if (!player.isCreative() && !player.isSpectator() && SPBRevampedClient.isInBackrooms()) {
                ci.cancel();
            }
        }
    }

}
