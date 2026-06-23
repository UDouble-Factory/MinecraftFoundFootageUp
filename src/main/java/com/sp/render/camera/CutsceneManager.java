package com.sp.render.camera;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.util.MathStuff;
import foundry.veil.api.client.util.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Will likely use in a future API if I ever feel like making it
 */
@SuppressWarnings("DataFlowIssue")
public class CutsceneManager {
    public boolean started;
    public boolean isPlaying;
    public boolean fall;
    private int prevLightRenderDistance;
    public boolean backroomsBySP;
    private long startTime;
    private final int duration;
    private final int duration2;
    public BlackScreen blackScreen;
    private Entity camera;
    public float cameraRotZ;
    private final Minecraft client;

    // Keyframe data inlined — replaces Veil anim Path/Frame/Keyframe which was removed in 1.21
    // POS path: (0.5, 220, 0.5) → (0.5, 27, 0.5) over duration
    // ROT_X:    80° → 60° (first half), 60° → 110° (second half)
    // ROT_Y:    0°  → 120° over duration
    // ROT_Z:    0°→20°→-20°→0° split thirds

    public CutsceneManager() {
        this.started = false;
        this.isPlaying = false;
        this.fall = false;
        this.backroomsBySP = false;
        this.camera = null;
        this.duration = 7000;
        this.duration2 = 5000;
        this.blackScreen = new BlackScreen();
        this.client = Minecraft.getInstance();
        this.cameraRotZ = 0;
    }

    public void tick() {
        if (client.player != null && client.level != null) {
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(client.player);
            if (playerComponent.isDoingCutscene() && client.level.dimension() == BackroomsLevels.LEVEL0_WORLD_KEY) {
                this.pause();
                this.Fall();
                this.BackroomsBySP();
            } else {
                playerComponent.setDoingCutscene(false);
            }
            this.blackScreen.tick();
        }
    }

    private void pause() {
        if (!this.backroomsBySP && !this.fall) {
            if (!this.started) {
                this.blackScreen.showBlackScreen(60, true, false);
                this.startTime = System.currentTimeMillis();
                this.started = true;
            }
            float timer = (float) (System.currentTimeMillis() - this.startTime) / 2900;
            client.options.hideGui = true;
            if (timer >= 1.0) {
                this.fall = true;
            }
        }
    }

    private void Fall() {
        if (!this.backroomsBySP && this.fall) {
            if (!this.isPlaying) {
                this.prevLightRenderDistance = ConfigStuff.lightRenderDistance;
                ConfigStuff.lightRenderDistance = 1000;
                this.startTime = System.currentTimeMillis();
                this.isPlaying = true;
                client.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.FALLING, 1.0f));
            }
            float timer = (float) (System.currentTimeMillis() - this.startTime) / this.duration;
            if (this.camera == null) {
                this.initCamera();
            }
            if (timer >= 1.0) {
                this.blackScreen.showBlackScreen(50, true, false);
                this.backroomsBySP = true;
                this.startTime = System.currentTimeMillis() + 2500L;
                this.fall = false;
                camera.moveTo(3, 21, 1.5, 15, (float) 90);
                ConfigStuff.lightRenderDistance = this.prevLightRenderDistance;
            } else {
                client.options.hideGui = true;
                Vec3 newCameraPos = lerpedCameraPos(timer);
                Vec3 newCameraRot = lerpedCameraRot(timer);

                this.cameraRotZ = (float) newCameraRot.z;
                camera.moveTo(newCameraPos.x, newCameraPos.y, newCameraPos.z, (float) newCameraRot.y, (float) newCameraRot.x);
                client.cameraEntity = camera;
            }
        }
    }

    private void BackroomsBySP() {
        if (this.backroomsBySP) {
            float timer = (float) (System.currentTimeMillis() - this.startTime) / this.duration2;

            if (timer >= 1.0) {
                this.blackScreen.showBlackScreen(40, true, false);
                this.reset();
            } else {
                client.options.hideGui = false;
                camera.moveTo(3, 21, 1.5, 5, (float) 83);
                this.cameraRotZ = 100;
                client.cameraEntity = camera;
            }
        }
    }

    // Replaces Veil Path/Frame interpolation — simple piecewise lerp with easing
    private Vec3 lerpedCameraRot(float timer) {
        // ROT_X: 80 → 60 (first half), 60 → 110 (second half)
        double rotX;
        if (timer < 0.5f) {
            float t = Easing.EASE_IN_OUT_SINE.ease(timer * 2f);
            rotX = Mth.lerp(t, 80.0, 60.0);
        } else {
            float t = Easing.EASE_IN_OUT_SINE.ease((timer - 0.5f) * 2f);
            rotX = Mth.lerp(t, 60.0, 110.0);
        }

        // ROT_Y: 0 → 120 linear
        double rotY = Mth.lerp((double) timer, 0.0, 120.0);

        // ROT_Z: 0→20 (0–1/3), 20→-20 (1/3–2/3), -20→0 (2/3–1)
        double rotZ;
        if (timer < 1f / 3f) {
            float t = Easing.EASE_IN_OUT_SINE.ease(timer * 3f);
            rotZ = Mth.lerp(t, 0.0, 20.0);
        } else if (timer < 2f / 3f) {
            float t = Easing.EASE_IN_OUT_SINE.ease((timer - 1f / 3f) * 3f);
            rotZ = Mth.lerp(t, 20.0, -20.0);
        } else {
            float t = Easing.EASE_IN_OUT_SINE.ease((timer - 2f / 3f) * 3f);
            rotZ = Mth.lerp(t, -20.0, 0.0);
        }

        return new Vec3(rotX, rotY, rotZ);
    }

    private Vec3 lerpedCameraPos(float timer) {
        // POS: (0.5, 220, 0.5) → (0.5, 27, 0.5) using easeInSine
        float t = Easing.EASE_IN_SINE.ease(timer);
        double y = Mth.lerp(t, 220.0, 27.0);
        return new Vec3(0.5, y, 0.5);
    }

    public void reset() {
        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(client.player);
        this.isPlaying = false;
        this.started = false;
        this.fall = false;
        this.backroomsBySP = false;
        if (this.camera != null) {
            this.camera.remove(Entity.RemovalReason.DISCARDED);
            this.camera = null;
        }
        client.cameraEntity = client.player;
        client.options.hideGui = false;
        this.startTime = 0L;
        playerComponent.setDoingCutscene(false);

        SPBRevampedClient.sendComponentSyncPacket(playerComponent.isDoingCutscene(), "cutscene");
    }

    private void initCamera() {
        this.camera = new ItemEntity(client.level, 1.5, 300, 1.5, ItemStack.EMPTY);
        this.camera.moveTo(1.5, 300, 1.5, 0, 90);
    }


    public class BlackScreen {
        public boolean isBlackScreen;
        public boolean noEscape;
        private long duration;
        private long startTime;
        private boolean shouldPauseSounds;

        public BlackScreen() {
            this.startTime = 0L;
            this.isBlackScreen = false;
            this.duration = 0;
        }

        public void showBlackScreen(int time, boolean shouldPauseSounds, boolean noEscape) {
            this.duration = time * 50L;
            this.isBlackScreen = true;
            this.noEscape = noEscape;
            this.startTime = System.currentTimeMillis();
            this.shouldPauseSounds = shouldPauseSounds;
            client.options.hideGui = true;
        }

        public void tick() {
            if (isBlackScreen) {
                Minecraft client = Minecraft.getInstance();
                float timer = (float) (System.currentTimeMillis() - this.startTime) / this.duration;

                if (timer >= 1.0) {
                    SPBRevampedClient.blackScreen = false;
                    SPBRevampedClient.youCantEscape = false;
                    this.isBlackScreen = false;
                    client.options.hideGui = false;
                    this.startTime = 0;
                    client.getSoundManager().resume();
                } else {
                    if (shouldPauseSounds) {
                        client.getSoundManager().pause();
                    }
                    if (noEscape) {
                        SPBRevampedClient.youCantEscape = true;
                    }
                    client.options.hideGui = true;
                    SPBRevampedClient.blackScreen = true;
                }
            }
        }
    }
}
