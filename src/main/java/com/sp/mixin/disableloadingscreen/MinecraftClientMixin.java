package com.sp.mixin.disableloadingscreen;

import com.sp.SPBRevampedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Redirect(method = "setLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ProgressScreen;progressStartNoAbort(Lnet/minecraft/network/chat/Component;)V"))
    private void doNothing(ProgressScreen instance, Component title){
        if (SPBRevampedClient.isInBackrooms()) {
            return;
        }

        instance.progressStartNoAbort(Component.translatable("connect.joining"));
    }
}
