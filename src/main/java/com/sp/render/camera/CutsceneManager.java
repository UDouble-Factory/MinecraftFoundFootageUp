package com.sp.render.camera;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.mixin.cutscene.PathAccessor;
import com.sp.util.MathStuff;
import foundry.veil.api.client.anim.Frame;
import foundry.veil.api.client.anim.Keyframe;
import foundry.veil.api.client.anim.Path;
import foundry.veil.api.client.util.Easings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

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
    private final Path cameraPathPos;
    private final Path cameraPathRotX;
    private final Path cameraPathRotY;
    private final Path cameraPathRotZ;
    public float cameraRotZ;
    private final Minecraft client;

    public CutsceneManager(){
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
        this.cameraPathPos = new Path(List.of(
                new Keyframe(new Vec3(0.5,220,0.5), Vec3.ZERO, Vec3.ZERO, MathStuff.millisecToTick(this.duration), Easings.Easing.easeInSine),
                new Keyframe(new Vec3(0.5,27,0.5), Vec3.ZERO, Vec3.ZERO,0, Easings.Easing.linear)
        ), false, false);
        this.cameraPathRotX = new Path(List.of(
                new Keyframe(Vec3.ZERO, new Vec3(80,0,0), Vec3.ZERO,MathStuff.millisecToTick(this.duration) / 2, Easings.Easing.easeInOutSine),
                new Keyframe(Vec3.ZERO, new Vec3(60,0,0), Vec3.ZERO,MathStuff.millisecToTick(this.duration) / 2, Easings.Easing.easeInOutSine),
                new Keyframe(Vec3.ZERO, new Vec3(110,0,0), Vec3.ZERO,0, Easings.Easing.easeInOutSine)
        ), false, false);
        this.cameraPathRotY = new Path(List.of(
                new Keyframe(Vec3.ZERO, new Vec3(0,0,0), Vec3.ZERO, MathStuff.millisecToTick(this.duration), Easings.Easing.linear),
                new Keyframe(Vec3.ZERO, new Vec3(0,120,0), Vec3.ZERO,0, Easings.Easing.linear)
        ), false, false);
        this.cameraPathRotZ = new Path(List.of(
                new Keyframe(Vec3.ZERO, new Vec3(0,0,20), Vec3.ZERO, MathStuff.millisecToTick(this.duration) / 2, Easings.Easing.easeInOutSine),
                new Keyframe(Vec3.ZERO, new Vec3(0,0,-20), Vec3.ZERO,MathStuff.millisecToTick(this.duration) / 2, Easings.Easing.easeInOutSine),
                new Keyframe(Vec3.ZERO, new Vec3(0,0,0), Vec3.ZERO,0, Easings.Easing.easeInOutSine)
        ), false, false);
    }


    public void tick(){
        if(client.player != null && client.level != null) {
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(client.player);
            if(playerComponent.isDoingCutscene() && client.level.dimension() == BackroomsLevels.LEVEL0_WORLD_KEY){
                this.pause();
                this.Fall();
                this.BackroomsBySP();
            } else {
                playerComponent.setDoingCutscene(false);
            }
            this.blackScreen.tick();
        }
    }

    private void pause(){
        if(!this.backroomsBySP && !this.fall) {
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
        if(!this.backroomsBySP && this.fall) {
            if (!this.isPlaying) {
                this.prevLightRenderDistance = ConfigStuff.lightRenderDistance;
                ConfigStuff.lightRenderDistance = 1000;
                this.startTime = System.currentTimeMillis();
                this.isPlaying = true;
//                SPBRevampedClient.getCameraShake().setCameraShake(MathStuff.millisecToTick(this.duration), 1, Easings.Easing.linear, true);
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

    private void BackroomsBySP(){
        if(this.backroomsBySP) {
            float timer = (float) (System.currentTimeMillis() - this.startTime) / this.duration2;

            if(timer >= 1.0){
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

    private Vec3 lerpedCameraRot(float timer){

        double interpolateX = MathStuff.mod(timer * ((PathAccessor) cameraPathRotX).getFrames().size(), 1);
        double currentFrameRotX = cameraPathRotX.frameAtProgress(timer).getRotation().x;
        double prevFrameRotX = previousFrameAtProgress(cameraPathRotX, timer).getRotation().x;

        double interpolateY = MathStuff.mod(timer * ((PathAccessor) cameraPathRotY).getFrames().size(), 1);
        double currentFrameRotY = cameraPathRotY.frameAtProgress(timer).getRotation().y;
        double prevFrameRotY = previousFrameAtProgress(cameraPathRotY, timer).getRotation().y;

        double interpolateZ = MathStuff.mod(timer * ((PathAccessor) cameraPathRotZ).getFrames().size(), 1);
        double currentFrameRotZ = cameraPathRotZ.frameAtProgress(timer).getRotation().z;
        double prevFrameRotZ = previousFrameAtProgress(cameraPathRotZ, timer).getRotation().z;

        return new Vec3(
                Mth.lerp(interpolateX, prevFrameRotX, currentFrameRotX),
                Mth.lerp(interpolateY, prevFrameRotY, currentFrameRotY),
                Mth.lerp(interpolateZ, prevFrameRotZ, currentFrameRotZ)
        );
    }

    private Vec3 lerpedCameraPos(float timer){
        double interpolatePos = MathStuff.mod(timer * ((PathAccessor) cameraPathPos).getFrames().size(), 1);
        Vec3 currentFramePos = cameraPathPos.frameAtProgress(timer).getPosition();
        Vec3 prevFramePos = previousFrameAtProgress(cameraPathPos, timer).getPosition();

        return new Vec3(
                Mth.lerp(interpolatePos, prevFramePos.x, currentFramePos.x),
                Mth.lerp(interpolatePos, prevFramePos.y, currentFramePos.y),
                Mth.lerp(interpolatePos, prevFramePos.z, currentFramePos.z)
        );
    }

    public void reset(){
        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(client.player);
        this.isPlaying = false;
        this.started = false;
        this.fall = false;
        this.backroomsBySP = false;
        if(this.camera != null) {
            this.camera.remove(Entity.RemovalReason.DISCARDED);
            this.camera = null;
        }
        client.cameraEntity = client.player;
        client.options.hideGui = false;
        this.startTime = 0L;
        playerComponent.setDoingCutscene(false);

        SPBRevampedClient.sendComponentSyncPacket(playerComponent.isDoingCutscene(), "cutscene");

    }

    private void initCamera(){
        this.camera = new ItemEntity(client.level, 1.5, 300, 1.5, ItemStack.EMPTY);
        this.camera.moveTo(1.5, 300, 1.5, 0, 90);
    }

    private Frame previousFrameAtProgress(Path path, double progress){
        List<Frame> frames = ((PathAccessor) path).getFrames();
        int index = (int) (frames.size() * progress) - 1;
        if(index < 0){
            return frames.get(0);
        }
        return frames.get(index);
    }



    public class BlackScreen{
        public boolean isBlackScreen;
        public boolean noEscape;
        private long duration;
        private long startTime;
        private boolean shouldPauseSounds;

        //Duration in ticks
        public BlackScreen(){
            this.startTime = 0L;
            this.isBlackScreen = false;
            this.duration = 0;
        }

        public void showBlackScreen(int time, boolean shouldPauseSounds, boolean noEscape){
            this.duration = time * 50L;
            this.isBlackScreen = true;
            this.noEscape = noEscape;
            this.startTime = System.currentTimeMillis();
            this.shouldPauseSounds = shouldPauseSounds;
            client.options.hideGui = true;
        }

        //Tick
        public void tick(){
            if(isBlackScreen){
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
                    if(shouldPauseSounds) {
                        client.getSoundManager().pause();
                    }
                    if(noEscape){
                        SPBRevampedClient.youCantEscape = true;
                    }
                    client.options.hideGui = true;
                    SPBRevampedClient.blackScreen = true;
                }
            }
        }

    }

}
