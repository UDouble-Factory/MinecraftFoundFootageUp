package com.sp;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinds {
    public static KeyMapping toggleFlashlight;
    public static KeyMapping toggleEvent;

    public static void initializeKeyBinds() {
        toggleFlashlight = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.spb-revamped.toggle_flashlight", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "spb-revamped.keybinds"));

        if (Minecraft.getInstance().getUser().getName().equals("SppacePotato") || Minecraft.getInstance().getUser().getName().equals("HerrChaotic") || FabricLoader.getInstance().isDevelopmentEnvironment()) {
            toggleEvent = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.spb-revamped.toggle_event", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_SEMICOLON, "spb-revamped.keybinds"));
        }
    }
}
