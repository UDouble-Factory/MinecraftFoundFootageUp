package com.sp.render.camera;

import com.sp.SPBRevampedClient;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.util.MathStuff;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

public class CameraShake {
    public double trauma;
    private double traumaGoal;
    public double noiseSpeed;
    private double noiseSpeedGoal;
    private double noiseY;
    private double amplitude;
    private ImprovedNoise noiseSampler;
    private float cameraZRot;

    public CameraShake(){
        this.trauma = 0.1;
        this.noiseSpeed = 0.1;
        this.noiseY = 0;
        this.amplitude = 5;
        this.noiseSampler = new ImprovedNoise(RandomSource.create());
        this.cameraZRot = 0.0f;
    }

    public void tick(Camera camera) {
        if (ConfigStuff.enableRealCamera && !SPBRevampedClient.getCutsceneManager().isPlaying) {
            float frameDelta = Minecraft.getInstance().getDeltaFrameTime();
            if (this.noiseY >= 1000) {
                this.noiseY = 0;
            }

            Player player = Minecraft.getInstance().player;
            if (player != null) {
                float playerSpeed = (player.walkDist - player.walkDistO) * 6;

                if (player.isFallFlying()) playerSpeed /= 6; // https://github.com/SpacePotatoee/MinecraftFoundFootage/issues/101

                this.traumaGoal = Mth.clamp(0.6 * playerSpeed, 0.5, 1.5f);
                this.noiseSpeedGoal = Mth.clamp(0.25 * playerSpeed, 0.1, 1.0f);
                this.amplitude = 4;

                this.trauma = Math.max(MathStuff.Lerp((float) this.trauma, (float) this.traumaGoal, 0.93f, frameDelta), 0.5);
                this.noiseSpeed = Math.max(MathStuff.Lerp((float) this.noiseSpeed, (float) this.noiseSpeedGoal, 0.93f, frameDelta), 0.1);

                this.noiseY += (this.noiseSpeed * frameDelta);

                double pitchOffset = this.amplitude * this.getShakeIntensity() * (this.noiseSampler.noise(1, this.noiseY, 0));
                double yawOffset = this.amplitude * this.getShakeIntensity() * (this.noiseSampler.noise(73, this.noiseY, 0));
                double rollOffset = this.amplitude * this.getShakeIntensity() * (this.noiseSampler.noise(146, this.noiseY, 0));

                camera.setRotation((float) (camera.getYRot() + yawOffset), (float) (camera.getXRot() + pitchOffset));
                this.cameraZRot = (float) rollOffset * 2;
            }
        }
    }

    private double getShakeIntensity(){
        return this.trauma * this.trauma;
    }

    public float getCameraZRot() {
        return this.cameraZRot;
    }

}
