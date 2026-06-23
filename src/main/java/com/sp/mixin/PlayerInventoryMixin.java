package com.sp.mixin;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class PlayerInventoryMixin {

    @Inject(method = "swapPaint", at = @At("HEAD"))
    private void isScrolling(double scrollAmount, CallbackInfo ci){
        Minecraft client = Minecraft.getInstance();

        if(client.player != null){
            PlayerComponent component = InitializeComponents.PLAYER.get(client.player);
            component.setScrollingInInventoryTime(component.getScrollingInInventoryTime() + 1);
        }

    }

}
