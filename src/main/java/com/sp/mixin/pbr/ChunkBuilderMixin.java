package com.sp.mixin.pbr;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.sp.render.RenderLayers;
import com.sp.render.VertexFormats;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public class ChunkBuilderMixin {

    @Redirect(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk;beginLayer(Lcom/mojang/blaze3d/vertex/BufferBuilder;)V"))
    private void beginPBR(ChunkRenderDispatcher.RenderChunk instance, BufferBuilder buffer, @Local RenderType renderLayer){
        if(renderLayer == RenderLayers.getPbrLayer()){
            buffer.begin(VertexFormat.Mode.QUADS, VertexFormats.PBR);
        } else {
            buffer.begin(VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.BLOCK);
        }
    }

}
