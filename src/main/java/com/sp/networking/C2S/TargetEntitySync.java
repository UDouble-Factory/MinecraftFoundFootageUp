package com.sp.networking.C2S;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class TargetEntitySync {
    public static void receive(TargetEntitySyncPayload payload, ServerPlayNetworking.Context context) {
        int targetID = payload.entityId();
        ServerPlayer player = context.player();

        context.server().execute(() -> {
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);

            if (targetID == -1) {
                playerComponent.setTargetEntity(null);
            } else {
                playerComponent.setTargetEntity(player.level().getEntity(targetID));
            }
        });
    }
}
