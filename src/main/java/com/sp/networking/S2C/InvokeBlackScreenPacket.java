package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class InvokeBlackScreenPacket {

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender){
        int duration = buf.readInt();
        boolean shouldPauseSounds = buf.readBoolean();
        boolean noEscape = buf.readBoolean();

        client.execute(()->{
            SPBRevampedClient.getCutsceneManager().blackScreen.showBlackScreen(duration, shouldPauseSounds, noEscape);
        });
    }

}
