package com.sp.mixin;

import com.sp.SPBRevamped;
import com.sp.SPBRevampedClient;
import com.sp.networking.callbacks.ClientConnectionEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow private static Minecraft instance;

    @Shadow @Final public Options options;

    @Shadow @Final private PackRepository resourcePackRepository;

    @ModifyArg(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V"))
    private CameraType disableF5(CameraType perspective){
        if(SPBRevampedClient.getCutsceneManager().isPlaying) {
            return CameraType.FIRST_PERSON;
        }

        return perspective;
    }

    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"))
    private void onDisconnect(Screen screen, CallbackInfo ci){
        if(!(screen instanceof ProgressScreen)) {
            ClientConnectionEvents.DISCONNECT.invoker().onLoginDisconnect((Minecraft) (Object) this);
        }
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runTick(Z)V", shift = At.Shift.AFTER))
    private void enableDeferredResourcePack(CallbackInfo ci){
        if (instance != null && resourcePackRepository != null) {
            if(!resourcePackRepository.getSelectedPacks().contains(resourcePackRepository.getPack("veil:deferred"))) {
                SPBRevamped.LOGGER.info("Re-enabled Deferred Resourcepack");
                resourcePackRepository.addPack("veil:deferred");
                options.updateResourcePacks(resourcePackRepository);
            }
        }
    }
}
