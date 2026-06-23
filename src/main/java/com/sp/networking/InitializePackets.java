package com.sp.networking;

import com.sp.SPBRevamped;
import com.sp.networking.C2S.SeeActiveSkinwalkerSync;
import com.sp.networking.C2S.SyncServerComponent;
import com.sp.networking.C2S.TargetEntitySync;
import com.sp.networking.C2S.TargetEntitySyncPayload;
import com.sp.networking.S2C.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class InitializePackets {
    public static final ResourceLocation SEE_SKINWALKER_SYNC = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "see_skin");
    public static final ResourceLocation COMPONENT_SYNC = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "comp_sync");

    public static void registerC2SPackets() {
        PayloadTypeRegistry.playC2S().register(TargetEntitySyncPayload.TYPE, TargetEntitySyncPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TargetEntitySyncPayload.TYPE, TargetEntitySync::receive);
        ServerPlayNetworking.registerGlobalReceiver(SEE_SKINWALKER_SYNC, SeeActiveSkinwalkerSync::receive);
        ServerPlayNetworking.registerGlobalReceiver(COMPONENT_SYNC, SyncServerComponent::receive);
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
