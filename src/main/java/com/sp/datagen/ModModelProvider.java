package com.sp.datagen;

import com.sp.init.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createTrivialCube(ModBlocks.CARPET_BLOCK);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CONCRETE_BLOCK_1);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CONCRETE_BLOCK_2);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CONCRETE_BLOCK_5);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CONCRETE_BLOCK_6);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CONCRETE_BLOCK_7);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CONCRETE_BLOCK_10);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CONCRETE_BLOCK_11);
        blockStateModelGenerator.createTrivialCube(ModBlocks.ROAD);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CONCRETE_BLOCK_12);
        blockStateModelGenerator.createTrivialCube(ModBlocks.POOL_TILES);

        BlockModelGenerators.BlockFamilyProvider concretePool = blockStateModelGenerator.family(ModBlocks.CONCRETE_BLOCK_9);
        concretePool.slab(ModBlocks.CONCRETE_BLOCK_9_SLAB);

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {

    }
}
