package com.sp.init;

import com.sp.SPBRevamped;
import com.sp.block.SprintBlockSoundGroup;
import com.sp.block.custom.*;
import com.sp.block.custom.pipes.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SlabBlock;

public class ModBlocks {

    public static final Block VOID_BLOCK = registerBlock("void_block",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));

    //////Level 1 Stuff//////
    public static final Block WALL_BLOCK = registerBlock("wall_block",
            new WallBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).destroyTime(-1f).forceSolidOn().noParticlesOnBreak().sound(SprintBlockSoundGroup.WALL)));

    public static final Block WALL_BLOCK_2 = registerBlock("wall_block_2",
            new WallBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).destroyTime(-1f).forceSolidOn().noParticlesOnBreak().sound(SprintBlockSoundGroup.WALL)));


    public static final Block CARPET_BLOCK = registerBlock("carpet_block",
            new CarpetBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).destroyTime(-1f).forceSolidOn().noParticlesOnBreak().sound(SprintBlockSoundGroup.CARPET)));

    public static final Block CEILING_TILE = registerBlock("ceiling_tile",
            new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).destroyTime(-1f).forceSolidOn().noParticlesOnBreak().sound(SprintBlockSoundGroup.CEILING_TILE)));

    public static final Block GHOST_CEILING_TILE = registerBlock("ghost_ceiling_tile",
            new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).destroyTime(-1f).forceSolidOn().noParticlesOnBreak().noCollission()));


    public static final Block FLUORESCENT_LIGHT = registerBlock("fluorescent_light",
            new FluorescentLightBlock(FabricBlockSettings.copyOf(Blocks.GLASS).destroyTime(-1f).noParticlesOnBreak()));


    public static final Block WALL_ARROW_1 = registerBlock("arrow1",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_ARROW_2 = registerBlock("arrow2",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_ARROW_3 = registerBlock("arrow3",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_ARROW_4 = registerBlock("arrow4",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));


    public static final Block WALL_SMALL_1 = registerBlock("wall_small_1",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_SMALL_2 = registerBlock("wall_small_2",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_DRAWING_DOOR = registerBlock("wall_drawing_door",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_DRAWING_WINDOW = registerBlock("wall_drawing_window",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block drawingMarker = registerBlock("drawing_marker",
            new DrawingMarker(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak()));


    public static final Block RUG_1 = registerBlock("rug1",
            new Rug(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block RUG_2 = registerBlock("rug2",
            new Rug(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block CHAINFENCE = registerBlock("chain_fence",
            new ChainFence(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).destroyTime(-1f).noParticlesOnBreak()));

    public static final Block NEWSTAIRS = registerBlock("new_stairs",
            new NewStairs(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().sound(SprintBlockSoundGroup.CONCRETE)));

    public static final Block BRICKS = registerBlock("bricks",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak()));



    //////Level 2 and 3 Stuff//////
    public static final Block THIN_FLUORESCENT_LIGHT = registerBlock("thin_fluorescent_light",
            new ThinFluorescentLightBlock(FabricBlockSettings.copyOf(Blocks.GLASS).destroyTime(-1f).noParticlesOnBreak()));

    public static final Block EMERGENCY_LIGHT = registerBlock("emergency_light",
            new EmergencyLightBlock(FabricBlockSettings.copyOf(Blocks.GLASS).destroyTime(-1f).noParticlesOnBreak()));

    public static final Block BOTTOM_TRIM = registerBlock("bottom_trim",
            new BottomTrim(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noCollission().noOcclusion().noParticlesOnBreak().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block CONCRETE_BLOCK_1 = registerBlock("concrete1",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));

    public static final Block CONCRETE_BLOCK_2 = registerBlock("concrete2",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));

    public static final Block CONCRETE_BLOCK_5 = registerBlock("concrete5",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));

    public static final Block CONCRETE_BLOCK_6 = registerBlock("concrete6",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak().sound(SprintBlockSoundGroup.CONCRETE)));

    public static final Block CONCRETE_BLOCK_7 = registerBlock("concrete7",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));

    public static final Block CONCRETE_BLOCK_9 = registerBlock("concrete9",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));

    public static final Block CONCRETE_BLOCK_9_SLAB = registerBlock("concrete9slab",
            new SlabBlock(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));

    public static final Block CONCRETE_BLOCK_10 = registerBlock("concrete10",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));

    public static final Block CONCRETE_BLOCK_11 = registerBlock("concrete11",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));

    public static final Block CONCRETE_BLOCK_12 = registerBlock("concrete12",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));


    public static final Block THIN_PIPE = registerBlock("thin_pipe",
            new ThinPipe(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block THIN_PIPE_CORNER = registerBlock("thin_pipe_corner",
            new ThinPipeCorner(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block PIPE = registerBlock("pipe",
            new Pipe(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block PIPE_MIDDLE = registerBlock("pipe_middle",
            new Pipe(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block PIPE_CORNER = registerBlock("pipe_corner",
            new PipeCorner(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block BIG_PIPE = registerBlock("big_pipe",
            new Pipe(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block BIG_PIPE_MIDDLE = registerBlock("big_pipe_middle",
            new Pipe(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block SMALL_PIPE_SET = registerBlock("small_pipe_set",
            new SmallPipeSet(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));


    public static final Block WALL_TEXT_1 = registerBlock("wall_text_1",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_TEXT_2 = registerBlock("wall_text_2",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_TEXT_3 = registerBlock("wall_text_3",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_TEXT_4 = registerBlock("wall_text_4",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_TEXT_5 = registerBlock("wall_text_5",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_TEXT_6 = registerBlock("wall_text_6",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_TEXT_7 = registerBlock("wall_text_7",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_TEXT_8 = registerBlock("wall_text_8",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block WALL_TEXT_99 = registerBlock("wall_text_99",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().noCollission().sound(SprintBlockSoundGroup.SILENT)));


    public static final Block WOODEN_CRATE = registerBlock("wooden_crate",
            new WoodenCrate(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));



    //////Poolrooms Stuff//////
    public static final Block POOLROOMS_SKY_BLOCK = registerBlock("pool_sky",
            new GlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.SILENT)));

    public static final Block POOL_TILES = registerBlock("pool_tiles",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak().sound(SprintBlockSoundGroup.CONCRETE)));

    public static final Block POOL_TILE_WALL = registerBlock("pool_tile_wall",
            new PoolTileWall(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).forceSolidOn().noParticlesOnBreak()));

    public static final Block POOL_TILE_SLOPE = registerBlock("slope",
            new PoolTileSlopeBlock(FabricBlockSettings.copyOf(Blocks.STONE).destroyTime(-1f).noParticlesOnBreak().noOcclusion().sound(SprintBlockSoundGroup.CONCRETE)));

    public static final Block CEILINGLIGHT = registerBlock("ceiling_light",
            new CeilingLight(FabricBlockSettings.copyOf(Blocks.GLASS).destroyTime(-1f).noParticlesOnBreak()));


    ///////Infinite Fields Stuff//////
    public static final Block POWER_POLE_TOP = registerBlock("power_pole_top",
            new UtilityPole(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).destroyTime(-1f).noParticlesOnBreak(), 5));

    public static final Block POWER_POLE = registerBlock("power_pole",
            new UtilityPole(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).destroyTime(-1f).noParticlesOnBreak(), 5));

    public static final Block DIRT = registerBlock("dirt",
            new Block(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.GRASS2)));


    ///////Level 324 Stuff//////
    public static final Block ROAD = registerBlock("road",
            new Block(FabricBlockSettings.copyOf(ModBlocks.CONCRETE_BLOCK_11).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.CONCRETE)));

    public static final Block RED_DIRT = registerBlock("red_dirt",
            new Block(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.GRASS2)));

    public static final Block PLASTIC = registerBlock("plastic",
            new Block(FabricBlockSettings.copyOf(ModBlocks.CONCRETE_BLOCK_9).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.SHROOMLIGHT)));

    public static final Block NONE_REFLECTIVE_PLASTIC = registerBlock("none_reflective_plastic",
            new Block(FabricBlockSettings.copyOf(ModBlocks.CONCRETE_BLOCK_9).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.SHROOMLIGHT)));

    public static final Block RED_METAL_CASING = registerBlock("red_metal_casing",
            new RedMetalCasingBlock(FabricBlockSettings.copyOf(ModBlocks.CONCRETE_BLOCK_9).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.METAL)));

    public static final Block PILLAR = registerBlock("pillar",
            new PillarBlock(FabricBlockSettings.copyOf(ModBlocks.CONCRETE_BLOCK_9).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.SHROOMLIGHT)));

    public static final Block POLE = registerBlock("pole",
            new UtilityPole(FabricBlockSettings.copyOf(ModBlocks.CONCRETE_BLOCK_9).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.SHROOMLIGHT), 6));

    public static final Block LAMP = registerBlock("lamp",
            new LampBlock(FabricBlockSettings.copyOf(ModBlocks.CONCRETE_BLOCK_9).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.METAL)));

    public static final Block WINDOW = registerBlock("window",
            new WindowBlock(FabricBlockSettings.copyOf(Blocks.GLASS).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.GLASS)));

    public static final Block TINY_FLUORESCENT_LIGHT = registerBlock("tiny_fluorescent_light",
            new TinyFluorescentLightBlock(FabricBlockSettings.copyOf(Blocks.GLASS).destroyTime(-1f).noParticlesOnBreak()));

    public static final Block FLOOR_TILING = registerBlock("floor_tiling",
            new Block(FabricBlockSettings.copyOf(ModBlocks.CONCRETE_BLOCK_11).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.CONCRETE)));

    public static final Block DOUBLE_SIDED_SHELF = registerBlock("double_sided_shelf",
            new DoubleSidedShelfBlock(FabricBlockSettings.copyOf(ModBlocks.CONCRETE_BLOCK_11).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.CONCRETE)));

    public static final Block ONE_SIDED_SHELF = registerBlock("one_sided_shelf",
            new DoubleSidedShelfBlock(FabricBlockSettings.copyOf(ModBlocks.CONCRETE_BLOCK_11).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.WOOD)));

    public static final Block PAVEMENT = registerBlock("pavement",
            new Block(FabricBlockSettings.copyOf(ModBlocks.CONCRETE_BLOCK_11).destroyTime(-1f).noParticlesOnBreak().sound(SprintBlockSoundGroup.CONCRETE)));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(SPBRevamped.MOD_ID, name), block);
    }
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(SPBRevamped.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void init() {

    }

}
