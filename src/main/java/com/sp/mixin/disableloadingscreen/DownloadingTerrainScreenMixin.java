package com.sp.mixin.disableloadingscreen;

import com.sp.SPBRevampedClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReceivingLevelScreen.class)
public class DownloadingTerrainScreenMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void removeScreen(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci){
        if (SPBRevampedClient.isInBackrooms()) {
            ci.cancel();
        }
    }
}
