package com.sp.mixin;

import com.sp.SPBRevampedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public class DisableJumpMixin {

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/KeyboardInput;jumping:Z", opcode = Opcodes.PUTFIELD))
    private void disableJumping(KeyboardInput instance, boolean value){
        Player player = Minecraft.getInstance().player;

        if(player != null) {
            if(player.isInWater() || player.isCreative() || player.isSpectator() || !SPBRevampedClient.isInBackrooms()) {
                instance.jumping = value;
            } else {
                instance.jumping = false;
            }
        } else {
            instance.jumping = false;
        }
    }

}
