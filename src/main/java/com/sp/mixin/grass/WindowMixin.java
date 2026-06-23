package com.sp.mixin.grass;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.*;

@Mixin(value = Window.class, remap = false)
public class WindowMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 5))
    private void openGLContext(WindowEventHandler eventHandler, ScreenManager monitorTracker, DisplayData settings, String videoMode, String title, CallbackInfo ci){
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
    }

}
