package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class InvokeBlackScreenPacket {
    public static void receive(BlackScreenPayload payload, ClientPlayNetworking.Context context) {
        int duration = payload.duration();
        boolean shouldPauseSounds = payload.shouldPauseSounds();
        boolean noEscape = payload.noEscape();
        context.client().execute(() ->
            SPBRevampedClient.getCutsceneManager().blackScreen.showBlackScreen(duration, shouldPauseSounds, noEscape)
        );
    }
}
