package com.sp.render;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FlashlightRenderer {
    private final Minecraft client;
    private final HashMap<AbstractClientPlayer, ArrayList<LightRenderHandle<AreaLightData>>> flashLightList2;

    public FlashlightRenderer() {
        this.client = Minecraft.getInstance();
        this.flashLightList2 = new HashMap<>();
    }

    public void renderFlashlightForEveryPlayer(float partialTicks) {
        if (client.level != null) {
            List<AbstractClientPlayer> playerList = client.level.players();

            for (AbstractClientPlayer player : playerList) {
                if (player != null) {
                    if (player.isSpectator() && !player.equals(client.player)) {
                        tryToRemoveFlashlight(player);
                        continue;
                    }
                    PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                    if (playerComponent.isFlashLightOn()) {
                        Vec3 playerPos = player.getEyePosition(partialTicks);
                        if (!flashLightList2.containsKey(player)) {
                            AreaLightData data1 = new AreaLightData();
                            AreaLightData data2 = new AreaLightData();

                            Quaternionf orientation = new Quaternionf().rotateXYZ(
                                    (float) -Math.toRadians(player.getViewXRot(partialTicks)),
                                    (float) Math.toRadians(player.getViewYRot(partialTicks)),
                                    0.0f
                            );
                            data1.setBrightness(1f)
                                    .setDistance(25f)
                                    .setSize(0, 0);
                            data1.getPosition().set(playerPos.x(), playerPos.y(), playerPos.z());
                            data1.getOrientation().set(orientation);
                            data1.markDirty();

                            data2.setBrightness(1f)
                                    .setAngle(0.25f)
                                    .setDistance(25f)
                                    .setSize(0, 0);
                            data2.getPosition().set(playerPos.x(), playerPos.y(), playerPos.z());
                            data2.getOrientation().set(orientation);
                            data2.markDirty();

                            var lightRenderer = VeilRenderSystem.renderer().getLightRenderer();
                            LightRenderHandle<AreaLightData> handle1 = lightRenderer.addLight(data1);
                            LightRenderHandle<AreaLightData> handle2 = lightRenderer.addLight(data2);

                            ArrayList<LightRenderHandle<AreaLightData>> list = new ArrayList<>();
                            list.add(handle1);
                            list.add(handle2);
                            flashLightList2.put(player, list);
                        } else {
                            ArrayList<LightRenderHandle<AreaLightData>> handleList = flashLightList2.get(player);

                            for (LightRenderHandle<AreaLightData> handle : handleList) {
                                AreaLightData areaLights = handle.getLightData();
                                Quaternionf currentRot = new Quaternionf().rotateXYZ(
                                        (float) -Math.toRadians(player.getViewXRot(partialTicks)),
                                        (float) Math.toRadians(player.getViewYRot(partialTicks)),
                                        0.0f
                                );
                                float alpha = client.player.isSpectator() ? 1.0f : 0.7f * client.getTimer().getRealtimeDeltaTicks();
                                areaLights.getOrientation().slerp(currentRot, alpha);
                                areaLights.getPosition().set(playerPos.x(), playerPos.y(), playerPos.z());
                                handle.markDirty();
                            }
                        }
                    } else {
                        tryToRemoveFlashlight(player);
                    }
                }
            }
        }
    }

    public void tryToRemoveFlashlight(AbstractClientPlayer player) {
        if (flashLightList2.containsKey(player) && flashLightList2.get(player) != null) {
            ArrayList<LightRenderHandle<AreaLightData>> handleList = flashLightList2.get(player);
            for (LightRenderHandle<AreaLightData> handle : handleList) {
                handle.free();
            }
            flashLightList2.remove(player);
        }
    }

    public void clearFlashlights() {
        for (ArrayList<LightRenderHandle<AreaLightData>> handleList : flashLightList2.values()) {
            for (LightRenderHandle<AreaLightData> handle : handleList) {
                handle.free();
            }
        }
        this.flashLightList2.clear();
    }
}
