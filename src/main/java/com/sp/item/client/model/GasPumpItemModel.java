package com.sp.item.client.model;

import com.sp.SPBRevamped;
import com.sp.item.custom.GasPumpItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GasPumpItemModel extends GeoModel<GasPumpItem> {
    @Override
    public ResourceLocation getModelResource(GasPumpItem animatable) {
        return new ResourceLocation(SPBRevamped.MOD_ID, "geo/blocks/staircase.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GasPumpItem animatable) {
        return new ResourceLocation(SPBRevamped.MOD_ID, "textures/block/staircase.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GasPumpItem animatable) {
        return new ResourceLocation(SPBRevamped.MOD_ID, "animations/staircase.animation.json");
    }
}