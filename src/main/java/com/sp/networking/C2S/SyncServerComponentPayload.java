package com.sp.networking.C2S;

import com.sp.SPBRevamped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncServerComponentPayload(boolean value, String component) implements CustomPacketPayload {

    public static final ResourceLocation ID_LOCATION = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "comp_sync");
    public static final Type<SyncServerComponentPayload> TYPE = new Type<>(ID_LOCATION);

    public static final StreamCodec<FriendlyByteBuf, SyncServerComponentPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeBoolean(payload.value());
                        buf.writeUtf(payload.component());
                    },
                    buf -> new SyncServerComponentPayload(buf.readBoolean(), buf.readUtf())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
