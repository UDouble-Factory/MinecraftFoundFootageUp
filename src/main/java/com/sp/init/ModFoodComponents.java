package com.sp.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodComponents {
    public static final FoodProperties CANNED_FOOD = new FoodProperties.Builder().nutrition(5).saturationModifier(0.8f).effect(new MobEffectInstance(MobEffects.POISON, 200, 2), 0.05f).build();
    public static final FoodProperties BACKSHROOM = new FoodProperties.Builder().nutrition(3).saturationModifier(0.4f).effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 2), 0.1f).build();
}
