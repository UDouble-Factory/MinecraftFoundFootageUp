package com.sp.render.bird;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.sp.SPBRevamped;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.RenderIndirectExtension;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.VeilFramebuffers;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniformAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;

import static net.minecraft.util.Mth.floor;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL40C.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL42C.GL_ALL_BARRIER_BITS;
import static org.lwjgl.opengl.GL42C.glMemoryBarrier;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL43C.glDispatchCompute;

public class BirdRenderer {
    private static final ResourceLocation shaderPath = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "bird/bird");
    public static final ResourceLocation computeShaderPath = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "bird/compute/positions");

    private static final VertexFormat POSITION_NORMAL = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Normal", VertexFormatElement.NORMAL)
            .build();

    private final int positionsVbo;
    private final int indirectVbo;

    private int lastBirdCount;
    private ByteBuffer cmd;

    VertexBuffer vertexBuffer;

    private int lastFlockCount;

    public BirdRenderer() {
        this.lastBirdCount = ConfigStuff.birdQuality.getBirdCount();
        this.lastFlockCount = ConfigStuff.birdQuality.getFlockCount();

        this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.uploadBirdModel();

        this.positionsVbo = glGenBuffers();
        this.indirectVbo = glGenBuffers();
        this.updateBuffers(true);
    }

    private void uploadBirdModel() {
        ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(256);
        BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.TRIANGLES, POSITION_NORMAL);

        this.createGrassModel(bufferBuilder);

        MeshData meshData = bufferBuilder.buildOrThrow();
        this.vertexBuffer.bind();
        this.vertexBuffer.upload(meshData);
        VertexBuffer.unbind();
        byteBufferBuilder.close();
    }

    public void render() {
        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(VeilFramebuffers.MAIN);
        if (fbo == null) return;
        fbo.bind(false);

        if (ConfigStuff.birdQuality.getBirdCount() != this.lastBirdCount) {
            if (this.vertexBuffer != null) {
                this.vertexBuffer.close();
            }
            this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            this.uploadBirdModel();
        }

        this.updateBuffers(false);
        this.computeBirdPositions();

        ShaderProgram shader = VeilRenderSystem.setShader(shaderPath);
        if (shader == null) return;

        ShaderUniformAccess gameTime = shader.getUniform("GameTime");
        if (gameTime != null) gameTime.setFloat(RenderSystem.getShaderGameTime());

        ShaderUniformAccess numOfInst = shader.getUniform("NumOfInstances");
        if (numOfInst != null) numOfInst.setInt(ConfigStuff.birdQuality.getBirdCount());

        shader.bindSamplers(null, 0);

        this.vertexBuffer.bind();
        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, this.indirectVbo);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, this.positionsVbo);
        shader.bind();

        ((RenderIndirectExtension) this.vertexBuffer).spb_revamped_1_20_1$drawIndirect();

        ShaderProgram.unbind();
        shader.clearSamplers();

        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0);
        VertexBuffer.unbind();

        AdvancedFbo.unbind();
    }

    private void updateBuffers(boolean init) {
        int currentBirdCount = ConfigStuff.birdQuality.getBirdCount();
        int currentFlockCount = ConfigStuff.birdQuality.getFlockCount();
        boolean configChange = currentBirdCount != this.lastBirdCount || currentFlockCount != this.lastFlockCount;

        if (configChange || init) {
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.positionsVbo);
            glBufferData(GL_SHADER_STORAGE_BUFFER, (long) 6 * ((long) currentBirdCount) * Float.BYTES, GL_DYNAMIC_DRAW);

            ByteBuffer initialData = glMapBufferRange(
                    GL_SHADER_STORAGE_BUFFER, 0, (long) 6 * ((long) currentBirdCount) * Float.BYTES,
                    GL_MAP_WRITE_BIT | GL_MAP_INVALIDATE_BUFFER_BIT
            );

            if (initialData != null) {
                RandomSource random = RandomSource.create();
                for (int i = 0; i < currentBirdCount; i++) {
                    initialData.putFloat(0);
                    initialData.putFloat(100);
                    initialData.putFloat(0);
                    initialData.putFloat(0);
                    initialData.putFloat(1);
                    initialData.putFloat(0);
                }
                initialData.flip();
                glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
            }
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        }

        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, this.indirectVbo);
        glBufferData(GL_DRAW_INDIRECT_BUFFER, 20, GL_STATIC_DRAW);

        this.cmd = glMapBufferRange(
                GL_DRAW_INDIRECT_BUFFER, 0, 20,
                GL_MAP_WRITE_BIT | GL_MAP_INVALIDATE_BUFFER_BIT | GL_MAP_UNSYNCHRONIZED_BIT
        );

        if (cmd != null) {
            this.cmd.clear();
            this.cmd.putInt(VeilRenderSystem.getIndexCount(this.vertexBuffer));
            this.cmd.putInt(0);
            this.cmd.putInt(0);
            this.cmd.putInt(0);
            this.cmd.putInt(0);
            this.cmd.flip();
        }
        glUnmapBuffer(GL_DRAW_INDIRECT_BUFFER);
        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0);

        if (configChange) {
            this.lastBirdCount = currentBirdCount;
            this.lastFlockCount = currentFlockCount;
        }
    }

    private void computeBirdPositions() {
        ShaderProgram shader = VeilRenderSystem.setShader(computeShaderPath);
        ShaderProgram fragShader = VeilRenderSystem.setShader(shaderPath);
        if (shader == null) return;
        if (fragShader == null) return;
        int numOfInst = ConfigStuff.birdQuality.getBirdCount();

        if (shader.isCompute()) {
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, this.positionsVbo);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, this.indirectVbo);

            ShaderUniformAccess numUniform = shader.getUniform("NumOfInstances");
            if (numUniform != null) numUniform.setInt(numOfInst);

            // Extract frustum planes from projection * modelView matrix
            Matrix4f proj = new Matrix4f(RenderSystem.getProjectionMatrix());
            Matrix4f mv = new Matrix4f(RenderSystem.getModelViewMatrix());
            Matrix4f combo = proj.mul(mv);
            float[] planes = extractFrustumPlanes(combo);

            ShaderUniformAccess frustumUniform = shader.getUniform("FrustumPlanes");
            if (frustumUniform != null) frustumUniform.setFloats(planes);

            shader.bind();

            int grass = floor(Math.sqrt((float) ConfigStuff.birdQuality.getBirdCount()) / 8);
            int x = Math.min(grass, VeilRenderSystem.maxComputeWorkGroupCountX());
            int y = Math.min(grass, VeilRenderSystem.maxComputeWorkGroupCountY());

            glDispatchCompute(x, y, 1);
            glMemoryBarrier(GL_ALL_BARRIER_BITS);

            ShaderProgram.unbind();
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, 0);
        }

        ShaderUniformAccess numUniform2 = fragShader.getUniform("NumOfInstances");
        if (numUniform2 != null) numUniform2.setInt(numOfInst);

        ShaderProgram.unbind();
    }

    private static float[] extractFrustumPlanes(Matrix4f m) {
        // Extract 6 frustum planes (left, right, bottom, top, near, far) as vec4 (a,b,c,d)
        float[] planes = new float[24];
        // Left
        planes[0] = m.m03() + m.m00(); planes[1] = m.m13() + m.m10(); planes[2] = m.m23() + m.m20(); planes[3] = m.m33() + m.m30();
        // Right
        planes[4] = m.m03() - m.m00(); planes[5] = m.m13() - m.m10(); planes[6] = m.m23() - m.m20(); planes[7] = m.m33() - m.m30();
        // Bottom
        planes[8] = m.m03() + m.m01(); planes[9] = m.m13() + m.m11(); planes[10] = m.m23() + m.m21(); planes[11] = m.m33() + m.m31();
        // Top
        planes[12] = m.m03() - m.m01(); planes[13] = m.m13() - m.m11(); planes[14] = m.m23() - m.m21(); planes[15] = m.m33() - m.m31();
        // Near
        planes[16] = m.m03() + m.m02(); planes[17] = m.m13() + m.m12(); planes[18] = m.m23() + m.m22(); planes[19] = m.m33() + m.m32();
        // Far
        planes[20] = m.m03() - m.m02(); planes[21] = m.m13() - m.m12(); planes[22] = m.m23() - m.m22(); planes[23] = m.m33() - m.m32();
        return planes;
    }

    private void createGrassModel(BufferBuilder bufferBuilder) {
        // Front face
        bufferBuilder.addVertex(0.000000f, 0.000000f, -1.000000f).setNormal(0.8402f, 0.2425f, -0.4851f);
        bufferBuilder.addVertex(0.000000f, 2.000000f, 0.000000f).setNormal(0.8402f, 0.2425f, -0.4851f);
        bufferBuilder.addVertex(0.866025f, 0.000000f, 0.500000f).setNormal(0.8402f, 0.2425f, -0.4851f);

        // Bottom face
        bufferBuilder.addVertex(0.000000f, 0.000000f, -1.000000f).setNormal(0.0000f, -1.0000f, 0.0000f);
        bufferBuilder.addVertex(0.866025f, 0.000000f, 0.500000f).setNormal(0.0000f, -1.0000f, 0.0000f);
        bufferBuilder.addVertex(-0.866025f, 0.000000f, 0.500000f).setNormal(0.0000f, -1.0000f, 0.0000f);

        // Right face
        bufferBuilder.addVertex(0.866025f, 0.000000f, 0.500000f).setNormal(0.0000f, 0.2425f, 0.9701f);
        bufferBuilder.addVertex(0.000000f, 2.000000f, 0.000000f).setNormal(0.0000f, 0.2425f, 0.9701f);
        bufferBuilder.addVertex(-0.866025f, 0.000000f, 0.500000f).setNormal(0.0000f, 0.2425f, 0.9701f);

        // Left face
        bufferBuilder.addVertex(-0.866025f, 0.000000f, 0.500000f).setNormal(-0.8402f, 0.2425f, -0.4851f);
        bufferBuilder.addVertex(0.000000f, 2.000000f, 0.000000f).setNormal(-0.8402f, 0.2425f, -0.4851f);
        bufferBuilder.addVertex(0.000000f, 0.000000f, -1.000000f).setNormal(-0.8402f, 0.2425f, -0.4851f);
    }

    public void close() {
        glDeleteBuffers(this.positionsVbo);
        glDeleteBuffers(this.indirectVbo);
        if (this.cmd != null) {
            this.cmd.clear();
            this.cmd = null;
        }
    }
}

