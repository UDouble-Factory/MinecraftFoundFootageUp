package com.sp.render;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.ModSounds;
import com.sp.util.ExtraUtils;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class SkinwalkerJumpscare {
    private static long startTime;
    private static boolean started = false;
    private static RandomSource random = RandomSource.create(13);
    private static RandomSource random2 = RandomSource.create(8767);

    private static SimpleSoundInstance jumpScareSound;

    public static void doJumpscare(ShaderProgram program, Minecraft client, PlayerComponent component){
        if(!started){
            startTime = System.currentTimeMillis();
            started = true;
            if(client.player != null) {
                jumpScareSound = new SimpleSoundInstance(ModSounds.JUMPSCARE, SoundSource.HOSTILE, 1.0f, 1.0f, client.player.getRandom(), client.player.blockPosition());
                client.getSoundManager().play(jumpScareSound);
            }
        }

        ExtraUtils.stopAllOtherSounds(jumpScareSound.getLocation(), client.getSoundManager().soundEngine);
        client.options.hideGui = true;
        program.setInt("Jumpscare", 1);

        long currentTime = (System.currentTimeMillis() - startTime);

        if(currentTime >= 2000 && currentTime < 12000) {
            program.setInt("CreepyFace1", 1);
        }

        if(currentTime >= 12000) {
            program.setInt("CreepyFace2", 1);
            program.setVector("Rand",  random.nextFloat(), random2.nextFloat());
        }

        if(currentTime >= 14000) {
            component.setBeingCaptured(false);
            client.options.hideGui = false;
            started = false;
            startTime = 0L;

            SPBRevampedClient.sendComponentSyncPacket(component.isBeingCaptured(), "beingCaptured");

            program.setInt("Jumpscare", 0);
            program.setInt("CreepyFace1", 0);
            program.setInt("CreepyFace2", 0);
            program.setVector("Rand", 0, 0);
        }

    }

}
