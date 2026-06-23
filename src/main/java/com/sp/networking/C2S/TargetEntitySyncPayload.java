package com.sp.networking.C2S;

import com.sp.SPBRevamped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TargetEntitySyncPayload(int entityId) implements CustomPacketPayload {

    public static final ResourceLocation ID_LOCATION = ResourceLocation.fromNamespaceAndPath(SPBRevamped.MOD_ID, "targ_ent");
    public static final Type<TargetEntitySyncPayload> TYPE = new Type<>(ID_LOCATION);

    public static final StreamCodec<FriendlyByteBuf, TargetEntitySyncPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeInt(payload.entityId()),
                    buf -> new TargetEntitySyncPayload(buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
