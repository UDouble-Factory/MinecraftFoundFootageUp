package com.sp.mixin.pbr;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.sp.mixininterfaces.BlockMaterial;
import com.sp.render.VertexFormats;
import com.sp.render.pbr.BlockIdMap;
import com.sp.render.pbr.PbrRegistry;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

/**
 * This Mixin adds additional vertex data to blocks when rendering:<br>
 * MaterialID for every block<br>
 * Zoom and Resolution for PBR blocks
 */
@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements BlockMaterial {
    @Shadow public abstract void nextElement();

    @Shadow private ByteBuffer buffer;
    @Shadow private int nextElementByte;

    @Shadow public abstract void putFloat(int index, float value);

    @Unique boolean isRenderingBlock;
    @Unique Block currentBlock;
    @Unique VertexFormat currentFormat;

    @Override
    public void setCurrentBlock(Block block) {
        this.currentBlock = block;
    }

    @ModifyVariable(method = "begin", at = @At("HEAD"), argsOnly = true)
    private VertexFormat setFormat(VertexFormat format) {
        this.isRenderingBlock = false;
        currentFormat = format;

        //Rendering a normal block. Redirect it to include the Custom Material
        if (format == com.mojang.blaze3d.vertex.DefaultVertexFormat.BLOCK) {
            return setRendering(VertexFormats.BLOCKS);
        }

        //Rendering a PBR block. Redirect it to include the Zoom and resolution
        if (format == VertexFormats.PBR) {
            return setRendering(VertexFormats.PBR);
        }

        return format;
    }


    @Inject(method = "endVertex", at = @At("HEAD"))
    private void putBlockID(CallbackInfo ci){
        if (this.isRenderingBlock) {

            //Normal Block
            if(currentFormat == VertexFormats.BLOCKS) {
                this.buffer.putInt(this.nextElementByte, BlockIdMap.getBlockID(this.currentBlock));
                this.nextElement();
            }

            //PBR block
            else if(this.currentFormat == VertexFormats.PBR){
                PbrRegistry.PbrMaterial material = PbrRegistry.getMaterial(this.currentBlock);
                if (material == null) {
                    return;
                }

                this.putFloat(0, material.zoom());
                this.nextElement();

                this.putInt(0, material.textureResolution());
                this.nextElement();

                this.putInt(0, material.enableHeight() ? 1 : 0);
                this.nextElement();

                this.putFloat(0, material.depthMultiplier());
                this.nextElement();

            }
        }
    }

    @Unique
    private void putInt(int offset, int value){
        this.buffer.putInt(this.nextElementByte + offset, value);
    }

    @Unique
    private VertexFormat setRendering(VertexFormat format){
        this.isRenderingBlock = true;
        this.currentFormat = format;
        return format;
    }

}