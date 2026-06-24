package com.sp.block.entity;

import com.sp.init.ModBlockEntities;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class CeilingLightBlockEntity extends BlockEntity {
    AreaLightData light;
    LightRenderHandle<AreaLightData> lightHandle;
    float brightness;
    float angle;
    int ticks;
    RandomSource random = RandomSource.create();
    int randomInt;
    boolean on;

    public CeilingLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CEILING_LIGHT_BLOCK_ENTITY, pos, state);
        this.randomInt = random.nextIntBetweenInclusive(1, 4);
        this.on = true;
    }

    @Override
    public void setRemoved() {
        if (this.lightHandle != null && level.isClientSide) {
            this.lightHandle.free();
            this.lightHandle = null;
            this.light = null;
        }
        super.setRemoved();
    }

    public void tick(Level world, BlockPos pos, BlockState state) {
        if (!world.isClientSide) {
            return;
        }

        if (this.light != null) {
            return;
        }

        Vec3 position = pos.getCenter().add(-0.5, -0.06, 0);
        this.brightness = 2.58f;
        this.angle = 60.4f;
        this.light = new AreaLightData();
        this.light.getOrientation().rotateXYZ((float) Math.toRadians(-90d), 0, 0);
        this.light.getPosition().set(position.x, position.y, position.z);

        this.lightHandle = VeilRenderSystem.renderer().getLightRenderer().addLight(this.light
                .setBrightness(this.brightness)
                .setSize(0.9, 0.0)
                .setAngle((float) Math.toRadians(this.angle))
                .setDistance(15)
        );

//            if(!state.get(CeilingLight.STOPPED)) {
//                ticks++;
//
//                if (this.light == null && this.on) {
//                    Vec3d position = pos.toCenterPos().add(-0.5, -0.06, 0);
//                    this.brightness = 2.58f;
//                    this.angle = 60.4f;
//                    this.light = new AreaLight();
//                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.light
//                            .setBrightness(this.brightness)
//                            .setSize(0.9, 0.0)
//                            .setAngle((float) Math.toRadians(this.angle))
//                            .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-90d), 0, 0))
//                            .setPosition(new Vector3d(position.x, position.y, position.z))
//                            .setDistance(15)
//                    );
//                } else if (this.light != null) {
//                    if (ticks % this.randomInt == 0) {
//                        if (random.nextBoolean()) {
//                            this.on = false;
//                        } else {
//                            this.on = true;
//                        }
//                    }
//                }
//
//                if (this.light != null) {
//                    if (!this.on) {
//                        this.brightness = Math.max(this.brightness - 0.5f, 0.0f);
//                        this.angle = Math.max(this.angle - 4.0f, 0.0f);
//                    } else {
//                        this.brightness = 2.58f;
//                        this.angle = 60.4f;
//                    }
//
//                    this.light.setBrightness(this.brightness).setAngle((float) Math.toRadians(this.angle));
//                }
//            } else

    }
}
