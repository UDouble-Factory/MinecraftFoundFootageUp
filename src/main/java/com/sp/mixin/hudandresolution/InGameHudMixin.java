package com.sp.mixin.hudandresolution;

import com.sp.SPBRevampedClient;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.util.TickTimer;
import com.sp.util.Timer;
import foundry.veil.api.client.util.Easings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow private int screenHeight;

    @Shadow @Final private static ResourceLocation GUI_ICONS_LOCATION;

    @Unique Timer hotbarSlideTimer = new Timer(500, Easings.Easing.easeInCirc, Easings.Easing.easeOutCirc);
    @Unique TickTimer hotbarHoldTimer = new TickTimer();
    @Unique Integer prevSelectedSlot = 0;
    @Unique double hotbarPosition;

    @Inject(method = {"renderHotbar"}, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER))
    private void hotbarSlide1(float tickDelta, GuiGraphics context, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            this.hotbarPosition = 45 * hotbarSlideTimer.getCurrentTime();
            if (!ConfigStuff.useDefaultGUI) {
                context.pose().translate(0, this.hotbarPosition, 0);
            }
            Minecraft client = Minecraft.getInstance();

            if (client.player != null) {
                int selectedSlot = client.player.getInventory().selected;
                if (this.prevSelectedSlot != null) {
                    if (this.prevSelectedSlot != selectedSlot) {
                        hotbarSlideTimer.reverse();
                        hotbarSlideTimer.startTimer();
                        hotbarHoldTimer.resetToZero();
                    } else {
                        if (hotbarHoldTimer.getCurrentTick() >= 60) {
                            hotbarSlideTimer.forward();
                        }
                    }
                }
                this.prevSelectedSlot = selectedSlot;
            }
        }
    }

    @Inject(method = {"renderSlot"}, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 1, shift = At.Shift.AFTER))
    private void itemCountFix(GuiGraphics context, int x, int y, float f, Player player, ItemStack stack, int seed, CallbackInfo ci){
        if (!ConfigStuff.useDefaultGUI) {
            context.pose().translate(0, this.hotbarPosition, 0);
        }
    }

    @Inject(method = {"renderSlot"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V"))
    private void hotbarSlide2(GuiGraphics context, int x, int y, float f, Player player, ItemStack stack, int seed, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            context.pose().pushPose();

            if (!ConfigStuff.useDefaultGUI) {
                context.pose().translate(0, this.hotbarPosition, 0);
            }
        }
    }

    @Inject(method = {"renderSlot"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", shift = At.Shift.AFTER))
    private void hotbarSlide3(GuiGraphics context, int x, int y, float f, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            context.pose().popPose();
        }
    }

    


    //RENDER HEALTH BAR
    @Inject(method = "renderHearts", at = @At("HEAD"))
    private void setHealthOpacity1(GuiGraphics context, Player player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            if (!ConfigStuff.useDefaultGUI) {
                context.setColor(1.0f, 1.0f, 1.0f, 0.2f);
            }

            context.pose().pushPose();
            if (!ConfigStuff.useDefaultGUI) {
                context.pose().translate(0, 5, 0);
            }
        }
    }

    @Inject(method = "renderHearts", at = @At("TAIL"))
    private void setHealthOpacity2(GuiGraphics context, Player player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            context.pose().popPose();
            if (!ConfigStuff.useDefaultGUI) {
                context.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }


    //RENDER HUNGER BAR
    @Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I", shift = At.Shift.AFTER))
    private void setHungerOpacity1(GuiGraphics context, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            if (!ConfigStuff.useDefaultGUI) {
                context.setColor(1.0f, 1.0f, 1.0f, 0.2f);
            }

            context.pose().pushPose();
            if (!ConfigStuff.useDefaultGUI) {
                context.pose().translate(0, 5, 0);
            }
        }
    }

    @Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 2))
    private void setHungerOpacity2(GuiGraphics context, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            context.pose().popPose();
            if (!ConfigStuff.useDefaultGUI) {
                context.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }



    @Inject(method = {"renderExperienceBar", "renderCrosshair"}, at = @At("HEAD"), cancellable = true)
    private void disable(CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            if (!ConfigStuff.useDefaultGUI) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderVignette", at = @At("HEAD"), cancellable = true)
    private void disableVignette(CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            if (SPBRevampedClient.getCutsceneManager().isPlaying || SPBRevampedClient.getCutsceneManager().blackScreen.isBlackScreen) {
                ci.cancel();
            }
        }
    }

}
