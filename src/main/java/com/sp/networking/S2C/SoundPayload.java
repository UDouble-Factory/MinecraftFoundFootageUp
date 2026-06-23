package com.sp.networking.S2C;

import com.sp.SPBRevamped;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public record SoundPayload(Holder<SoundEvent> sound, float volume, float pitch) implements CustomPacketPayload {

    public static final ResourceLocation ID_LOCATION = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "snd");
    public static final Type<SoundPayload> TYPE = new Type<>(ID_LOCATION);

    public static final StreamCodec<FriendlyByteBuf, SoundPayload> CODEC = StreamCodec.of(
            (buf, p) -> {
                SoundEvent.STREAM_CODEC.encode(buf, p.sound().value());
                buf.writeFloat(p.volume());
                buf.writeFloat(p.pitch());
            },
            buf -> new SoundPayload(Holder.direct(SoundEvent.STREAM_CODEC.decode(buf)), buf.readFloat(), buf.readFloat())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
