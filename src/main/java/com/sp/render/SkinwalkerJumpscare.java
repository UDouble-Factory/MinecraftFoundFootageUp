package com.sp.render;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.ModSounds;
import com.sp.util.ExtraUtils;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniform;
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
        setUniformInt(program, "Jumpscare", 1);

        long currentTime = (System.currentTimeMillis() - startTime);

        if(currentTime >= 2000 && currentTime < 12000) {
            setUniformInt(program, "CreepyFace1", 1);
        }

        if(currentTime >= 12000) {
            setUniformInt(program, "CreepyFace2", 1);
            setUniformVec2(program, "Rand", random.nextFloat(), random2.nextFloat());
        }

        if(currentTime >= 14000) {
            component.setBeingCaptured(false);
            client.options.hideGui = false;
            started = false;
            startTime = 0L;

            SPBRevampedClient.sendComponentSyncPacket(component.isBeingCaptured(), "beingCaptured");

            setUniformInt(program, "Jumpscare", 0);
            setUniformInt(program, "CreepyFace1", 0);
            setUniformInt(program, "CreepyFace2", 0);
            setUniformVec2(program, "Rand", 0.0f, 0.0f);
        }

    }

    private static void setUniformInt(ShaderProgram program, String name, int value) {
        ShaderUniform uniform = program.getUniform(name);
        if (uniform != null) {
            uniform.setInt(value);
        }
    }

    private static void setUniformVec2(ShaderProgram program, String name, float x, float y) {
        ShaderUniform uniform = program.getUniform(name);
        if (uniform != null) {
            uniform.setVector(x, y);
        }
    }

}
