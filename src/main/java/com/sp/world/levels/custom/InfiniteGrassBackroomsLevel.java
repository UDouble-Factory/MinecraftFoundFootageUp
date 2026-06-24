package com.sp.world.levels.custom;

import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.BackroomsLevels;
import com.sp.world.events.infinite_grass.InfiniteGrassAmbience;
import com.sp.world.generation.chunk_generator.InfGrassChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InfiniteGrassBackroomsLevel extends BackroomsLevel {

    public InfiniteGrassBackroomsLevel() {
        super("inf_grass", InfGrassChunkGenerator.CODEC, new Vec3(0, 31, 0), BackroomsLevels.INFINITE_FIELD_WORLD_KEY);
    }

    @Override
    public void register() {
        super.register();

        this.registerEvent("ambience", InfiniteGrassAmbience::new);

        this.registerTransition((world, playerComponent, from) -> {
            List<LevelTransition> playerList = new ArrayList<>();

            if (from instanceof InfiniteGrassBackroomsLevel && playerComponent.player.position().y > 57.5 && playerComponent.player.onGround()) {
                playerList.add(getOverworldTransition(playerComponent));
            }

            return playerList;
        }, this.getLevelId() + "->" + BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL.getLevelId());
    }

    private LevelTransition getOverworldTransition(PlayerComponent playerComponent) {
        Optional<Vec3> optional = Optional.empty();
        BlockPos blockPos1 = new BlockPos(0, 64, 0);
        if (playerComponent.player instanceof ServerPlayer) {
            BlockPos blockPos = ((ServerPlayer) playerComponent.player).getRespawnPosition();
            ServerLevel serverWorld = playerComponent.player.level().getServer().getLevel(Level.OVERWORLD);

            if (serverWorld != null && blockPos != null) {
                ServerPlayer serverPlayer = (ServerPlayer) playerComponent.player;
                DimensionTransition transition = serverPlayer.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING);
                if (!transition.missingRespawnBlock()) {
                    optional = Optional.of(transition.pos());
                }
            }

            Level overworld = playerComponent.player.level().getServer().getLevel(Level.OVERWORLD);
            blockPos1 = overworld.getSharedSpawnPos();
        }

        return new LevelTransition(
                1,
                (teleport, tick) -> {
                    if (!teleport.playerComponent().player.level().isClientSide()) {
                        teleport.playerComponent().loadPlayerSavedInventory();
                    }
                },
                new CrossDimensionTeleport(
                        playerComponent,
                        optional.orElse(blockPos1.getCenter()),
                        this,
                        BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL),
                (teleport, tick) -> {});
    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(1000, 1200);
    }

    @Override
    public void writeToNbt(CompoundTag nbt) {

    }

    @Override
    public void readFromNbt(CompoundTag nbt) {

    }

    @Override
    public void transitionOut(CrossDimensionTeleport crossDimensionTeleport) {
    }

    @Override
    public void transitionIn(CrossDimensionTeleport crossDimensionTeleport) {
        crossDimensionTeleport.playerComponent().player.fallDistance = 0;
    }

    @Override
    public BoolTextPair allowsTorch() {
        return new BoolTextPair(false, Component.translatable("spb-revamped.flashlight.wet1").append(Component.translatable("spb-revamped.flashlight.wet2").withStyle(ChatFormatting.RED)));
    }

    @Override
    public boolean hasVanillaLighting() {
        return true;
    }
}
