package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ReloadLightsPacket {
    public static void receive(ReloadLightsPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            VeilRenderer renderer = VeilRenderSystem.renderer();
            renderer.getLightRenderer().free();
            if (SPBRevampedClient.getCutsceneManager().started) {
                SPBRevampedClient.getCutsceneManager().blackScreen.showBlackScreen(60, true, false);
            }
        });
    }
}
