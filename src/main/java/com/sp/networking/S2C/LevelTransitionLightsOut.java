package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.ModSounds;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LevelTransitionLightsOut {

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int time = buf.readInt();

        client.execute(()->{
            if(client.player != null) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(client.player);
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

                //Turn off the lights
                playerComponent.player.playNotifySound(ModSounds.LIGHTS_OUT, SoundSource.AMBIENT, 1, 1);
                SPBRevampedClient.getCutsceneManager().blackScreen.showBlackScreen(time, false, false);

                //PlaySound after black screen is over
                executorService.schedule(() -> {
                    playerComponent.player.playNotifySound(ModSounds.LIGHTS_ON, SoundSource.AMBIENT, 1, 1);
                    SPBRevampedClient.sendComponentSyncPacket(false, "teleporting");
                    executorService.shutdown();
                }, (time * 100L)/2, TimeUnit.MILLISECONDS);
            }
        });
    }

}
