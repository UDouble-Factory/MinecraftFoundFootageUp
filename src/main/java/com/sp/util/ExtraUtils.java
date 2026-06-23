package com.sp.util;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;

public class ExtraUtils {

    public static void stopAllOtherSounds(ResourceLocation id, SoundEngine soundSystem){
        for (SoundInstance soundInstance : soundSystem.instanceBySource.values()) {
            if (!soundInstance.getLocation().equals(id)) {
                soundSystem.stop(soundInstance);
            }
        }
    }

}
