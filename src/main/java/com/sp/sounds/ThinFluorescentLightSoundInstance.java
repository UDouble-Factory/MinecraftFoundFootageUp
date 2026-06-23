package com.sp.sounds;

import com.sp.SPBRevampedClient;
import com.sp.block.custom.ThinFluorescentLightBlock;
import com.sp.block.entity.ThinFluorescentLightBlockEntity;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.world.levels.BackroomsLevelWithLights;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
public class ThinFluorescentLightSoundInstance extends AbstractTickableSoundInstance {
    private final BlockEntity entity;
    private final Player player;

    public ThinFluorescentLightSoundInstance(BlockEntity entity, Player player) {
        super(ModSounds.FLUORESCENT_LIGHT_HUM2, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.x = (float) entity.getBlockPos().getCenter().x;
        this.y = (float) entity.getBlockPos().getCenter().y;
        this.z = (float) entity.getBlockPos().getCenter().z;
        this.entity = entity;
        this.player = player;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public boolean isLooping() {
        return true;
    }

    @Override
    public void tick() {
        Level world = this.entity.getLevel();

        if(world != null) {
            boolean blackedOut = false;

            if ((BackroomsLevels.getLevel(world)).orElse(BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL) instanceof Level0BackroomsLevel level) {
                blackedOut = level.getLightState() == BackroomsLevelWithLights.LightState.BLACKOUT;
            }

            if ((BackroomsLevels.getLevel(world)).orElse(BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL) instanceof Level1BackroomsLevel level) {
                blackedOut = level.getLightState() == BackroomsLevelWithLights.LightState.BLACKOUT;
            }

            if (!this.entity.isRemoved() &&
                    entity.getBlockPos().closerToCenterThan(player.position(), 15.0f) &&
                    !blackedOut &&
                    ((ThinFluorescentLightBlockEntity) entity).getCurrentState().getValue(ThinFluorescentLightBlock.ON) &&
                    !((ThinFluorescentLightBlockEntity) entity).getCurrentState().getValue(ThinFluorescentLightBlock.BLACKOUT) &&
                    !SPBRevampedClient.blackScreen) {
                this.pitch = 1.0F;
                this.volume = 0.2F;
            } else {
                this.stop();
                ((ThinFluorescentLightBlockEntity) entity).setPlayingSound(false);
            }
        }
    }
}
