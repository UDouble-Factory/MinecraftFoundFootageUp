package com.sp.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SmilerComponent;
import com.sp.entity.client.model.SmilerModel;
import com.sp.entity.custom.SmilerEntity;
import com.sp.init.ModModelLayers;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class SmilerRenderer extends MobRenderer<SmilerEntity, SmilerModel<SmilerEntity>> {
    private static final ResourceLocation defaultTexture = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "textures/entity/smiler/smiler.png");
    private static final ResourceLocation texture1 = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "textures/entity/smiler/smiler1.png");
    private static final ResourceLocation texture2 = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "textures/entity/smiler/smiler2.png");

    public SmilerRenderer(EntityRendererProvider.Context context) {
        super(context, new SmilerModel<>(context.bakeLayer(ModModelLayers.SMILER)), 0);
    }

    @Override
    public void render(SmilerEntity mobEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        matrixStack.translate(0,1,0);
        float angle = lookAtEntityAroundYAxis(mobEntity.getEyePosition(), camera.getPosition());
        matrixStack.mulPose(new Quaternionf().rotateXYZ(0, (float) Math.toRadians(angle), 0));
        matrixStack.translate(0,-1,0);


        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public ResourceLocation getTextureLocation(SmilerEntity entity) {
        SmilerComponent component = InitializeComponents.SMILER.get(entity);
        return switch (component.getRandomTexture()) {
            case 1 -> texture1;
            case 2 -> texture2;
            default -> defaultTexture;
        };
    }

    @Override
    protected @Nullable RenderType getRenderType(SmilerEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return super.getRenderType(entity, showBody, translucent, showOutline);
    }

    public static float lookAtEntityAroundYAxis(Vec3 position1, Vec3 position2) {
        double d = position2.x() - position1.x();
        double e = position2.z() - position1.z();
        float h = -(float)(Mth.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;

        return h - 180.0F;
    }
}
