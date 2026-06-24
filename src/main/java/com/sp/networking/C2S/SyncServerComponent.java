package com.sp.networking.C2S;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class SyncServerComponent {

    public static void receive(SyncServerComponentPayload payload, ServerPlayNetworking.Context context) {
        boolean readBoolean = payload.value();
        String component = payload.component();
        ServerPlayer player = context.player();

        context.server().execute(() -> {
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
