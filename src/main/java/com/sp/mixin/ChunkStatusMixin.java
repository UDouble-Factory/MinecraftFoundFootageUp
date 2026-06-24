package com.sp.mixin;

import com.sp.world.generation.chunk_generator.BackroomsChunkGenerator;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/**
 * This mixin calls the {@link BackroomsChunkGenerator#generate(WorldGenRegion, ChunkAccess)} method.
 * It's what allows minecraft to generate the backrooms mazes
 */
@Mixin(ChunkStatusTasks.class)
public abstract class ChunkStatusMixin {

    @Inject(method = "generateFeatures", at = @At("HEAD"))
    private static void runGenerationTask(WorldGenContext worldGenContext, ChunkStep chunkStep, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        ChunkGenerator generator = worldGenContext.generator();

        if (generator instanceof BackroomsChunkGenerator backroomsChunkGenerator) {
            ServerLevel world = worldGenContext.level();
            WorldGenRegion chunkRegion = new WorldGenRegion(world, cache, chunkStep, chunk);
            backroomsChunkGenerator.generate(chunkRegion, chunk);
        }
    }
}
