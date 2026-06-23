package com.sp.networking.S2C;

import com.sp.SPBRevamped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ReloadLightsPayload() implements CustomPacketPayload {

    public static final ResourceLocation ID_LOCATION = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "rl_lights");
    public static final Type<ReloadLightsPayload> TYPE = new Type<>(ID_LOCATION);

    public static final StreamCodec<FriendlyByteBuf, ReloadLightsPayload> CODEC = StreamCodec.of(
            (buf, p) -> {},
            buf -> new ReloadLightsPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
