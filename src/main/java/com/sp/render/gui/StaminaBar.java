package com.sp.render.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static net.minecraft.util.Mth.floor;

public class StaminaBar implements HudRenderCallback {
    private static final ResourceLocation STAMINA_ICONS = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "textures/gui/stamina.png");
    private Long fadeStart;
    private float fadeTimer;


    @Override
    public void onHudRender(GuiGraphics drawContext, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;

        if(player != null) {
            PlayerComponent component = InitializeComponents.PLAYER.get(player);
            int width = 44;
            int height = 64;
            RenderSystem.enableBlend();
            if(component.getStamina() < 300){
                this.fadeStart = null;
                this.fadeTimer = 0.0f;

                drawContext.pose().pushPose();
                drawContext.pose().translate((float)(drawContext.guiWidth() / 2), (float)(drawContext.guiHeight() / 2), 0.0F);
                drawContext.pose().scale(0.2f,0.2f,0.2f);
                drawContext.setColor(1.0f, 1.0f, 1.0f, 0.25f);

                float normalizedStamina = 1.0f - (float) component.getStamina() / 300;
                int offset = floor(normalizedStamina * 64);

                drawContext.blit(STAMINA_ICONS, width/2, -height/2, width, 0, 64, height);
                drawContext.blit(STAMINA_ICONS, width/2 + 4, -height/2 + offset, 0, offset, width, height);

                drawContext.setColor(1.0F, 1.0F, 1.0F, 1.0F);

                drawContext.pose().popPose();
            } else if(this.fadeTimer < 1.0f) {
                if(this.fadeStart == null){
                    this.fadeStart = Util.getMillis();
                }
                this.fadeTimer = Math.min((float) (Util.getMillis() - this.fadeStart) / 1000L, 1.0f);

                drawContext.pose().pushPose();
                drawContext.pose().translate((float)(drawContext.guiWidth() / 2), (float)(drawContext.guiHeight() / 2), 0.0F);
                drawContext.pose().scale(0.2f,0.2f,0.2f);

                drawContext.setColor(1.0f, 1.0f, 1.0f, 0.25f * (1.0f - this.fadeTimer));
                drawContext.blit(STAMINA_ICONS, width/2, -height/2, width, 0, 64, height);
                drawContext.blit(STAMINA_ICONS, width/2 + 4, -height/2, 0, 0, width, height);

                drawContext.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                drawContext.pose().popPose();

            }
            RenderSystem.disableBlend();

        }
    }
}
