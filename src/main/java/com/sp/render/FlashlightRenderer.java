package com.sp.render;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FlashlightRenderer {
    private final Minecraft client;
    private final HashMap<AbstractClientPlayer, ArrayList<AreaLight>> flashLightList2;

    public FlashlightRenderer(){
        this.client = Minecraft.getInstance();
        this.flashLightList2 = new HashMap<>();
    }

    public void renderFlashlightForEveryPlayer(float partialTicks) {
        if(client.level != null) {
            List<AbstractClientPlayer> playerList = client.level.players();

            for (AbstractClientPlayer player : playerList) {
                if (player != null) {
                    if(player.isSpectator() && !player.equals(client.player)){
                        tryToRemoveFlashlight(player);
                        continue;
                    }
                    PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                    if (playerComponent.isFlashLightOn()) {
                        Vec3 playerPos = player.getEyePosition(partialTicks);
                        if (!flashLightList2.containsKey(player)) {
                            AreaLight areaLight = new AreaLight();
                            AreaLight areaLight2 = new AreaLight();
                            Quaternionf orientation = new Quaternionf().rotateXYZ((float) -Math.toRadians(player.getViewXRot(partialTicks)), (float) Math.toRadians(player.getViewYRot(partialTicks)), 0.0f);
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(areaLight
                                    .setBrightness(1f)
                                    .setDistance(25f)
                                    .setSize(0, 0)
                                    .setPosition(playerPos.x(), playerPos.y(), playerPos.z())
                                    .setOrientation(orientation)
                            );
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(areaLight2
                                    .setBrightness(1f)
                                    .setAngle(0.25f)
                                    .setDistance(25f)
                                    .setSize(0, 0)
                                    .setPosition(playerPos.x(), playerPos.y(), playerPos.z())
                                    .setOrientation(orientation)
                            );
                            ArrayList<AreaLight> list = new ArrayList<>();
                            list.add(areaLight);
                            list.add(areaLight2);
                            flashLightList2.put(player, list);
                        } else {
                            ArrayList<AreaLight> areaLightList = flashLightList2.get(player);

                            for(AreaLight areaLights : areaLightList) {
                                Quaternionf currentRot = new Quaternionf().rotateXYZ((float) -Math.toRadians(player.getViewXRot(partialTicks)), (float) Math.toRadians(player.getViewYRot(partialTicks)), 0.0f);
                                //*Fix for replay mod
                                float alpha = client.player.isSpectator() ? 1.0f : 0.7f * client.getDeltaFrameTime();
                                areaLights.getOrientation().slerp(currentRot, alpha);
                                areaLights.setPosition(playerPos.x(), playerPos.y(), playerPos.z());
                            }
                        }
                    } else {
                        tryToRemoveFlashlight(player);
                    }
                }
            }
        }
    }

    public void tryToRemoveFlashlight(AbstractClientPlayer player){
        if (flashLightList2.containsKey(player) && flashLightList2.get(player) != null) {
            ArrayList<AreaLight> areaLightList = flashLightList2.get(player);
            for(AreaLight areaLights : areaLightList){
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(areaLights);
            }
            flashLightList2.remove(player);
        }
    }

    public void clearFlashlights(){
        this.flashLightList2.clear();
    }
}
