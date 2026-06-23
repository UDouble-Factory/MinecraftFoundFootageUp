package com.sp.mixin.customatlas;

import com.sp.SPBRevamped;
import com.sp.render.RenderLayers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * This method adds both the Normal texture atlas, and Height texture atlas for PBR materials.
 * It also changes the vanilla block atlas to remove height and normal textures
 */
@Mixin(ModelManager.class)
public class BakedModelManagerMixin {

    @Mutable
    @Shadow
    @Final
    private static Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES;



    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addPBRAtlas(CallbackInfo ci){
        VANILLA_ATLASES = new HashMap<>(VANILLA_ATLASES);
        VANILLA_ATLASES.put(
                RenderLayers.NORMAL_ATLAS_TEXTURE,
                ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "normal")
        );
        VANILLA_ATLASES.put(
                RenderLayers.HEIGHT_ATLAS_TEXTURE,
                ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "height")
        );
        VANILLA_ATLASES.put(
                TextureAtlas.LOCATION_BLOCKS,
                ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "blocks")
        );
    }

}
