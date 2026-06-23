package com.sp.networking.C2S;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class SyncServerComponent {

    public static void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender){
        boolean readBoolean = buf.readBoolean();
        String component = buf.readUtf();

        server.execute(()->{
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);

            switch (component) {
                case "beingCaptured": playerComponent.setBeingCaptured(readBoolean); break;
                case "cutscene": playerComponent.setDoingCutscene(readBoolean); break;
                case "flashlight": playerComponent.setFlashLightOn(readBoolean); break;
                case "glitch": playerComponent.setShouldInflictGlitchDamage(readBoolean); break;
                case "teleporting": playerComponent.setTeleporting(readBoolean); break;
            }

            playerComponent.sync();
        });
    }

}
