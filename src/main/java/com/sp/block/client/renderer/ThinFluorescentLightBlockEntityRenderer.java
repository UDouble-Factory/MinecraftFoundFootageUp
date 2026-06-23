package com.sp.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sp.SPBRevamped;
import com.sp.SPBRevampedClient;
import com.sp.block.custom.ThinFluorescentLightBlock;
import com.sp.block.entity.ThinFluorescentLightBlockEntity;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.render.RenderLayers;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.joml.Matrix4f;

import static net.minecraft.core.Direction.WEST;

public class ThinFluorescentLightBlockEntityRenderer implements BlockEntityRenderer<ThinFluorescentLightBlockEntity> {
    private static final ResourceLocation SHADER = new ResourceLocation(SPBRevamped.MOD_ID, "light/fluorescent_light");


    public ThinFluorescentLightBlockEntityRenderer(BlockEntityRendererProvider.Context context){

    }


    @Override
    public void render(ThinFluorescentLightBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        Minecraft client = Minecraft.getInstance();
        boolean blackout = entity.getCurrentState().getValue(ThinFluorescentLightBlock.BLACKOUT);
        boolean on = entity.getCurrentState().getValue(ThinFluorescentLightBlock.ON);

        ShaderProgram shader = VeilRenderSystem.setShader(SHADER);
        if(shader == null){
            return;
        }

        if(client.level != null) {
            shader.setFloat("warAngle", SPBRevampedClient.getWarpTimer(client.level));
        }

        //don't render if blackout is active
        if(blackout || !on) return;

        Direction facing = entity.getCurrentState().getValue(ThinFluorescentLightBlock.FACING);
        AttachFace wall = entity.getCurrentState().getValue(ThinFluorescentLightBlock.FACE);

        matrices.translate(0.5, 0.5, 0.5);
        if(wall == AttachFace.CEILING) {
            matrices.mulPose(Axis.YP.rotationDegrees(facing.getOpposite().toYRot()));
        }
        if(wall == AttachFace.WALL){
            matrices.mulPose(facing.getOpposite().getRotation());
        }
        if(wall == AttachFace.FLOOR){
            matrices.mulPose(Axis.XP.rotationDegrees(180));
            matrices.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
        }
        matrices.translate(-0.5, -0.5, -0.5);
        Matrix4f matrix4f = matrices.last().pose();

        shader.bind();
        this.renderCube(entity, matrix4f, vertexConsumers.getBuffer(this.getLayer()));
        ShaderProgram.unbind();
    }

    private void renderCube(ThinFluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer) {
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 0.875f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 1.0f, 0.875f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
            renderFace(entity, matrix, buffer, 0.625f, 0.625f, 1.0f, 0.875f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
            renderFace(entity, matrix, buffer, 0.375f, 0.375f, 0.875f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, WEST);
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 0.875f, 0.875f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
            renderFace(entity, matrix, buffer, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.UP);

    }

    private void renderFace(ThinFluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer, float f, float g, float h, float i, float j, float k, float l, float m, Direction direction) {
            buffer.vertex(matrix, f, h, j).endVertex();
            buffer.vertex(matrix, g, h, k).endVertex();
            buffer.vertex(matrix, g, i, l).endVertex();
            buffer.vertex(matrix, f, i, m).endVertex();
    }

    protected RenderType getLayer() {
        return RenderLayers.FLUORESCENT_LIGHT;
    }

    @Override
    public int getViewDistance() {
        return (int) ConfigStuff.getLightRenderDistance();
    }
}
