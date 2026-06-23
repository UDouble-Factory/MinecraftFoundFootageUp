package com.sp.block.entity;

import com.sp.clientWrapper.ClientWrapper;
import com.sp.init.ModBlockEntities;
import com.sp.sounds.EmergencyAlarmSoundInstance;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.client.render.light.data.PointLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EmergencyLightBlockEntity extends BlockEntity {
    public final float randomOffset;
    public boolean initEmergencyLights = false;
    public boolean playingEmergencyAlarm = false;
    public boolean initNormalLights = false;

    public EmergencyAlarmSoundInstance emergencyAlarmSoundInstance;
    public AreaLightData areaLight1;
    public AreaLightData areaLight2;
    public PointLightData pointLight;
    public LightRenderHandle<AreaLightData> areaLight1Handle;
    public LightRenderHandle<AreaLightData> areaLight2Handle;
    public LightRenderHandle<PointLightData> pointLightHandle;

    public EmergencyLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EMERGENCY_LIGHT_BLOCK_ENTITY, pos, state);
        this.randomOffset = RandomSource.create().nextFloat() * 180;
    }

    @Override
    public void setRemoved() {
        if (this.initEmergencyLights){
            this.removeEmergencyLights();
        }

        if(this.initNormalLights){
            this.removeNormalLights();
        }

        super.setRemoved();
    }

    public void tick(Level world, BlockPos pos, BlockState state) {
        if (!world.isClientSide) {
            return;
        }

        ClientWrapper.tickEmergencyLight(world, pos, state, this);
    }

    public void setEmergencyAlarm(boolean b) {
        this.playingEmergencyAlarm = b;
    }

    public void removeEmergencyLights() {
        if (this.initEmergencyLights && this.level.isClientSide) {
            this.areaLight1Handle.free();
            this.areaLight1Handle = null;
            this.areaLight1 = null;
            this.areaLight2Handle.free();
            this.areaLight2Handle = null;
            this.areaLight2 = null;
            this.pointLightHandle.free();
            this.pointLightHandle = null;
            this.pointLight = null;
            this.initEmergencyLights = false;
        }
    }

    public void removeNormalLights() {
        if(this.initNormalLights && this.level.isClientSide) {
            this.pointLightHandle.free();
            this.pointLightHandle = null;
            this.pointLight = null;
            this.initNormalLights = false;
        }
    }
}
