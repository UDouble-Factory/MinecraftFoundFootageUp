package com.sp.mixin;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevampedClient;
import com.sp.render.PoolroomsDayCycle;
import foundry.veil.api.client.render.shader.program.MutableUniformAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MutableUniformAccess.class, remap = false)
public interface MutableUniformAccessMixin {
    @Shadow void setVector(CharSequence name, float x, float y);

    @Shadow void setFloat(CharSequence name, float value);

    @Shadow void setMatrix(CharSequence name, Matrix4fc value);

    @Shadow void setInt(CharSequence name, int value);

    @Shadow void setVector(CharSequence name, float[] values);

    @Shadow void setVector(CharSequence name, Vector3fc value);

    /**
     * @author SpacePotato
     * @reason Because apparently you can't inject into interfaces :\
     */
    @Overwrite
    default void applyRenderSystem() {
        this.setMatrix("RenderModelViewMat", RenderSystem.getModelViewMatrix());
        this.setMatrix("RenderProjMat", RenderSystem.getProjectionMatrix());
        this.setVector("ColorModulator", RenderSystem.getShaderColor());
        this.setFloat("GlintAlpha", RenderSystem.getShaderGlintAlpha());
        this.setFloat("FogStart", RenderSystem.getShaderFogStart());
        this.setFloat("FogEnd", RenderSystem.getShaderFogEnd());
        this.setVector("FogColor", RenderSystem.getShaderFogColor());
        this.setInt("FogShape", RenderSystem.getShaderFogShape().getIndex());
        this.setMatrix("TextureMatrix", RenderSystem.getTextureMatrix());
        this.setFloat("GameTime", RenderSystem.getShaderGameTime());

        if(SPBRevampedClient.cameraBobOffset != null) {
            this.setVector("cameraBobOffset", SPBRevampedClient.cameraBobOffset);
        }

        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();
        this.setVector("ScreenSize", window.getScreenWidth(), window.getScreenHeight());

        TextureAtlas texture = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
        if(texture != null) {
            this.setFloat("atlasAspectRatio", (float) texture.getHeight() / texture.getWidth());
        }

        if(client.level != null && SPBRevampedClient.camera != null) {
            this.setFloat("sunsetTimer", PoolroomsDayCycle.getDayTime(client.level));
            SPBRevampedClient.setShadowUniforms((MutableUniformAccess) this, client.level);

            this.setFloat("warpAngle", SPBRevampedClient.getWarpTimer(client.level));
        }

    }

}
