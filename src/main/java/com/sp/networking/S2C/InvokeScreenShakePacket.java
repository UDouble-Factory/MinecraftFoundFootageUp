package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class InvokeScreenShakePacket {
    public static void receive(ScreenShakePayload payload, ClientPlayNetworking.Context context) {
        double speed = payload.speed();
        double trauma = payload.trauma();
        context.client().execute(() -> {
            SPBRevampedClient.getCameraShake().noiseSpeed = speed;
            SPBRevampedClient.getCameraShake().trauma = trauma;
        });
    }
}
