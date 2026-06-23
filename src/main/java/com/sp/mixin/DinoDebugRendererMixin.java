package com.sp.mixin;

import com.sp.entity.ik.util.PrAnCommonClass;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public abstract class DinoDebugRendererMixin {
    @Shadow protected abstract void debugFeedbackComponent(Component text);

    @Unique
    private static final int L = 74;

    @Inject(method = "handleDebugKeys", at = @At("HEAD"), cancellable = true)
    private void onHandleDebugKeys(int keyCode, CallbackInfoReturnable<Boolean> cir) {
        if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
            if (keyCode == L) {
                PrAnCommonClass.shouldRenderDebugLegs = !PrAnCommonClass.shouldRenderDebugLegs;
                this.debugFeedbackComponent(Component.translatable("debug.toggled_joint_debug.message"));
                cir.setReturnValue(true);
            }
        }
    }
}