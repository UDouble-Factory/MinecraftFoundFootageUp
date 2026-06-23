package com.sp.mixin.skinstolen;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.client.SkinWalkerCapturedFlavorText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 1))
    private void disableInventory(Minecraft instance, Screen screen){
        if(instance.player != null) {
            PlayerComponent component = InitializeComponents.PLAYER.get(instance.player);
            if (!component.hasBeenCaptured()) {
                instance.setScreen(screen);
            } else {
                SkinWalkerCapturedFlavorText.triedToOpenInventory = true;
            }
        }
    }
}
