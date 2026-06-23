package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class ReloadLightsPacket {

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender){

        client.execute(()->{
            VeilRenderer renderer = VeilRenderSystem.renderer();
            renderer.getDeferredRenderer().getLightRenderer().free();

            if(SPBRevampedClient.getCutsceneManager().started) {
                SPBRevampedClient.getCutsceneManager().blackScreen.showBlackScreen(60, true, false);
            }
        });
    }

}
