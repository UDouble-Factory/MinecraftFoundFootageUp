package com.sp.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.sp.SPBRevamped;
import foundry.veil.api.client.render.VeilRenderBridge;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class RenderLayers extends RenderType {

    public static final ResourceLocation NORMAL_ATLAS_TEXTURE = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "textures/atlas/normal.png");
    public static final ResourceLocation HEIGHT_ATLAS_TEXTURE = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "textures/atlas/height.png");

    private static final RenderStateShard.ShaderStateShard LIGHT_SHADER = VeilRenderBridge.shaderState(ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "light/fluorescent_light"));
    private static final RenderStateShard.ShaderStateShard DISTORTED_ENTITY_SHADER = VeilRenderBridge.shaderState(ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "distorted_entity"));
    private static final RenderStateShard.ShaderStateShard POOLROOMS_SKY_SHADER = VeilRenderBridge.shaderState(ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "sky"));
    private static final RenderStateShard.ShaderStateShard PBR_SHADER = VeilRenderBridge.shaderState(ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "pbr/pbr"));

    private static final RenderType PBR_LAYER = RenderType.create(
            "pbr",
            VertexFormats.PBR,
            VertexFormat.Mode.QUADS,
            2097152,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(PBR_SHADER)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(TextureAtlas.LOCATION_BLOCKS, false, true)
                            .add(NORMAL_ATLAS_TEXTURE, false, true)
                            .add(HEIGHT_ATLAS_TEXTURE, false, false)  // Sampler2 gets replaced with the lightmap texture for some reason
                            .add(HEIGHT_ATLAS_TEXTURE, false, true)   // Which is why I added it twice
                            .build()
                    )
                    .createCompositeState(true)
    );

    public static final RenderType FLUORESCENT_LIGHT = RenderType.create(
            "fluorescent_light",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(LIGHT_SHADER)
                    .createCompositeState(false)
    );

    private static final RenderType POOLROOMS_SKY = RenderType.create(
            "poolrooms_sky",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(POOLROOMS_SKY_SHADER)
                    .createCompositeState(true)
    );



    private static final Function<ResourceLocation, RenderType> DISTORTED_ENTITY = Util.memoize((texture) -> {
        CompositeState multiPhaseParameters = RenderType.CompositeState.builder()
                .setShaderState(DISTORTED_ENTITY_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return create("distorted_entity", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, multiPhaseParameters);
    });

        /*RenderLayer.of(
            "distored_entity",
            VertexFormats.POSITION,
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(DISTORTED_ENTITY)
                    .build(true)
    );
    */

    public static RenderType getDistortedEntity(ResourceLocation texture) {
        return DISTORTED_ENTITY.apply(texture);
    }

    public static RenderType getPbrLayer() {
        return PBR_LAYER;
    }
    public static RenderType getPoolroomsSky() {
        return POOLROOMS_SKY;
    }

    public RenderLayers(String name, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }
}
