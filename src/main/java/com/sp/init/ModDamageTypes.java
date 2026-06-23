package com.sp.init;

import com.sp.SPBRevamped;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> ACID_WATER = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(SPBRevamped.MOD_ID, "acid_water"));
    public static final ResourceKey<DamageType> SMILER = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(SPBRevamped.MOD_ID, "smiler"));

    public static DamageSource of(Level world, ResourceKey<DamageType> key){
        return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key));
    }

}
