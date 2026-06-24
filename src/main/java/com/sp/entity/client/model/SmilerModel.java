package com.sp.entity.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sp.entity.custom.SmilerEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class SmilerModel<T extends SmilerEntity> extends HierarchicalModel<T> {
	private final ModelPart bone;


	public SmilerModel(ModelPart root) {
		super(RenderType::eyes);
		this.bone = root.getChild("bone");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition bone = modelPartData.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(1, 0).addBox(-22.0F, -22.0F, 0.0F, 44.0F, 44.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));
		return LayerDefinition.create(modelData, 96, 96);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}


	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
		bone.render(poseStack, vertexConsumer, i, j, k);
	}

	@Override
	public ModelPart root() {
		return bone;
	}


}