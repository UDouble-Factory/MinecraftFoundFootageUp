package com.sp.mixin.disableloadingscreen;

import com.sp.SPBRevampedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow protected abstract void updateScreenAndTick(Screen screen);

    @Redirect(method = "setLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;updateScreenAndTick(Lnet/minecraft/client/gui/screens/Screen;)V"))
    private void doNothing(Minecraft instance, Screen screen){
        if (SPBRevampedClient.isInBackrooms()) {
            return;
        }

        updateScreenAndTick(screen);
    }
}
