package com.sp.networking.S2C;

import com.sp.SPBRevamped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record LevelTransitionLightsOutPayload(int time) implements CustomPacketPayload {

    public static final ResourceLocation ID_LOCATION = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "ltos");
    public static final Type<LevelTransitionLightsOutPayload> TYPE = new Type<>(ID_LOCATION);

    public static final StreamCodec<FriendlyByteBuf, LevelTransitionLightsOutPayload> CODEC = StreamCodec.of(
            (buf, p) -> buf.writeInt(p.time()),
            buf -> new LevelTransitionLightsOutPayload(buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
