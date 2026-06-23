package com.sp.mixin.lightshadows;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevampedClient;
import com.sp.init.BackroomsLevels;
import com.sp.render.ShadowMapRenderer;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightRenderer.class)
public class LightRendererMixin {

    @Inject(method = "applyShader", at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/shader/program/ShaderProgram;bind()V"), remap=false)
    private void setUniforms(CallbackInfo ci, @Local ShaderProgram shader) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if(player != null && client.level != null) {
            ResourceKey<Level> registryKey = player.level().dimension();
            if(registryKey == BackroomsLevels.LEVEL0_WORLD_KEY && !SPBRevampedClient.getCutsceneManager().isPlaying){
                setShadowUniforms(shader);
                shader.setInt("InOverWorld", registryKey == Level.OVERWORLD ? 1 : 0);
                shader.setInt("ShouldRender", 1);
            } else {
                shader.setInt("InOverWorld", registryKey == Level.OVERWORLD ? 1 : 0);
                shader.setInt("ShouldRender", 0);
            }
        }
        shader.setFloat("gameTime", RenderSystem.getShaderGameTime());
    }

    @Unique
    public void setShadowUniforms(ShaderProgram shaderProgram) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        shaderProgram.setMatrix("viewMatrix", ShadowMapRenderer.createShadowModelView(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z, true).last().pose());
        shaderProgram.setMatrix("orthographMatrix", ShadowMapRenderer.createProjMat());
    }
}
