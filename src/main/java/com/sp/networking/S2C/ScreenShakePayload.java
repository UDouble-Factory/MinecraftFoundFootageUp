package com.sp.networking.S2C;

import com.sp.SPBRevamped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ScreenShakePayload(double speed, double trauma) implements CustomPacketPayload {

    public static final ResourceLocation ID_LOCATION = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "scr_shake");
    public static final Type<ScreenShakePayload> TYPE = new Type<>(ID_LOCATION);

    public static final StreamCodec<FriendlyByteBuf, ScreenShakePayload> CODEC = StreamCodec.of(
            (buf, p) -> { buf.writeDouble(p.speed()); buf.writeDouble(p.trauma()); },
            buf -> new ScreenShakePayload(buf.readDouble(), buf.readDouble())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
