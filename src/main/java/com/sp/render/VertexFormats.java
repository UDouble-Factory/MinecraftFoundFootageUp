package com.sp.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public class VertexFormats {
    public static final VertexFormat BLOCKS;
    public static final VertexFormat PBR;

    public static final VertexFormatElement MATERIAL_ELEMENT = VertexFormatElement.register(
            6, 0,
            VertexFormatElement.Type.FLOAT,
            VertexFormatElement.Usage.GENERIC,
            1
    );

    public static final VertexFormatElement ZOOM_ELEMENT = VertexFormatElement.register(
            7, 0,
            VertexFormatElement.Type.FLOAT,
            VertexFormatElement.Usage.GENERIC,
            1
    );

    public static final VertexFormatElement RESOLUTION_ELEMENT = VertexFormatElement.register(
            8, 0,
            VertexFormatElement.Type.FLOAT,
            VertexFormatElement.Usage.GENERIC,
            1
    );

    public static final VertexFormatElement ENABLE_HEIGHT_ELEMENT = VertexFormatElement.register(
            9, 0,
            VertexFormatElement.Type.FLOAT,
            VertexFormatElement.Usage.GENERIC,
            1
    );

    public static final VertexFormatElement DEPTH_MULTIPLIER_ELEMENT = VertexFormatElement.register(
            10, 0,
            VertexFormatElement.Type.FLOAT,
            VertexFormatElement.Usage.GENERIC,
            1
    );

    static {
        BLOCKS = VertexFormat.builder()
                .add("Position", VertexFormatElement.POSITION)
                .add("Color", VertexFormatElement.COLOR)
                .add("UV0", VertexFormatElement.UV0)
                .add("UV2", VertexFormatElement.UV2)
                .add("Normal", VertexFormatElement.NORMAL)
                .padding(1)
                .add("Material", MATERIAL_ELEMENT)
                .build();

        PBR = VertexFormat.builder()
                .add("Position", VertexFormatElement.POSITION)
                .add("Color", VertexFormatElement.COLOR)
                .add("UV0", VertexFormatElement.UV0)
                .add("UV2", VertexFormatElement.UV2)
                .add("Normal", VertexFormatElement.NORMAL)
                .padding(1)
                .add("Zoom", ZOOM_ELEMENT)
                .add("Resolution", RESOLUTION_ELEMENT)
                .add("EnableHeight", ENABLE_HEIGHT_ELEMENT)
                .add("DepthMultiplier", DEPTH_MULTIPLIER_ELEMENT)
                .build();
    }
}
