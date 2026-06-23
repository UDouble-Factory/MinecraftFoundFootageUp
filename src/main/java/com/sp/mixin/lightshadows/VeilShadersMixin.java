package com.sp.mixin.lightshadows;

import foundry.veil.api.client.render.shader.VeilShaders;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VeilShaders.class)
public class VeilShadersMixin {

    @Shadow
    public static final ResourceLocation LIGHT_POINT = new ResourceLocation("spbrevamped", "point");

    @Shadow
    public static final ResourceLocation LIGHT_AREA = new ResourceLocation("spbrevamped", "area");
}
