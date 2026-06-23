package com.sp.mixin.pbr;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.sp.render.RenderLayers;
import com.sp.render.VertexFormats;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SectionCompiler.class)
public class ChunkBuilderMixin {

    @Redirect(method = "getOrBeginLayer", at = @At(value = "NEW", target = "(Lcom/mojang/blaze3d/vertex/ByteBufferBuilder;Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)Lcom/mojang/blaze3d/vertex/BufferBuilder;"))
    private BufferBuilder beginPBR(ByteBufferBuilder byteBuffer, VertexFormat.Mode mode, VertexFormat format, @Local RenderType renderType) {
        if (renderType == RenderLayers.getPbrLayer()) {
            return new BufferBuilder(byteBuffer, mode, VertexFormats.PBR);
        }
        return new BufferBuilder(byteBuffer, mode, format);
    }
}
