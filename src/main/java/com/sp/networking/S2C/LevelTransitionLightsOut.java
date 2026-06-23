package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.ModSounds;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.sounds.SoundSource;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LevelTransitionLightsOut {
    public static void receive(LevelTransitionLightsOutPayload payload, ClientPlayNetworking.Context context) {
        int time = payload.time();
        context.client().execute(() -> {
            if (context.client().player != null) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(context.client().player);
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

                playerComponent.player.playNotifySound(ModSounds.LIGHTS_OUT, SoundSource.AMBIENT, 1, 1);
                SPBRevampedClient.getCutsceneManager().blackScreen.showBlackScreen(time, false, false);

                executorService.schedule(() -> {
                    playerComponent.player.playNotifySound(ModSounds.LIGHTS_ON, SoundSource.AMBIENT, 1, 1);
                    SPBRevampedClient.sendComponentSyncPacket(false, "teleporting");
                    executorService.shutdown();
                }, (time * 100L) / 2, TimeUnit.MILLISECONDS);
            }
        });
    }
}
