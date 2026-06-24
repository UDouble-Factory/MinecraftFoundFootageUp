package com.sp.networking;

import com.sp.networking.C2S.*;
import com.sp.networking.S2C.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class InitializePackets {

    public static void registerC2SPackets() {
        PayloadTypeRegistry.playC2S().register(TargetEntitySyncPayload.TYPE, TargetEntitySyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SeeActiveSkinwalkerSyncPayload.TYPE, SeeActiveSkinwalkerSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SyncServerComponentPayload.TYPE, SyncServerComponentPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(TargetEntitySyncPayload.TYPE, TargetEntitySync::receive);
        ServerPlayNetworking.registerGlobalReceiver(SeeActiveSkinwalkerSyncPayload.TYPE, SeeActiveSkinwalkerSync::receive);
        ServerPlayNetworking.registerGlobalReceiver(SyncServerComponentPayload.TYPE, SyncServerComponent::receive);
    }

    public static void registerS2CPackets() {
        PayloadTypeRegistry.playS2C().register(ScreenShakePayload.TYPE, ScreenShakePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BlackScreenPayload.TYPE, BlackScreenPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ReloadLightsPayload.TYPE, ReloadLightsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SoundPayload.TYPE, SoundPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LevelTransitionLightsOutPayload.TYPE, LevelTransitionLightsOutPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(ScreenShakePayload.TYPE, InvokeScreenShakePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BlackScreenPayload.TYPE, InvokeBlackScreenPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ReloadLightsPayload.TYPE, ReloadLightsPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoundPayload.TYPE, SoundPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LevelTransitionLightsOutPayload.TYPE, LevelTransitionLightsOut::receive);
    }
}
