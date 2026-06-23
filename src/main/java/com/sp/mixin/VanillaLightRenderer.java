package com.sp.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.init.BackroomsLevels;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = foundry.veil.impl.client.render.deferred.light.VanillaLightRenderer.class, remap = false)
public class VanillaLightRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/shader/program/ShaderProgram;setFloat(Ljava/lang/CharSequence;F)V"))
    private void noWeirdBrightnessThing(ShaderProgram instance, CharSequence name, float value, @Local(argsOnly = true) ClientLevel level, @Local Direction direction) {
        if(level.dimension() == BackroomsLevels.POOLROOMS_WORLD_KEY){
            instance.setFloat("LightShading" + direction.ordinal(), 0.9f);
        }

        instance.setFloat("LightShading" + direction.ordinal(), level.getShade(direction, true));
    }

}
