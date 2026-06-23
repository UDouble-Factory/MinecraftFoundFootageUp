package com.sp.mixin.renderlayer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.sp.render.RenderLayers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderType.class)
public class RenderLayerMixin {

    @ModifyReturnValue(method = "chunkBufferLayers", at = @At("RETURN"))
    private static List<RenderType> addRenderLayer(List<RenderType> original){
        List<RenderType> list = new ArrayList<>(original);
        list.add(RenderLayers.getPoolroomsSky());
        list.add(RenderLayers.getPbrLayer());
        return list;
    }

}
