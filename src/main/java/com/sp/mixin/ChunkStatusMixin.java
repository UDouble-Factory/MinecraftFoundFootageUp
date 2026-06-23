package com.sp.mixin;

import com.sp.world.generation.chunk_generator.BackroomsChunkGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * This mixin calls the {@link  BackroomsChunkGenerator#generate(WorldGenLevel, ChunkAccess)} method.
 * It's what allows minecraft to generate the backrooms mazes
 */
@Mixin(ChunkStatus.class)
public abstract class ChunkStatusMixin {
    @Inject(method = "method_38284(Lnet/minecraft/world/level/chunk/ChunkStatus;Ljava/util/concurrent/Executor;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/server/level/ThreadedLevelLightEngine;Ljava/util/function/Function;Ljava/util/List;Lnet/minecraft/world/level/chunk/ChunkAccess;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private static void runGenerationTask(ChunkStatus targetStatus, Executor executor, ServerLevel world, ChunkGenerator generator, StructureTemplateManager structureTemplateManager, ThreadedLevelLightEngine lightingProvider, Function fullChunkConverter, List<ChunkAccess> chunks, ChunkAccess chunk, CallbackInfoReturnable<CompletableFuture> cir) {

        if (generator instanceof BackroomsChunkGenerator backroomsChunkGenerator) {
            WorldGenRegion chunkRegion = new WorldGenRegion(world, chunks, targetStatus, backroomsChunkGenerator.getPlacementRadius());
            backroomsChunkGenerator.generate(chunkRegion, chunk);
        }

    }
}
