package com.sp.mixin.soundphysicsremasteredcompat;

import com.llamalad7.mixinextras.sugar.Local;
import com.sonicether.soundphysics.config.AllowedSoundConfig;
import com.sp.init.ModSounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AllowedSoundConfig.class)
@Pseudo
public class AllowedSoundsConfigMixin {

    @Inject(method = "createDefaultMap", at = @At("RETURN"), remap = false)
    private void ignoreSoundPhysics(CallbackInfoReturnable<Map<String, Boolean>> cir, @Local Map<String, Boolean> map) {
        map.put(ModSounds.INTERCOM_BASIC1.getLocation().toString(), false);
        map.put(ModSounds.INTERCOM_BASIC2.getLocation().toString(), false);
        map.put(ModSounds.INTERCOM_FRIEND.getLocation().toString(), false);
        map.put(ModSounds.INTERCOM_REVERSED.getLocation().toString(), false);

        map.put(ModSounds.LIGHTS_OUT.getLocation().toString(), false);
        map.put(ModSounds.LIGHTS_ON.getLocation().toString(), false);
        map.put(ModSounds.LIGHT_BLINK.getLocation().toString(), false);

        map.put(ModSounds.CREEPY_MUSIC1.getLocation().toString(), false);
        map.put(ModSounds.CREEPY_MUSIC2.getLocation().toString(), false);
        map.put(ModSounds.FAR_CROWD.getLocation().toString(), false);

        map.put(ModSounds.LEVEL1_AMBIENCE1.getLocation().toString(), false);
        map.put(ModSounds.LEVEL1_AMBIENCE2.getLocation().toString(), false);
        map.put(ModSounds.LEVEL1_AMBIENCE3.getLocation().toString(), false);
        map.put(ModSounds.LEVEL1_AMBIENCE4.getLocation().toString(), false);

        map.put(ModSounds.SMILER_AMBIENCE.getLocation().toString(), false);
        map.put(ModSounds.SMILER_GLITCH.getLocation().toString(), false);

        map.put(ModSounds.CREAKING1.getLocation().toString(), false);
        map.put(ModSounds.CREAKING2.getLocation().toString(), false);
        map.put(ModSounds.LEVEL2_AMBIENCE.getLocation().toString(), false);
        map.put(ModSounds.AMBIENCE.getLocation().toString(), false);

        map.put(ModSounds.SKINWALKER_AMBIENCE.getLocation().toString(), false);
        map.put(ModSounds.SKINWALKER_BONE_CRACK.getLocation().toString(), false);
        map.put(ModSounds.SKINWALKER_CHASE.getLocation().toString(), false);
        map.put(ModSounds.SKINWALKER_FOOTSTEP.getLocation().toString(), false);
        map.put(ModSounds.SKINWALKER_NOTICE.getLocation().toString(), false);
        map.put(ModSounds.SKINWALKER_BONE_CRACK_LONG.getLocation().toString(), false);
        map.put(ModSounds.SKINWALKER_RELEASE.getLocation().toString(), false);
        map.put(ModSounds.SKINWALKER_REVEAL.getLocation().toString(), false);
        map.put(ModSounds.SKINWALKER_SNIFF.getLocation().toString(), false);

        map.put(ModSounds.POOLROOMS_SPLASH1.getLocation().toString(), false);
        map.put(ModSounds.POOLROOMS_SPLASH2.getLocation().toString(), false);
        map.put(ModSounds.POOLROOMS_DRIP1.getLocation().toString(), false);
        map.put(ModSounds.POOLROOMS_DRIP2.getLocation().toString(), false);

        map.put(ModSounds.MIDNIGHT_TRANSITION.getLocation().toString(), false);
        map.put(ModSounds.SUNSET_TRANSITION.getLocation().toString(), false);
        map.put(ModSounds.SUNSET_TRANSITION_END.getLocation().toString(), false);

        map.put(ModSounds.POOLROOMS_AMBIENCE_NOON.getLocation().toString(), false);
        map.put(ModSounds.POOLROOMS_AMBIENCE_SUNSET.getLocation().toString(), false);

        map.put(ModSounds.INFINITE_GRASS_AMBIENCE.getLocation().toString(), false);
        map.put(ModSounds.INFINITE_GRASS_SOUNDEVENT.getLocation().toString(), false);
        map.put(ModSounds.INFINITE_GRASS_SOUNDEVENT_FAR.getLocation().toString(), false);

        map.put(ModSounds.WINDTUNNEL_GRASS_AMBIENCE.getLocation().toString(), false);

        map.put(ModSounds.EMERGENCY_LIGHT_ALARM.getLocation().toString(), false);

        map.put(ModSounds.WALKER_FOOTSTEP.getLocation().toString(), false);
    }
}
