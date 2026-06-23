package com.sp.networking.S2C;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;

public class SoundPacket {

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender){
        Holder<SoundEvent> sound =  buf.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEvent::readFromNetwork);
        float volume = buf.readFloat();
        float pitch = buf.readFloat();

        client.execute(()->{
            client.getSoundManager().play(SimpleSoundInstance.forUI(sound.value(), pitch, volume));
        });
    }

}
