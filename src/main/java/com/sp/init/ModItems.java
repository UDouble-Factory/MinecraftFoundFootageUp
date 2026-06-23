package com.sp.init;

import com.sp.SPBRevamped;
import com.sp.item.custom.Backshroom;
import com.sp.item.custom.CannedFood;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final Item BACKSHROOM = registerItem("backshroom",
            new Backshroom(new FabricItemSettings().food(ModFoodComponents.BACKSHROOM)));

    public static final Item CANNED_FOOD = registerItem("canned_food",
            new CannedFood(new FabricItemSettings().food(ModFoodComponents.CANNED_FOOD)));

    private static Item registerItem(String name, Item item){
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(SPBRevamped.MOD_ID, name), item);
    }

    public static void registerModItems() {
        SPBRevamped.LOGGER.info("Registering Mod Items for " + SPBRevamped.MOD_ID);
    }
}
