package com.sp.entity.client.model;

import com.sp.SPBRevamped;
import com.sp.entity.custom.WalkerEntity;
import com.sp.entity.ik.model.GeckoLib.GeoModelAccessor;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
/**
 * Original model base by ShadowZecro <br>
 * Colored by VoidAtomicX <br>
 * Head reworked with the help of Jarton
 */
public class WalkerModel extends GeoModel<WalkerEntity> {
	private final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "geo/entity/walker.geo.json");
	private final ResourceLocation TEXTURES = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "textures/entity/walker/walker.png");


	@Override
	public void setCustomAnimations(WalkerEntity animatable, long instanceId, AnimationState<WalkerEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);

		animatable.tickComponentsClient(animatable, new GeoModelAccessor(this));
	}

	@Override
	public ResourceLocation getModelResource(WalkerEntity animatable) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(WalkerEntity animatable) {
		return TEXTURES;
	}

	@Override
	public ResourceLocation getAnimationResource(WalkerEntity animatable) {
		return null;
	}

}