package com.sp.mixin.lightmap;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.init.BackroomsLevels;
import com.sp.render.PoolroomsDayCycle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightTexture.class)
public class PoolroomsSkylightColor {

    //This is done so that the Darkrooms stay the same color instead of also changing with the day night cycle
    @Redirect(method = {"updateLightTexture"}, at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;lerp(Lorg/joml/Vector3fc;F)Lorg/joml/Vector3f;", ordinal = 0))
    private Vector3f fixWeirdBlueDarknessAndChangeSunlightColor(Vector3f instance, Vector3fc other, float t, @Local ClientLevel clientWorld) {
        float f = clientWorld.getSkyDarken(1.0F);
        Vector3f baseColor = new Vector3f(f);

        if(clientWorld.dimension() == BackroomsLevels.POOLROOMS_WORLD_KEY) {
            return baseColor.mul(PoolroomsDayCycle.getLightColor());
        }

        return baseColor;
    }

}