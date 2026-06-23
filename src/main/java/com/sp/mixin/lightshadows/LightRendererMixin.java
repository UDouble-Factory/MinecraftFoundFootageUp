package com.sp.mixin.lightshadows;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevampedClient;
import com.sp.init.BackroomsLevels;
import com.sp.render.ShadowMapRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.renderer.InstancedLightRenderer;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InstancedLightRenderer.class, remap = false)
public class LightRendererMixin {

    @Inject(method = "renderLights", at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/vertex/VertexArray;bind()V"))
    private void setUniforms(LightRenderer lightRenderer, CallbackInfo ci) {
        ShaderProgram shader = VeilRenderSystem.getShader();
        if (shader == null) return;

        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player != null && client.level != null) {
            ResourceKey<Level> registryKey = player.level().dimension();
            if (registryKey == BackroomsLevels.LEVEL0_WORLD_KEY && !SPBRevampedClient.getCutsceneManager().isPlaying) {
                setShadowUniforms(shader);
                var uInOverWorld = shader.getUniform("InOverWorld");
                if (uInOverWorld != null) uInOverWorld.setInt(registryKey == Level.OVERWORLD ? 1 : 0);
                var uShouldRender = shader.getUniform("ShouldRender");
                if (uShouldRender != null) uShouldRender.setInt(1);
            } else {
                var uInOverWorld = shader.getUniform("InOverWorld");
                if (uInOverWorld != null) uInOverWorld.setInt(registryKey == Level.OVERWORLD ? 1 : 0);
                var uShouldRender = shader.getUniform("ShouldRender");
                if (uShouldRender != null) uShouldRender.setInt(0);
            }
        }
        var uGameTime = shader.getUniform("gameTime");
        if (uGameTime != null) uGameTime.setFloat(RenderSystem.getShaderGameTime());
    }

    @Unique
    private void setShadowUniforms(ShaderProgram shader) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        ShadowMapRenderer.createShadowModelView(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z, true)
                .last().pose();
        SPBRevampedClient.setShadowUniforms(shader, Minecraft.getInstance().level);
    }
}
