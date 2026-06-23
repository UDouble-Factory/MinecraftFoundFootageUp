package com.sp.init;

import com.sp.SPBRevamped;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.entity.custom.SmilerEntity;
import com.sp.entity.custom.WalkerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    public static final EntityType<SkinWalkerEntity> SKIN_WALKER_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(SPBRevamped.MOD_ID, "skin_walker"),
            FabricEntityTypeBuilder.create(MobCategory.MONSTER, SkinWalkerEntity::new).dimensions(EntityDimensions.fixed(0.6f, 2.0f)).build());

    public static final EntityType<SmilerEntity> SMILER_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(SPBRevamped.MOD_ID, "smiler"),
            FabricEntityTypeBuilder.create(MobCategory.MONSTER, SmilerEntity::new).dimensions(EntityDimensions.fixed(0.6f, 2.0f)).build());

    public static final EntityType<WalkerEntity> WALKER_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(SPBRevamped.MOD_ID, "walker"),
            FabricEntityTypeBuilder.create(MobCategory.MONSTER, WalkerEntity::new).dimensions(EntityDimensions.fixed(2.0f, 2.0f)).build());
}
