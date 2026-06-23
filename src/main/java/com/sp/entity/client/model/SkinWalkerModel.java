package com.sp.entity.client.model;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.entity.ik.model.GeckoLib.GeoModelAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class SkinWalkerModel extends GeoModel<SkinWalkerEntity> {
	private final ResourceLocation SLIM_MODEL = new ResourceLocation(SPBRevamped.MOD_ID, "geo/entity/skin_walker_slim.geo.json");
	private final ResourceLocation DEFAULT_MODEL = new ResourceLocation(SPBRevamped.MOD_ID, "geo/entity/skin_walker_default.geo.json");
	private final ResourceLocation FINAL_MODEL = new ResourceLocation(SPBRevamped.MOD_ID, "geo/entity/skin_walker_final_default.geo.json");

	private final ResourceLocation PLACEHOLDER_TEXTURE = new ResourceLocation(SPBRevamped.MOD_ID, "textures/entity/skinwalker/placeholder.png");
	private final ResourceLocation STEVE_TEXTURE = new ResourceLocation("textures/entity/player/wide/steve.png");

	private final ResourceLocation ANIMATION = new ResourceLocation(SPBRevamped.MOD_ID, "animations/entity/skinwalker.animation.json");

	@Override
	public void setCustomAnimations(SkinWalkerEntity animatable, long instanceId, AnimationState<SkinWalkerEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);
		SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(animatable);

		if(component.isInTrueForm()) {
			animatable.tickComponentsClient(animatable, new GeoModelAccessor(this));
		}
	}

	@Override
	public ResourceLocation getModelResource(SkinWalkerEntity animatable) {
		SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(animatable);

		if(!component.isInTrueForm()) {
			Minecraft client = Minecraft.getInstance();
			if (client.level != null) {
				AbstractClientPlayer player = (AbstractClientPlayer) client.level.getPlayerByUUID(component.getTargetPlayerUUID());
				if (player != null) {
					if (player.getModelName().equals("slim")) {
						return SLIM_MODEL;
					}
				}
			}

			return DEFAULT_MODEL;
		}

		return FINAL_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(SkinWalkerEntity animatable) {
		Minecraft client = Minecraft.getInstance();
		SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(animatable);
		if(client.level != null){
			AbstractClientPlayer player = (AbstractClientPlayer) client.level.getPlayerByUUID(component.getTargetPlayerUUID());
			if(player != null) {
				return player.getSkinTextureLocation();
			}
		}

		return PLACEHOLDER_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(SkinWalkerEntity animatable) {
		return ANIMATION;
	}

}