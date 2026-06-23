package com.sp.render.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevampedClient;
import com.sp.render.camera.CutsceneManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TitleText implements HudRenderCallback {
    public TitleText (){
    }

    @Override
    public void onHudRender(GuiGraphics drawContext, float tickDelta) {
        CutsceneManager cutsceneManager = SPBRevampedClient.getCutsceneManager();

        if(cutsceneManager.backroomsBySP && !cutsceneManager.blackScreen.isBlackScreen){
            Component text = Component.translatable("intro.backrooms");
            Component text2 = Component.translatable("intro.credit").append(Component.literal("SpacePotato").withStyle(ChatFormatting.GOLD));
            Minecraft client = Minecraft.getInstance();
            if(client != null){
                int width = client.getWindow().getGuiScaledWidth();
                int height = (client.getWindow().getGuiScaledHeight());

                drawContext.pose().pushPose();
                drawContext.pose().translate((float)(width / 2), (float)(height / 2), 0.0F);
                RenderSystem.enableBlend();
                drawContext.pose().pushPose();
                drawContext.pose().scale(2.5F, 2.5F, 2.5F);
                int w = client.font.width(text);
                int h = client.font.lineHeight;
                drawContext.drawString(client.font, text, -w/2,  -h/2, 0xFFFFFF, true);
                drawContext.pose().popPose();

                drawContext.pose().pushPose();
                drawContext.pose().scale(1.0F, 1.0F, 1.0F);
                int w2 = client.font.width(text2);
                int h2 = client.font.lineHeight;
                drawContext.drawString(client.font, text2, -w2/2,  10, 0xFFFFFF, true);
                drawContext.pose().popPose();

                RenderSystem.disableBlend();
                drawContext.pose().popPose();

            }
        }
    }
}
