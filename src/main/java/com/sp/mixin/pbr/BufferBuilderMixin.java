package com.sp.mixin.pbr;

import com.mojang.blaze3d.vertex.*;
import com.sp.mixininterfaces.BlockMaterial;
import com.sp.render.VertexFormats;
import com.sp.render.pbr.BlockIdMap;
import com.sp.render.pbr.PbrRegistry;
import net.minecraft.world.level.block.Block;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This Mixin adds additional vertex data to blocks when rendering:<br>
 * MaterialID for every block<br>
 * Zoom and Resolution for PBR blocks
 */
@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements BlockMaterial {

    @Shadow private long vertexPointer;
    @Shadow private int elementsToFill;
    @Shadow @Final private int[] offsetsByElement;
    @Shadow @Final private VertexFormat format;

    @Unique boolean isRenderingBlock;
    @Unique Block currentBlock;

    @Override
    public void setCurrentBlock(Block block) {
        this.currentBlock = block;
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static VertexFormat redirectFormat(VertexFormat format) {
        if (format == DefaultVertexFormat.BLOCK) {
            return VertexFormats.BLOCKS;
        }
        return format;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(ByteBufferBuilder byteBufferBuilder, VertexFormat.Mode mode, VertexFormat vertexFormat, CallbackInfo ci) {
        this.isRenderingBlock = this.format == VertexFormats.BLOCKS || this.format == VertexFormats.PBR;
    }

    @Inject(method = "addVertex(FFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;", at = @At("TAIL"))
    private void putBlockData(float x, float y, float z, CallbackInfoReturnable<?> cir) {
        if (!this.isRenderingBlock || this.vertexPointer == -1L) {
            return;
        }

        if (this.format == VertexFormats.BLOCKS) {
            putCustomFloat(VertexFormats.MATERIAL_ELEMENT, Float.intBitsToFloat(BlockIdMap.getBlockID(this.currentBlock)));
        } else if (this.format == VertexFormats.PBR) {
            PbrRegistry.PbrMaterial material = PbrRegistry.getMaterial(this.currentBlock);
            if (material == null) {
                putCustomFloat(VertexFormats.ZOOM_ELEMENT, 0.0f);
                putCustomFloat(VertexFormats.RESOLUTION_ELEMENT, 0.0f);
                putCustomFloat(VertexFormats.ENABLE_HEIGHT_ELEMENT, 0.0f);
                putCustomFloat(VertexFormats.DEPTH_MULTIPLIER_ELEMENT, 0.0f);
                return;
            }
            putCustomFloat(VertexFormats.ZOOM_ELEMENT, material.zoom());
            putCustomFloat(VertexFormats.RESOLUTION_ELEMENT, Float.intBitsToFloat(material.textureResolution()));
            putCustomFloat(VertexFormats.ENABLE_HEIGHT_ELEMENT, material.enableHeight() ? 1.0f : 0.0f);
            putCustomFloat(VertexFormats.DEPTH_MULTIPLIER_ELEMENT, material.depthMultiplier());
        }
    }

    @Unique
    private void putCustomFloat(VertexFormatElement element, float value) {
        int mask = element.mask();
        int currentFill = this.elementsToFill;
        if ((currentFill & mask) == 0) {
            return;
        }
        this.elementsToFill = currentFill & ~mask;
        long ptr = this.vertexPointer + (long) this.offsetsByElement[element.id()];
        MemoryUtil.memPutFloat(ptr, value);
    }
}
