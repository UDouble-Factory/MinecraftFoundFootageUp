package com.sp.render.pbr;

import com.sp.SPBRevamped;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * In case I need a certain block to be bound to a certain ID. Kinda like how iris does it.<br>
 * Also keeps me from making a new render layer every time I need to access only a specific block. <br>
 * Lets me easily access certain blocks when running the shaders.
 */
public class BlockIdMap {
    private static Object2IntMap<Block> BlockIDs = new Object2IntOpenHashMap<>();
    private static List<BlockIDCallback> BlockIDCallbacks = new ArrayList<>();
    public static boolean init = false;
    private static int numOfBlocks;

    public static void init() {
        BlockIDs.clear();
        init = true;

        //For every block, assign an ID
        for(Block block : BuiltInRegistries.BLOCK){
            RenderType renderLayer = ItemBlockRenderTypes.getChunkRenderType(block.defaultBlockState());
            if(renderLayer == RenderType.solid()){
                BlockIDs.put(block, 0);
            } else if(renderLayer == RenderType.cutout()){
                BlockIDs.put(block, 1);
            } else if(renderLayer == RenderType.cutoutMipped()){
                BlockIDs.put(block, 2);
            } else if(renderLayer == RenderType.translucent()){
                BlockIDs.put(block, 3);
            }
            numOfBlocks++;
        }

        SPBRevamped.LOGGER.info("Loaded {} Default Block IDs", numOfBlocks);

        for (BlockIDCallback blockIDCallback : BlockIDCallbacks) {
            blockIDCallback.apply(BlockIDs);
        }
    }

    public static int getBlockID(Block block) {
        if (BlockIDs.isEmpty() || !init) {
            return -1;
        }

        return BlockIDs.getOrDefault(block, -1);
    }

    /**
     * Register a callback that will be called when the block IDs are initialized or updated.
     * Anything under 23 is already taken by either another modded block or a vanilla rendering layers <i>like the world border for example.</i>
     *
     * @param function The callback function to register.
     */
    public static void registerBlockID(BlockIDCallback function) {
        BlockIDCallbacks.add(function);
    }

    @FunctionalInterface
    public interface BlockIDCallback {
        void apply(Object2IntMap<Block> blockIDs);
    }
}
