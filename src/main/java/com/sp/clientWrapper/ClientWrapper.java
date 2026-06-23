package com.sp.clientWrapper;

import com.sp.ModKeyBinds;
import com.sp.SPBRevampedClient;
import com.sp.block.custom.EmergencyLightBlock;
import com.sp.block.custom.FluorescentLightBlock;
import com.sp.block.custom.ThinFluorescentLightBlock;
import com.sp.block.entity.EmergencyLightBlockEntity;
import com.sp.block.entity.FluorescentLightBlockEntity;
import com.sp.block.entity.ThinFluorescentLightBlockEntity;
import com.sp.block.entity.TinyFluorescentLightBlockEntity;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.entity.client.SkinWalkerCapturedFlavorText;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.entity.custom.SmilerEntity;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.init.BackroomsLevels;
import com.sp.init.HelpfulHintManager;
import com.sp.init.ModSounds;
import com.sp.networking.C2S.TargetEntitySyncPayload;
import com.sp.networking.InitializePackets;
import com.sp.sounds.*;
import com.sp.sounds.entity.SkinWalkerChaseSoundInstance;
import com.sp.sounds.entity.SmilerAmbienceSoundInstance;
import com.sp.sounds.entity.SmilerGlitchSoundInstance;
import com.sp.sounds.pipes.GasPipeSoundInstance;
import com.sp.sounds.pipes.WaterPipeSoundInstance;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.BackroomsLevelWithLights;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import com.sp.world.levels.custom.PoolroomsBackroomsLevel;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.client.render.light.data.PointLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.List;
import java.util.Optional;

import static com.sp.block.custom.ThinFluorescentLightBlock.FACE;
import static com.sp.block.custom.ThinFluorescentLightBlock.FACING;

/**
 * This class is just here to avoid dedicated server crashes.
 * Minecraft seams to crash even when a client class is present in a method without being called on the client.
 * This mostly happens with Sound Instances. And Veil lights.
 **/
public class ClientWrapper {
    public static void skinWalkerPlayStepSound(ServerLimb limb) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            Minecraft client = Minecraft.getInstance();
            client.getSoundManager().play(new SimpleSoundInstance(ModSounds.SKINWALKER_FOOTSTEP, SoundSource.HOSTILE, 10.0f, 1.0f, limb.random, limb.pos.x, limb.pos.y, limb.pos.z));
        }
    }

    public static void walkerPlayStepSound(ServerLimb limb) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            Minecraft client = Minecraft.getInstance();
            client.getSoundManager().play(new SimpleSoundInstance(ModSounds.WALKER_FOOTSTEP, SoundSource.HOSTILE, 10.0f, 1.0f, limb.random, limb.pos.x, limb.pos.y, limb.pos.z));
        }
    }

    public static void tickClientPlayerComponent(PlayerComponent playerComponent) {
        Minecraft client = Minecraft.getInstance();

        if (client.player != null && playerComponent.player == client.player) {
            SoundManager soundManager = client.getSoundManager();

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Get a list of all the smilers in the area and see if any of them can see you
            List<SmilerEntity> smilerEntityList = playerComponent.player.level().getEntitiesOfClass(SmilerEntity.class, playerComponent.player.getBoundingBox().inflate(15, 1, 15), livingEntity -> true);
            boolean isSeen = false;
            if (!smilerEntityList.isEmpty()) {
                for (SmilerEntity smiler : smilerEntityList) {
                    if (smiler.hasLineOfSight(playerComponent.player)) {
                        playerComponent.setShouldGlitch(true);
                        isSeen = true;
                        break;
                    }
                }

            }

            if (!isSeen) {
                playerComponent.setShouldGlitch(false);
            }

            //Update smiler glitch effect
            if (playerComponent.shouldGlitch()) {
                playerComponent.glitchTick = Math.min(playerComponent.glitchTick + 1, 80);
                playerComponent.glitchTimer = Math.min((float) playerComponent.glitchTick / 80, 1.0f);

                if (!soundManager.isActive(playerComponent.GlitchAmbience)) {
                    playerComponent.GlitchAmbience = new SmilerGlitchSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.GlitchAmbience);
                }

                if (playerComponent.glitchTimer >= 0.25f) {
                    if (!playerComponent.shouldInflictGlitchDamage) {
                        playerComponent.shouldInflictGlitchDamage = true;
//                                System.out.println("SENT TRUE TO: " + playerComponent.player.getName().toString());
                        SPBRevampedClient.sendComponentSyncPacket(true, "glitch");
                    }
                }

            } else if (!playerComponent.isTeleportingToPoolrooms() && (!(SPBRevampedClient.isInLevel(BackroomsLevels.LEVEL324_BACKROOMS_LEVEL) && playerComponent.player.level().getBlockState(playerComponent.player.blockPosition().relative(Direction.DOWN, 2)).is(Blocks.GREEN_WOOL)))) {
                playerComponent.glitchTick = Math.max(playerComponent.glitchTick - 1, 0);
                playerComponent.glitchTimer = Math.max((float) playerComponent.glitchTick / 80, 0.0f);

                if (playerComponent.glitchTimer <= 0) {
                    if (soundManager.isActive(playerComponent.GlitchAmbience)) {
                        soundManager.stop(playerComponent.GlitchAmbience);
                    }
                }

                if (playerComponent.glitchTimer <= 0.75f) {
                    if (playerComponent.shouldInflictGlitchDamage) {
                        playerComponent.shouldInflictGlitchDamage = false;
//                                System.out.println("SENT FALSE TO: " + playerComponent.player.getName().toString());
                        SPBRevampedClient.sendComponentSyncPacket(false, "glitch");
                    }
                }
            }

            if (SPBRevampedClient.isInLevel(BackroomsLevels.LEVEL324_BACKROOMS_LEVEL) && playerComponent.player.level().getBlockState(playerComponent.player.blockPosition().relative(Direction.DOWN, 2)).is(Blocks.GREEN_WOOL)) {
                playerComponent.glitchTick = Math.min(playerComponent.glitchTick + 4, 120);
                playerComponent.glitchTimer = (float) playerComponent.glitchTick / 30;

                if (!soundManager.isActive(playerComponent.GlitchAmbience)) {
                    playerComponent.GlitchAmbience = new SmilerGlitchSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.GlitchAmbience);
                }


                if (playerComponent.glitchTimer >= 3) {
                    if (SPBRevampedClient.isInLevel(BackroomsLevels.LEVEL324_BACKROOMS_LEVEL) && playerComponent.player.level().getBlockState(playerComponent.player.blockPosition().relative(Direction.DOWN, 3)).is(Blocks.RED_WOOL)) {
                        playerComponent.player.teleportTo(playerComponent.player.getX(), playerComponent.player.getY() - 65, playerComponent.player.getZ());
                    }
                }
            }


            //Teleporting to poolrooms Glitch
            if (playerComponent.isTeleportingToPoolrooms()) {
                playerComponent.glitchTick = Math.min(playerComponent.glitchTick + 1, 120);
                playerComponent.glitchTimer = Math.min((float) playerComponent.glitchTick / 120, 1.0f);

                if (!soundManager.isActive(playerComponent.GlitchAmbience)) {
                    playerComponent.GlitchAmbience = new SmilerGlitchSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.GlitchAmbience);
                }
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Sync Target Entity for updating SkinWalker suspicion
            if (playerComponent.getTargetEntity() != client.crosshairPickEntity) {
                playerComponent.setTargetEntity(client.crosshairPickEntity);

                int entityId = playerComponent.getTargetEntity() != null ? playerComponent.getTargetEntity().getId() : -1;
                ClientPlayNetworking.send(new TargetEntitySyncPayload(entityId));
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Flavor text while being controlled by the SkinWalker
            if (playerComponent.hasBeenCaptured() && !playerComponent.isBeingCaptured()) {
                SkinWalkerCapturedFlavorText.tickFlavorText(playerComponent.player);
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Client side stuff for level 0 -> 1 and 1 -> 2 and so on.

            Optional<BackroomsLevel> backroomsLevel = BackroomsLevels.getLevel(playerComponent.player.level());

            if (backroomsLevel.isPresent()) {
                BackroomsLevel level = backroomsLevel.get();

                List<BackroomsLevel.LevelTransition> teleports = level.checkForTransition(playerComponent, playerComponent.player.level());

                if (!teleports.isEmpty() && playerComponent.currentTransition == null) {
                    playerComponent.currentTransition = teleports.get(0);
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                //Flashlight

                if (ModKeyBinds.toggleFlashlight.consumeClick() && !SPBRevampedClient.getCutsceneManager().isPlaying && !SPBRevampedClient.getCutsceneManager().blackScreen.isBlackScreen && !playerComponent.hasBeenCaptured && !playerComponent.isBeingCaptured()) {
                    playerComponent.player.playSound(ModSounds.FLASHLIGHT_CLICK, 0.5f, 1);
                    if (level.allowsTorch().value()) {
                        playerComponent.setFlashLightOn(!playerComponent.isFlashLightOn());
                        HelpfulHintManager.disableFlashlightHint();

                        if (!playerComponent.player.isSpectator()) {
                            SPBRevampedClient.sendComponentSyncPacket(playerComponent.isFlashLightOn(), "flashlight");
                        }
                    } else {
                        playerComponent.setFlashLightOn(false);
                        playerComponent.player.displayClientMessage(level.allowsTorch().string(), true);
                    }
                } else if (playerComponent.hasBeenCaptured && playerComponent.isBeingCaptured()) {
                    if (playerComponent.isFlashLightOn()) {
                        playerComponent.setFlashLightOn(false);

                        SPBRevampedClient.sendComponentSyncPacket(playerComponent.isFlashLightOn(), "flashlight");
                    }
                }

                if (!level.allowsTorch().value()) {
                    if (playerComponent.isFlashLightOn()) {
                        SPBRevampedClient.sendComponentSyncPacket(false, "flashlight");
                    }
                    playerComponent.setFlashLightOn(false);
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }


            if (playerComponent.currentTransition != null) {
                if (playerComponent.getTeleportingTimer() == -1) {
                    playerComponent.setTeleportingTimer(playerComponent.currentTransition.duration());
                }

                if (playerComponent.getTeleportingTimer() == 0) {
                    playerComponent.currentTransition.teleport().to().transitionOut(playerComponent.currentTransition.teleport());
                    playerComponent.currentTransition.teleport().to().transitionIn(playerComponent.currentTransition.teleport());
                    playerComponent.currentTransition = null;
                }
            }

            if (playerComponent.getTeleportingTimer() >= 1) {
                if (playerComponent.currentTransition != null) {
                    playerComponent.currentTransition.callback().tick(playerComponent.currentTransition.teleport(), playerComponent.getTeleportingTimer());
                }
                playerComponent.setTeleportingTimer(playerComponent.getTeleportingTimer() - 1);
            }

            ////AMBIENCE////
            ResourceKey<Level> levelKey = playerComponent.player.level().dimension();

            if ((levelKey == BackroomsLevels.LEVEL1_WORLD_KEY || levelKey == BackroomsLevels.LEVEL2_WORLD_KEY) && !soundManager.isActive(playerComponent.DeepAmbience)) {
                playerComponent.DeepAmbience = new AmbientSoundInstance(playerComponent.player);
                soundManager.play(playerComponent.DeepAmbience);
            }

            if (levelKey == BackroomsLevels.LEVEL2_WORLD_KEY && !soundManager.isActive(playerComponent.WaterPipeAmbience) && !soundManager.isActive(playerComponent.GasPipeAmbience)) {
                playerComponent.WaterPipeAmbience = new WaterPipeSoundInstance(playerComponent.player);
                playerComponent.GasPipeAmbience = new GasPipeSoundInstance(playerComponent.player);

                soundManager.play(playerComponent.WaterPipeAmbience);
                soundManager.play(playerComponent.GasPipeAmbience);
            }

            if ((BackroomsLevels.getLevel(playerComponent.player.level()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL))
                    instanceof Level2BackroomsLevel level) {
                if (levelKey == BackroomsLevels.LEVEL2_WORLD_KEY && !soundManager.isActive(playerComponent.WarpAmbience) && level.isWarping()) {
                    playerComponent.WarpAmbience = new CreakingSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.WarpAmbience);
                }
            }

            if ((BackroomsLevels.getLevel(playerComponent.player.level()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL))
                    instanceof PoolroomsBackroomsLevel level) {
                if (level.isNoon() && !soundManager.isActive(playerComponent.PoolroomsNoonAmbience)) {
                    playerComponent.PoolroomsNoonAmbience = new PoolroomsNoonAmbienceSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.PoolroomsNoonAmbience);
                }
            }

            if ((BackroomsLevels.getLevel(playerComponent.player.level()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL))
                    instanceof PoolroomsBackroomsLevel level) {
                if (!level.isNoon() && !soundManager.isActive(playerComponent.PoolroomsSunsetAmbience)) {
                    playerComponent.PoolroomsSunsetAmbience = new PoolroomsSunsetAmbienceSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.PoolroomsSunsetAmbience);
                }
            }

            if ((BackroomsLevels.getLevel(playerComponent.player.level()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL))
                    instanceof Level1BackroomsLevel level) {
                if (level.getLightState() == BackroomsLevelWithLights.LightState.BLACKOUT && !soundManager.isActive(playerComponent.SmilerAmbience)) {
                    playerComponent.SmilerAmbience = new SmilerAmbienceSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.SmilerAmbience);
                }
            }

            if ((levelKey == BackroomsLevels.INFINITE_FIELD_WORLD_KEY) && !soundManager.isActive(playerComponent.WindAmbience)) {
                playerComponent.WindAmbience = new InfiniteGrassAmbienceSoundInstance(playerComponent.player);
                soundManager.play(playerComponent.WindAmbience);
            }

            if ((levelKey == BackroomsLevels.LEVEL324_WORLD_KEY) && !soundManager.isActive(playerComponent.WindAmbience) && playerComponent.player.getY() > 20) {
                playerComponent.WindAmbience = new InfiniteGrassAmbienceSoundInstance(playerComponent.player);
                if (soundManager.isActive(playerComponent.WindTunnelAmbience)) {
                    soundManager.stop(playerComponent.WindTunnelAmbience);
                }
                soundManager.play(playerComponent.WindAmbience);
            }

            if ((levelKey == BackroomsLevels.LEVEL324_WORLD_KEY) && !soundManager.isActive(playerComponent.WindTunnelAmbience) && playerComponent.player.getY() < 20) {
                playerComponent.WindTunnelAmbience = new WindTunnelAmbienceSoundInstance(playerComponent.player);
                if (soundManager.isActive(playerComponent.WindAmbience)) {
                    soundManager.stop(playerComponent.WindAmbience);
                }
                soundManager.play(playerComponent.WindTunnelAmbience);
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Level0 Cutscene
            if (playerComponent.player.isInWall() && playerComponent.player.level().dimension() == Level.OVERWORLD && !playerComponent.isDoingCutscene()) {
                playerComponent.suffocationTimer++;
                if (playerComponent.suffocationTimer >= 40) {
                    playerComponent.setDoingCutscene(true);
                    playerComponent.suffocationTimer = 0;
                }
            }

        }
    }

    public static void onRemoveSkinWalkerClientSide(SkinWalkerEntity entity) {
        if (entity.chaseSoundInstance != null && entity.level().isClientSide) {
            Minecraft.getInstance().getSoundManager().stop(entity.chaseSoundInstance);
        }
    }

    public static void handleSkinWalkerEntityClientSide(SkinWalkerEntity entity) {
        Minecraft client = Minecraft.getInstance();
        if (!client.getSoundManager().isActive(entity.chaseSoundInstance)) {
            entity.chaseSoundInstance = new SkinWalkerChaseSoundInstance(entity);
            client.getSoundManager().play(entity.chaseSoundInstance);
        }
    }

    public static void tickEmergencyLight(Level world, BlockPos pos, BlockState state, EmergencyLightBlockEntity block) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;

        if (player == null) {
            return;
        }

        if (state.getValue(EmergencyLightBlock.RED_LIGHT)) {
            if (!block.playingEmergencyAlarm) {
                block.emergencyAlarmSoundInstance = new EmergencyAlarmSoundInstance(block, player);
                client.getSoundManager().play(block.emergencyAlarmSoundInstance);
                block.setEmergencyAlarm(true);
            }

            AxisAngle4f axisAngle4d = new AxisAngle4f();
            Quaternionf quaternionf = new Quaternionf();
            Vec3 centerPos = pos.getCenter();
            switch (state.getValue(EmergencyLightBlock.FACE)) {
                case WALL -> {
                    switch (state.getValue(EmergencyLightBlock.FACING)) {
                        case EAST -> {
                            axisAngle4d.set(0.0f, 1, 0, 0);
                            quaternionf.rotateXYZ(0.0f, 0.0f, (float) Math.toRadians(90.0f));
                            centerPos = centerPos.add(-0.28125, 0.0f, 0.0f);
                        }
                        case WEST -> {
                            axisAngle4d.set(0.0f, -1, 0, 0);
                            quaternionf.rotateXYZ(0.0f, 0.0f, (float) Math.toRadians(90.0f));
                            centerPos = centerPos.add(0.28125, 0.0f, 0.0f);
                        }
                        case NORTH -> {
                            axisAngle4d.set(0.0f, 0, 0, -1);
                            quaternionf.rotateXYZ((float) Math.toRadians(90.0f), 0.0f, 0.0f);
                            centerPos = centerPos.add(0.0f, 0.0f, 0.28125);
                        }
                        case SOUTH -> {
                            axisAngle4d.set(0.0f, 0, 0, 1);
                            quaternionf.rotateXYZ((float) Math.toRadians(90.0f), 0.0f, 0.0f);
                            centerPos = centerPos.add(0.0f, 0.0f, -0.28125);
                        }
                    }

                }
                case FLOOR -> {
                    axisAngle4d.set(0.0f, 0, 1, 0);
                    centerPos = centerPos.add(0.0f, -0.28125, 0.0f);
                }
                default -> {
                    axisAngle4d.set(0.0f, 0, -1, 0);
                    centerPos = centerPos.add(0.0f, 0.28125, 0.0f);
                }
            }

            block.removeNormalLights();

            if (!block.initEmergencyLights) {
                block.areaLight1 = new AreaLightData();
                block.areaLight1.getOrientation().rotateXYZ(0, 0, 0);
                block.areaLight1.getPosition().set(centerPos.x, centerPos.y, centerPos.z);
                block.areaLight2 = new AreaLightData();
                block.areaLight2.getOrientation().rotateXYZ(0, 0, 0);
                block.areaLight2.getPosition().set(centerPos.x, centerPos.y, centerPos.z);
                block.pointLight = new PointLightData();


                block.areaLight1Handle = VeilRenderSystem.renderer().getLightRenderer().addLight(block.areaLight1
                        .setBrightness(1.0f)
                        .setColor(1.0f, 0.0f, 0.0f)
                        .setSize(0.0, 0.0)
                        .setAngle((float) Math.toRadians(50.0f))
                        .setDistance(15)
                );
                block.areaLight2Handle = VeilRenderSystem.renderer().getLightRenderer().addLight(block.areaLight2
                        .setBrightness(1.0f)
                        .setColor(1.0f, 0.0f, 0.0f)
                        .setSize(0.0, 0.0)
                        .setAngle((float) Math.toRadians(50.0f))
                        .setDistance(15)
                );
                block.pointLightHandle = VeilRenderSystem.renderer().getLightRenderer().addLight(block.pointLight
                        .setBrightness(0.5f)
                        .setColor(1.0f, 0.0f, 0.0f)
                        .setPosition(new Vector3d(centerPos.x, centerPos.y, centerPos.z))
                        .setRadius(15.0f)
                );
                block.initEmergencyLights = true;
            }

            Quaternionf quaternionf1 = new Quaternionf(quaternionf);
            quaternionf1.rotateLocalY((float) Math.toRadians(block.randomOffset + world.getGameTime() * 20));
            block.areaLight1.getOrientation().set(quaternionf1);
            block.areaLight1Handle.markDirty();

            Quaternionf quaternionf2 = new Quaternionf(quaternionf);
            quaternionf2.rotateLocalY((float) Math.toRadians(block.randomOffset + 180.0f + world.getGameTime() * 20));
            block.areaLight2.getOrientation().set(quaternionf2);
            block.areaLight2Handle.markDirty();

            return;
        }

        if (block.playingEmergencyAlarm) {
            client.getSoundManager().stop(block.emergencyAlarmSoundInstance);
            block.emergencyAlarmSoundInstance = null;
            block.setEmergencyAlarm(false);
        }

        block.removeEmergencyLights();

        if (!block.initNormalLights) {
            block.pointLight = new PointLightData();
            Vec3 centerPos = pos.getCenter().add(0.0f, -0.15625f, 0.0f);
            block.pointLightHandle = VeilRenderSystem.renderer().getLightRenderer().addLight(block.pointLight
                    .setBrightness(1.0f)
                    .setPosition(new Vector3d(centerPos.x, centerPos.y, centerPos.z))
                    .setRadius(15.0f)
            );
            block.initNormalLights = true;
        }
    }

    public static void doClientSideThinFluorescentsTick(Level world, BlockPos pos, BlockState state, java.util.Random random1, Vec3 position, ThinFluorescentLightBlockEntity block) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
            Vec3 playerPos = player.position();
            double distance;

            if (world.dimension() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                distance = Math.min(ConfigStuff.getLightRenderDistance(), 32);
            } else {
                distance = ConfigStuff.getLightRenderDistance();
            }

            boolean withinDistance = pos.closerToCenterThan(playerPos, distance);

            if (withinDistance) {
                if (!state.getValue(ThinFluorescentLightBlock.COPY) && pos.closerToCenterThan(playerPos, 15.0f)) {
                    if (block.prevOn != world.getBlockState(pos).getValue(ThinFluorescentLightBlock.ON)) {
                        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(ModSounds.LIGHT_BLINK, SoundSource.AMBIENT, 0.2F, random1.nextFloat(0.9f, 1.1f), block.random, pos));
                    }
                }

                if (!state.getValue(ThinFluorescentLightBlock.COPY) && state.getValue(ThinFluorescentLightBlock.ON) && !state.getValue(ThinFluorescentLightBlock.BLACKOUT)) {

                    if (!block.isPlayingSound() && pos.closerToCenterThan(playerPos, 15.0f) && !SPBRevampedClient.blackScreen) {
                        Minecraft.getInstance().getSoundManager().play(new ThinFluorescentLightSoundInstance(block, player));
                        block.setPlayingSound(true);
                    }

                    if (block.pointLight == null) {
                        block.pointLight = new PointLightData();
                        block.pointLightHandle = VeilRenderSystem.renderer().getLightRenderer().addLight(block.pointLight
                                .setRadius(18f)
                                .setBrightness(0.0024f)
                        );
                        switch (state.getValue(FACE)) {
                            case FLOOR:
                                block.pointLight.setPosition(position.x, position.y, position.z);
                            case WALL:
                                switch (state.getValue(FACING)) {
                                    case EAST:
                                        block.pointLight.setPosition(position.x, position.y, position.z + 0.5);
                                    case WEST:
                                        block.pointLight.setPosition(position.x, position.y, position.z - 0.5);
                                    case SOUTH:
                                        block.pointLight.setPosition(position.x + 0.5, position.y, position.z);
                                    case NORTH:
                                    default:
                                        block.pointLight.setPosition(position.x - 0.5, position.y, position.z);
                                }
                            case CEILING:
                            default:
                                block.pointLight.setPosition(position.x, position.y, position.z);

                        }

                        switch (world.dimension().location().toString()) {
                            case "spb-revamped:poolrooms": {
                                block.pointLight
                                        .setColor(175, 175, 255)
                                        .setBrightness(0.0035f);
                            }
                            break;
                            case "spb-revamped:level0": {
                                block.pointLight
                                        .setColor(200, 200, 255)
                                        .setBrightness(0.005f);
                            }
                            break;
                            default: {
                                block.pointLight.setColor(255, 255, 255);
                            }
                        }

                        if (world.dimension() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                            block.pointLight
                                    .setColor(200, 200, 255)
                                    .setBrightness(0.005f);
                        }
                    }
                } else {
                    if (block.pointLightHandle != null) {
                        block.pointLightHandle.free();
                        block.pointLightHandle = null;
                        block.pointLight = null;
                    }
                }
            } else {
                if (block.pointLightHandle != null) {
                    block.pointLightHandle.free();
                    block.pointLightHandle = null;
                    block.pointLight = null;
                }
            }
        }
    }


    public static void doClientSideTinyFluorescentsTick(Level world, BlockPos pos, BlockState state, java.util.Random random1, Vec3 position, TinyFluorescentLightBlockEntity block) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
            Vec3 playerPos = player.position();
            double distance;

            if (world.dimension() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                distance = Math.min(ConfigStuff.getLightRenderDistance(), 32);
            } else {
                distance = ConfigStuff.getLightRenderDistance();
            }

            boolean withinDistance = pos.closerToCenterThan(playerPos, distance);

            if (withinDistance) {
                if (!state.getValue(ThinFluorescentLightBlock.COPY) && pos.closerToCenterThan(playerPos, 15.0f)) {
                    if (block.prevOn != world.getBlockState(pos).getValue(ThinFluorescentLightBlock.ON)) {
                        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(ModSounds.LIGHT_BLINK, SoundSource.AMBIENT, 0.2F, random1.nextFloat(0.9f, 1.1f), block.random, pos));
                    }
                }

                if (!state.getValue(ThinFluorescentLightBlock.COPY) && state.getValue(ThinFluorescentLightBlock.ON) && !state.getValue(ThinFluorescentLightBlock.BLACKOUT)) {

                    if (!block.isPlayingSound() && pos.closerToCenterThan(playerPos, 15.0f) && !SPBRevampedClient.blackScreen) {
                        Minecraft.getInstance().getSoundManager().play(new TinyFluorescentLightSoundInstance(block, player));
                        block.setPlayingSound(true);
                    }

                    if (block.pointLight == null) {
                        block.pointLight = new PointLightData();
                        block.pointLightHandle = VeilRenderSystem.renderer().getLightRenderer().addLight(block.pointLight
                                .setRadius(18f)
                                .setBrightness(0.0024f)
                        );

                        block.pointLight.setPosition(position.x, position.y, position.z);

                        block.pointLight.setColor(255, 255, 255);

                        if (world.dimension().equals(BackroomsLevels.POOLROOMS_WORLD_KEY)) {
                            block.pointLight
                                    .setColor(175, 175, 255)
                                    .setBrightness(0.0035f);
                        }

                        if (world.dimension() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                            block.pointLight
                                    .setColor(200, 200, 255)
                                    .setBrightness(0.005f);
                        }

                        if (world.dimension().equals(BackroomsLevels.LEVEL0_WORLD_KEY)) {
                            block.pointLight
                                    .setColor(200, 200, 255)
                                    .setBrightness(0.005f);
                        }
                    }
                } else {
                    if (block.pointLightHandle != null) {
                        block.pointLightHandle.free();
                        block.pointLightHandle = null;
                        block.pointLight = null;
                    }
                }
            } else {
                if (block.pointLightHandle != null) {
                    block.pointLightHandle.free();
                    block.pointLightHandle = null;
                    block.pointLight = null;
                }
            }
        }
    }

    public static void doClientSideTick(Level world, BlockPos pos, BlockState state, FluorescentLightBlockEntity block) {
        if (!world.isClientSide) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        Player player = client.player;

        Vec3 position = pos.getCenter();

        if (player != null) {

            if (!state.getValue(FluorescentLightBlock.COPY)) {
                if (pos.closerToCenterThan(player.position(), 20)) {
                    if (block.prevOn != world.getBlockState(pos).getValue(FluorescentLightBlock.ON)) {
                        client.getSoundManager().play(new SimpleSoundInstance(ModSounds.LIGHT_BLINK, SoundSource.AMBIENT, 0.1F, block.random1.nextFloat(0.9f, 1.1f), block.random, pos));
                    }
                }
            }

            Vec3 playerPos = player.position();
            boolean withinDistance = pos.closerToCenterThan(playerPos, ConfigStuff.getLightRenderDistance());
            if (withinDistance) {
                if (!state.getValue(FluorescentLightBlock.COPY) &&
                        state.getValue(FluorescentLightBlock.ON) &&
                        !state.getValue(FluorescentLightBlock.BLACKOUT)) {
                    if (!block.isPlayingSound() && pos.closerToCenterThan(playerPos, 16.0f) && !state.getValue(FluorescentLightBlock.BLACKOUT) && !SPBRevampedClient.blackScreen) {
                        Minecraft.getInstance().getSoundManager().play(new FluorescentLightSoundInstance(block, player));
                        block.setPlayingSound(true);
                    }

                    if (block.pointLight == null) {
                        block.pointLight = new PointLightData();
                        block.pointLightHandle = VeilRenderSystem.renderer().getLightRenderer().addLight(block.pointLight
                                .setRadius(13f)
                                .setColor((float) 255 / 255, (float) 240 / 255, (float) 100 / 255)
                                .setPosition(position.x, position.y - 1, position.z)
                                .setBrightness(1.0f)
                        );
                    }
                } else {
                    if (block.pointLightHandle != null) {
                        block.pointLightHandle.free();
                        block.pointLightHandle = null;
                        block.pointLight = null;
                    }
                    block.setPlayingSound(false);
                }

            } else {
                if (block.pointLightHandle != null) {
                    block.pointLightHandle.free();
                    block.pointLightHandle = null;
                    block.pointLight = null;
                }
            }
        }
    }

}
