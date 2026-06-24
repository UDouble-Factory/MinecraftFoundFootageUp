package com.sp.networking.C2S;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class SeeActiveSkinwalkerSync {

    public static void receive(SeeActiveSkinwalkerSyncPayload payload, ServerPlayNetworking.Context context) {
        boolean canSeeSkinwalker = payload.canSeeSkinwalker();
        ServerPlayer player = context.player();

        context.server().execute(() -> {
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
            playerComponent.setCanSeeActiveSkinWalkerTarget(canSeeSkinwalker);
        });
    }
}
