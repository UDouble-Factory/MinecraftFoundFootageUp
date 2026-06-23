package com.sp.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sp.SPBRevamped;
import com.sp.SPBRevampedClient;
import com.sp.block.custom.TinyFluorescentLightBlock;
import com.sp.block.entity.TinyFluorescentLightBlockEntity;
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
import org.joml.Matrix4f;

import static net.minecraft.core.Direction.WEST;

public class TinyFluorescentLightBlockEntityRenderer implements BlockEntityRenderer<TinyFluorescentLightBlockEntity> {
    private static final ResourceLocation SHADER = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "light/fluorescent_light");


    public TinyFluorescentLightBlockEntityRenderer(BlockEntityRendererProvider.Context context){

    }


    @Override
    public void render(TinyFluorescentLightBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        Minecraft client = Minecraft.getInstance();
        boolean blackout = entity.getCurrentState().getValue(TinyFluorescentLightBlock.BLACKOUT);
        boolean on = entity.getCurrentState().getValue(TinyFluorescentLightBlock.ON);

        ShaderProgram shader = VeilRenderSystem.setShader(SHADER);
        if(shader == null){
            return;
        }

        if(client.level != null) {
            shader.getUniform("warAngle").setFloat(SPBRevampedClient.getWarpTimer(client.level));
        }

        //don't render if blackout is active
        if(blackout || !on) return;



        Matrix4f matrix4f = matrices.last().pose();

        shader.bind();
        this.renderCube(entity, matrix4f, vertexConsumers.getBuffer(this.getLayer()));
        ShaderProgram.unbind();
    }

    private void renderCube(TinyFluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer) {
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 0.875f, 1, 0.625f, 0.625f, 0.625f, 0.625f, Direction.SOUTH);
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 1.0f, 0.875f, 0.375f, 0.375f, 0.375f, 0.375f, Direction.NORTH);
            renderFace(entity, matrix, buffer, 0.625f, 0.625f, 1.0f, 0.875f, 0.375f, 0.625f, 0.625f, 0.375f, Direction.EAST);
            renderFace(entity, matrix, buffer, 0.375f, 0.375f, 0.875f, 1.0f, 0.375f, 0.625f, 0.625f, 0.375f, WEST);
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 0.875f, 0.875f, 0.375f, 0.375f, 0.625f, 0.625f, Direction.DOWN);
            renderFace(entity, matrix, buffer, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.375f, 0.375f, 0.375f, Direction.UP);
    }

    private void renderFace(TinyFluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer, float x, float x2, float y, float y2, float z, float z2, float z3, float z4, Direction direction) {
            buffer.addVertex(matrix, x, y, z);
            buffer.addVertex(matrix, x2, y, z2);
            buffer.addVertex(matrix, x2, y2, z3);
            buffer.addVertex(matrix, x, y2, z4);
    }

    protected RenderType getLayer() {
        return RenderLayers.FLUORESCENT_LIGHT;
    }

    @Override
    public int getViewDistance() {
        return (int) ConfigStuff.getLightRenderDistance();
    }
}
