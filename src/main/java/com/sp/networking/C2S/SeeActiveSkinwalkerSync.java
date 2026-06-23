package com.sp.networking.C2S;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class SeeActiveSkinwalkerSync {

    public static void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender){

        boolean canSeeSkinwalker = buf.readBoolean();

        server.execute(()->{
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
            playerComponent.setCanSeeActiveSkinWalkerTarget(canSeeSkinwalker);
        });
    }
}
