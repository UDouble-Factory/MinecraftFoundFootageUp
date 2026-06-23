package com.sp.render.grass;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.sp.SPBRevamped;
import com.sp.SPBRevampedClient;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.init.BackroomsLevels;
import com.sp.mixininterfaces.RenderIndirectExtension;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.VeilFramebuffers;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniformAccess;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;

import static net.minecraft.util.Mth.floor;
import static net.minecraft.util.Mth.sqrt;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL40C.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL42C.GL_ALL_BARRIER_BITS;
import static org.lwjgl.opengl.GL42C.glMemoryBarrier;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL43C.glDispatchCompute;

public class GrassRenderer {
    VertexBuffer vertexBuffer;
    private static final ResourceLocation shaderPath = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "grass/grass");
    private static final ResourceLocation windTexture = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "textures/shaders/puddle_noise.png");
    private static final ResourceLocation computeShaderPath = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "grass/compute/positions");

    private final int positionsVbo;
    private final int indirectVbo;

    private int lastGrassCount;
    private int lastMeshResolution;
    private float lastHeight;
    private ByteBuffer cmd;

    public static final VertexFormat POSITION_NORMAL = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Normal", VertexFormatElement.NORMAL)
            .build();

    private float getGrassHeight() {
        if (SPBRevampedClient.isInLevel(BackroomsLevels.LEVEL324_BACKROOMS_LEVEL)) {
            return 1.5f;
        }
        return 1f;
    }

    public GrassRenderer() {
        this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.uploadGrassModel();

        this.positionsVbo = glGenBuffers();
        this.indirectVbo = glGenBuffers();
        this.updateBuffers(true);
    }

    private void uploadGrassModel() {
        ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(4096);
        BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, POSITION_NORMAL);

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

        if (ConfigStuff.grassQuality.getCount() != this.lastGrassCount ||
                ConfigStuff.grassQuality.getResolution() != this.lastMeshResolution ||
                getGrassHeight() != lastHeight) {
            if (this.vertexBuffer != null) {
                this.vertexBuffer.close();
            }
            this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            this.uploadGrassModel();
        }

        this.updateBuffers(false);
        this.computeGrassPositions();

        ShaderProgram shader = VeilRenderSystem.setShader(shaderPath);
        if (shader == null) return;

        ShaderUniformAccess gameTime = shader.getUniform("GameTime");
        if (gameTime != null) gameTime.setFloat(RenderSystem.getShaderGameTime());

        ShaderUniformAccess numOfInst = shader.getUniform("NumOfInstances");
        if (numOfInst != null) numOfInst.setInt(floor(sqrt(ConfigStuff.grassQuality.getCount())));

        ShaderUniformAccess grassHeight = shader.getUniform("grassHeight");
        if (grassHeight != null) grassHeight.setFloat(getGrassHeight());

        ShaderUniformAccess density = shader.getUniform("density");
        if (density != null) density.setFloat(ConfigStuff.grassQuality.getDensity());

        RenderSystem.setShaderTexture(0, windTexture);
        shader.setTexture("WindNoise", windTexture);
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
        int currentGrassCount = ConfigStuff.grassQuality.getCount();
        int currentMeshResolution = ConfigStuff.grassQuality.getResolution();
        float currentHeight = getGrassHeight();
        boolean countChange = currentGrassCount != this.lastGrassCount;
        boolean resolutionChange = currentMeshResolution != this.lastMeshResolution;
        boolean heightChange = currentHeight != this.lastHeight;

        if (countChange) {
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.positionsVbo);
            glBufferData(GL_SHADER_STORAGE_BUFFER, (long) 4 * ((long) currentGrassCount) * Float.BYTES, GL_DYNAMIC_DRAW);
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        }

        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, this.indirectVbo);
        glBufferData(GL_DRAW_INDIRECT_BUFFER, 20L, GL_STATIC_DRAW);

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

        if (countChange) this.lastGrassCount = currentGrassCount;
        if (resolutionChange) this.lastMeshResolution = currentMeshResolution;
        if (heightChange) this.lastHeight = currentHeight;
    }

    private void computeGrassPositions() {
        ShaderProgram shader = VeilRenderSystem.setShader(computeShaderPath);
        if (shader == null) return;

        if (shader.isCompute()) {
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, this.positionsVbo);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, this.indirectVbo);

            int numOfInst = floor(sqrt(ConfigStuff.grassQuality.getCount()));
            ShaderUniformAccess numUniform = shader.getUniform("NumOfInstances");
            if (numUniform != null) numUniform.setInt(numOfInst);

            ShaderUniformAccess densityUniform = shader.getUniform("density");
            if (densityUniform != null) densityUniform.setFloat(ConfigStuff.grassQuality.getDensity());

            float maxDist = numOfInst / (ConfigStuff.grassQuality.getDensity() * 1.85f);
            ShaderUniformAccess maxDistUniform = shader.getUniform("maxDist");
            if (maxDistUniform != null) maxDistUniform.setFloat(maxDist);

            // Extract frustum planes from projection * modelView matrix
            Matrix4f proj = new Matrix4f(RenderSystem.getProjectionMatrix());
            Matrix4f mv = new Matrix4f(RenderSystem.getModelViewMatrix());
            float[] planes = extractFrustumPlanes(proj.mul(mv));
            ShaderUniformAccess frustumUniform = shader.getUniform("FrustumPlanes");
            if (frustumUniform != null) frustumUniform.setFloats(planes);

            shader.bind();

            int grass = floor(sqrt((float) ConfigStuff.grassQuality.getCount()) / 8);
            int x = Math.min(grass, VeilRenderSystem.maxComputeWorkGroupCountX());
            int y = Math.min(grass, VeilRenderSystem.maxComputeWorkGroupCountY());

            glDispatchCompute(x, y, 1);
            glMemoryBarrier(GL_ALL_BARRIER_BITS);

            ShaderProgram.unbind();
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, 0);
        }

        ShaderProgram.unbind();
    }

    private static float[] extractFrustumPlanes(Matrix4f m) {
        float[] planes = new float[24];
        planes[0]  = m.m03() + m.m00(); planes[1]  = m.m13() + m.m10(); planes[2]  = m.m23() + m.m20(); planes[3]  = m.m33() + m.m30();
        planes[4]  = m.m03() - m.m00(); planes[5]  = m.m13() - m.m10(); planes[6]  = m.m23() - m.m20(); planes[7]  = m.m33() - m.m30();
        planes[8]  = m.m03() + m.m01(); planes[9]  = m.m13() + m.m11(); planes[10] = m.m23() + m.m21(); planes[11] = m.m33() + m.m31();
        planes[12] = m.m03() - m.m01(); planes[13] = m.m13() - m.m11(); planes[14] = m.m23() - m.m21(); planes[15] = m.m33() - m.m31();
        planes[16] = m.m03() + m.m02(); planes[17] = m.m13() + m.m12(); planes[18] = m.m23() + m.m22(); planes[19] = m.m33() + m.m32();
        planes[20] = m.m03() - m.m02(); planes[21] = m.m13() - m.m12(); planes[22] = m.m23() - m.m22(); planes[23] = m.m33() - m.m32();
        return planes;
    }

    private void createGrassModel(BufferBuilder bufferBuilder) {
        int segments = ConfigStuff.grassQuality.getResolution();
        float xStep = 0.1f / segments;

        for (int i = 0; i < segments; i++) {
            bufferBuilder.addVertex(0.6f - xStep * (i + 1), getGrassHeight() / segments * (i + 1), 0).setNormal(0, 0, 1);
            bufferBuilder.addVertex(0.4f + xStep * (i + 1), getGrassHeight() / segments * (i + 1), 0).setNormal(0, 0, 1);
            bufferBuilder.addVertex(0.4f + xStep * i,       getGrassHeight() / segments * i,        0).setNormal(0, 0, 1);
            bufferBuilder.addVertex(0.6f - xStep * i,       getGrassHeight() / segments * i,        0).setNormal(0, 0, 1);

            bufferBuilder.addVertex(0.6f - xStep * i,       getGrassHeight() / segments * i,        0).setNormal(0, 0, -1);
            bufferBuilder.addVertex(0.4f + xStep * i,       getGrassHeight() / segments * i,        0).setNormal(0, 0, -1);
            bufferBuilder.addVertex(0.4f + xStep * (i + 1), getGrassHeight() / segments * (i + 1), 0).setNormal(0, 0, -1);
            bufferBuilder.addVertex(0.6f - xStep * (i + 1), getGrassHeight() / segments * (i + 1), 0).setNormal(0, 0, -1);
        }
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