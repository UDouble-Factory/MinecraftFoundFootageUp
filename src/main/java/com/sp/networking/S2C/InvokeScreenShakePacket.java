package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class InvokeScreenShakePacket {

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender){
        double speed = buf.readDouble();
        double trauma = buf.readDouble();

        client.execute(()->{
            SPBRevampedClient.getCameraShake().noiseSpeed = speed;
            SPBRevampedClient.getCameraShake().trauma = trauma;
        });
    }

}
