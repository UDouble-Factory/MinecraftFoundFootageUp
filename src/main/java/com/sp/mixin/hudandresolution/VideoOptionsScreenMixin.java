package com.sp.mixin.hudandresolution;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.render.VhsAspectRatio;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(VideoSettingsScreen.class)
public class VideoOptionsScreenMixin {
    @Shadow private OptionsList list;

    @Redirect(method = "init", at = @At(value = "NEW", target = "(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplier;Lnet/minecraft/client/OptionInstance$CaptionBasedToString;Lnet/minecraft/client/OptionInstance$ValueSet;Ljava/lang/Object;Ljava/util/function/Consumer;)Lnet/minecraft/client/OptionInstance;"))
    private OptionInstance<Integer> redirectOptionConstructor(String key, OptionInstance.TooltipSupplier tooltipFactory, OptionInstance.CaptionBasedToString valueTextGetter, OptionInstance.ValueSet callbacks, Object defaultValue, Consumer changeCallback, @Local Window window, @Local Monitor monitor, @Local(ordinal = 1) int j){
        int j2;
        if(ConfigStuff.enableVHSAspectRatio){
            Optional<VideoMode> optional = VhsAspectRatio.currentVhsVideoMode != null ? VhsAspectRatio.currentVhsVideoMode : window.getPreferredFullscreenVideoMode();
            j2 = (Integer)optional.map(this::findClosestVhsVideoModeIndex).orElse(-1);
        } else {
            Optional<VideoMode> optional = window.getPreferredFullscreenVideoMode();
            j2 = (Integer)optional.map(monitor::getVideoModeIndex).orElse(-1);
        }





        return VhsAspectRatio.normalVideoMode = new OptionInstance<>(
                ConfigStuff.enableVHSAspectRatio ? "spb-revamped.options.fullscreen.resolution" : "options.fullscreen.resolution",
                OptionInstance.noTooltip(),
                (prefix, value) -> {
                    if (monitor == null) {
                        return Component.translatable("options.fullscreen.unavailable");
                    } else {
                        return value == -1
                                ? Options.genericValueLabel(prefix, Component.translatable("options.fullscreen.current"))
                                : Options.genericValueLabel(prefix, ConfigStuff.enableVHSAspectRatio ? Component.literal(VhsAspectRatio.vhsAspectRatiosList.get(value).write()).withStyle(ChatFormatting.GREEN) : Component.literal(monitor.getMode(value).toString()));
                    }
                },
                new OptionInstance.IntRange(-1, monitor == null ? -1 : ConfigStuff.enableVHSAspectRatio ? VhsAspectRatio.vhsAspectRatiosList.size() - 1 : monitor.getModeCount() - 1),
                j2,
                value -> {
                    if (monitor != null) {
                        Optional<VideoMode> videoMode = value == -1 ? Optional.empty() : ConfigStuff.enableVHSAspectRatio ? Optional.of(VhsAspectRatio.vhsAspectRatiosList.get(value)) : Optional.of(monitor.getMode(value));
                        window.setPreferredFullscreenVideoMode(videoMode);
                    }
                }
        );
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/OptionsList;addBig(Lnet/minecraft/client/OptionInstance;)I", ordinal = 0))
    private OptionInstance addNormalVideoModeToList(OptionInstance<?> option){
        return VhsAspectRatio.normalVideoMode;
    }


//    @Redirect(method = "method_41844", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setVideoMode(Ljava/util/Optional;)V"))
//    private static void setVideoModeConditionally(Window instance, Optional<VideoMode> videoMode, @Local(argsOnly = true) Integer value, @Local(argsOnly = true) Monitor monitor){
//        if(!ConfigStuff.enableVHSAspectRatio){
//            instance.setVideoMode(value == -1 ? Optional.empty() : Optional.of(monitor.getVideoMode(value)));
//        }
//    }

//    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/OptionListWidget;addSingleOptionEntry(Lnet/minecraft/client/option/SimpleOption;)I", ordinal = 0))
//    private void createSimpleOption(CallbackInfo ci, @Local Monitor monitor, @Local Window window){
//        Optional<VideoMode> optional = VhsAspectRatio.currentVhsVideoMode != null ? VhsAspectRatio.currentVhsVideoMode : window.getVideoMode();
//        int j2 = (Integer)optional.map(this::findClosestVhsVideoModeIndex).orElse(-1);
//
//        VhsAspectRatio.vhsVideoMode = new SimpleOption<>(
//                "options.vhs.resolution",
//                SimpleOption.emptyTooltip(),
//                (prefix, value) -> {
//                    if (monitor == null) {
//                        return Text.translatable("options.vhs.resolutions.unavailable");
//                    } else {
//                        return value == -1
//                                ? GameOptions.getGenericValueText(prefix, Text.translatable("options.fullscreen.current"))
//                                : GameOptions.getGenericValueText(prefix, Text.literal(VhsAspectRatio.vhsAspectRatiosList.get(value).toString()));
//                    }
//                },
//                new SimpleOption.ValidatingIntSliderCallbacks(-1, monitor != null ? VhsAspectRatio.vhsAspectRatiosList.size() - 1 : -1),
//                j2,
//                value -> {
//                    if (monitor != null) {
//                        Optional<VideoMode> videoMode = value == -1 ? Optional.empty() : Optional.of(VhsAspectRatio.vhsAspectRatiosList.get(value));
//                        if(ConfigStuff.enableVHSAspectRatio) {
//                            System.out.println("SET");
//                            window.setVideoMode(videoMode);
//                        }
//                        VhsAspectRatio.currentVhsVideoMode = videoMode;
//                    }
//                }
//        );
//        this.list.addSingleOptionEntry(VhsAspectRatio.vhsVideoMode);
//    }

    @Unique
    private int findClosestVhsVideoModeIndex(VideoMode videoMode) {
        return VhsAspectRatio.vhsAspectRatiosList.indexOf(videoMode);
    }




}
