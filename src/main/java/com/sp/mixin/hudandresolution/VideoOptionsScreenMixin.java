package com.sp.mixin.hudandresolution;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.render.VhsAspectRatio;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(VideoSettingsScreen.class)
public class VideoOptionsScreenMixin {

    @Inject(method = "addOptions", at = @At("HEAD"), cancellable = true)
    private void addOptions(CallbackInfo ci) {
        if (!ConfigStuff.enableVHSAspectRatio) {
            return;
        }

        Window window = net.minecraft.client.Minecraft.getInstance().getWindow();
        Monitor monitor = window.findBestMonitor();

        Optional<VideoMode> optional = VhsAspectRatio.currentVhsVideoMode != null
                ? VhsAspectRatio.currentVhsVideoMode
                : window.getPreferredFullscreenVideoMode();
        int j2 = optional.map(this::findClosestVhsVideoModeIndex).orElse(-1);

        VhsAspectRatio.normalVideoMode = new OptionInstance<>(
                "spb-revamped.options.fullscreen.resolution",
                OptionInstance.noTooltip(),
                (prefix, value) -> {
                    if (monitor == null) {
                        return Component.translatable("options.fullscreen.unavailable");
                    } else {
                        return value == -1
                                ? Options.genericValueLabel(prefix, Component.translatable("options.fullscreen.current"))
                                : Options.genericValueLabel(prefix, Component.literal(VhsAspectRatio.vhsAspectRatiosList.get(value).write()).withStyle(ChatFormatting.GREEN));
                    }
                },
                new OptionInstance.IntRange(-1, monitor == null ? -1 : VhsAspectRatio.vhsAspectRatiosList.size() - 1),
                j2,
                value -> {
                    if (monitor != null) {
                        Optional<VideoMode> videoMode = value == -1 ? Optional.empty() : Optional.of(VhsAspectRatio.vhsAspectRatiosList.get(value));
                        window.setPreferredFullscreenVideoMode(videoMode);
                    }
                }
        );

        ((OptionsSubScreenAccessor) this).getList().addBig(VhsAspectRatio.normalVideoMode);
        ci.cancel();
    }

    @Unique
    private int findClosestVhsVideoModeIndex(VideoMode videoMode) {
        return VhsAspectRatio.vhsAspectRatiosList.indexOf(videoMode);
    }
}
