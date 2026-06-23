package com.sp.networking;

import com.sp.SPBRevamped;
import com.sp.networking.C2S.SeeActiveSkinwalkerSync;
import com.sp.networking.C2S.SyncServerComponent;
import com.sp.networking.C2S.TargetEntitySync;
import com.sp.networking.S2C.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class InitializePackets {
    public static final ResourceLocation TARGET_ENTITY_SYNC = new ResourceLocation(SPBRevamped.MOD_ID, "targ_ent");
    public static final ResourceLocation SEE_SKINWALKER_SYNC = new ResourceLocation(SPBRevamped.MOD_ID, "see_skin");
    public static final ResourceLocation COMPONENT_SYNC = new ResourceLocation(SPBRevamped.MOD_ID, "comp_sync");

    public static final ResourceLocation SCREEN_SHAKE = new ResourceLocation(SPBRevamped.MOD_ID, "scr_shake");
    public static final ResourceLocation BLACK_SCREEN = new ResourceLocation(SPBRevamped.MOD_ID, "blk_screen");
    public static final ResourceLocation RELOAD_LIGHTS = new ResourceLocation(SPBRevamped.MOD_ID, "rl_lights");
    public static final ResourceLocation SOUND = new ResourceLocation(SPBRevamped.MOD_ID, "snd");
    public static final ResourceLocation LEVEL_TRANSITION_LIGHTSOUT = new ResourceLocation(SPBRevamped.MOD_ID, "ltos");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(TARGET_ENTITY_SYNC, TargetEntitySync::receive);
        ServerPlayNetworking.registerGlobalReceiver(SEE_SKINWALKER_SYNC, SeeActiveSkinwalkerSync::receive);
        ServerPlayNetworking.registerGlobalReceiver(COMPONENT_SYNC, SyncServerComponent::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SCREEN_SHAKE, InvokeScreenShakePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BLACK_SCREEN, InvokeBlackScreenPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(RELOAD_LIGHTS, ReloadLightsPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SOUND, SoundPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LEVEL_TRANSITION_LIGHTSOUT, LevelTransitionLightsOut::receive);
    }
}
