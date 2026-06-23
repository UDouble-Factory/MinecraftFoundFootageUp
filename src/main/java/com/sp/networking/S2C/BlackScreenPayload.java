package com.sp.networking.S2C;

import com.sp.SPBRevamped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BlackScreenPayload(int duration, boolean shouldPauseSounds, boolean noEscape) implements CustomPacketPayload {

    public static final ResourceLocation ID_LOCATION = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "blk_screen");
    public static final Type<BlackScreenPayload> TYPE = new Type<>(ID_LOCATION);

    public static final StreamCodec<FriendlyByteBuf, BlackScreenPayload> CODEC = StreamCodec.of(
            (buf, p) -> { buf.writeInt(p.duration()); buf.writeBoolean(p.shouldPauseSounds()); buf.writeBoolean(p.noEscape()); },
            buf -> new BlackScreenPayload(buf.readInt(), buf.readBoolean(), buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
