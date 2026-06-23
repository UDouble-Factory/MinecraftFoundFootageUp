package com.sp.networking.S2C;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;

public class SoundPacket {
    public static void receive(SoundPayload payload, ClientPlayNetworking.Context context) {
        float volume = payload.volume();
        float pitch = payload.pitch();
        var sound = payload.sound();
        context.client().execute(() ->
            context.client().getSoundManager().play(SimpleSoundInstance.forUI(sound.value(), pitch, volume))
        );
    }
}
