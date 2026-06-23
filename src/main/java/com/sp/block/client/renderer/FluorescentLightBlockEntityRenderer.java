package com.sp.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sp.block.custom.FluorescentLightBlock;
import com.sp.block.entity.FluorescentLightBlockEntity;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.render.RenderLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

public class FluorescentLightBlockEntityRenderer implements BlockEntityRenderer<FluorescentLightBlockEntity> {
    public FluorescentLightBlockEntityRenderer(BlockEntityRendererProvider.Context context){

    }

    @Override
    public void render(FluorescentLightBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        boolean blackout = entity.getCurrentState().getValue(FluorescentLightBlock.BLACKOUT);
        boolean on = entity.getCurrentState().getValue(FluorescentLightBlock.ON);

        //don't render if blackout is active
        if(blackout || !on) return;

        Matrix4f matrix4f = matrices.last().pose();
        this.renderCube(entity, matrix4f, vertexConsumers.getBuffer(this.getLayer()));
    }

    private void renderCube(FluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer) {
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
        renderFace(entity, matrix, buffer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
        renderFace(entity, matrix, buffer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.WEST);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, Direction.UP);
    }

    private void renderFace(FluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer, float f, float g, float h, float i, float j, float k, float l, float m, Direction direction) {
            buffer.addVertex(matrix, f, h, j);
            buffer.addVertex(matrix, g, h, k);
            buffer.addVertex(matrix, g, i, l);
            buffer.addVertex(matrix, f, i, m);
    }

    protected RenderType getLayer() {
        return RenderLayers.FLUORESCENT_LIGHT;
    }

    @Override
    public int getViewDistance() {
        return (int) ConfigStuff.getLightRenderDistance();
    }
}
