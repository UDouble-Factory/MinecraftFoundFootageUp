package com.sp.networking.C2S;

import com.sp.SPBRevamped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SeeActiveSkinwalkerSyncPayload(boolean canSeeSkinwalker) implements CustomPacketPayload {

    public static final ResourceLocation ID_LOCATION = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "see_skin");
    public static final Type<SeeActiveSkinwalkerSyncPayload> TYPE = new Type<>(ID_LOCATION);

    public static final StreamCodec<FriendlyByteBuf, SeeActiveSkinwalkerSyncPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeBoolean(payload.canSeeSkinwalker()),
                    buf -> new SeeActiveSkinwalkerSyncPayload(buf.readBoolean())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
