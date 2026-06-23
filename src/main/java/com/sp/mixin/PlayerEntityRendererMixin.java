package com.sp.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "setModelProperties", at = @At("TAIL"))
    private void shouldRender(AbstractClientPlayer player, CallbackInfo ci, @Local PlayerModel<AbstractClientPlayer> playerEntityModel){
        if(SPBRevampedClient.getCutsceneManager().isPlaying) {
            playerEntityModel.setAllVisible(false);
        }

        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
        if (!playerComponent.isShouldRender()) {
            playerEntityModel.setAllVisible(false);
        }
    }

}
