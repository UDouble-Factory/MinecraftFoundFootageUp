package com.sp.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.*;

public class VertexFormats {
    public static final VertexFormat BLOCKS;
    public static final VertexFormat PBR;

    //For some reason setting the Component Type to INT breaks everything
    private static final VertexFormatElement FLOAT = new VertexFormatElement(
            0,
            VertexFormatElement.Type.FLOAT,
            VertexFormatElement.Usage.GENERIC,
            1
    );


    static {
        ImmutableMap.Builder<String, VertexFormatElement> blockElements = ImmutableMap.builder();
        blockElements.put("Position", ELEMENT_POSITION);
        blockElements.put("Color", ELEMENT_COLOR);
        blockElements.put("UV0", ELEMENT_UV0);
        blockElements.put("UV2", ELEMENT_UV2);
        blockElements.put("Normal", ELEMENT_NORMAL);
        blockElements.put("Padding", ELEMENT_PADDING);
        blockElements.put("Material", FLOAT);

        BLOCKS = new VertexFormat(blockElements.build());


        ImmutableMap.Builder<String, VertexFormatElement> blockElements2 = ImmutableMap.builder();
        blockElements2.put("Position", ELEMENT_POSITION);
        blockElements2.put("Color", ELEMENT_COLOR);
        blockElements2.put("UV0", ELEMENT_UV0);
        blockElements2.put("UV2", ELEMENT_UV2);
        blockElements2.put("Normal", ELEMENT_NORMAL);
        blockElements2.put("Zoom", FLOAT);
        blockElements2.put("Resolution", FLOAT);
        blockElements2.put("EnableHeight", FLOAT);
        blockElements2.put("DepthMultiplier", FLOAT);

        PBR = new VertexFormat(blockElements2.build());
    }
}
