package com.sp.mixin.respawnsystem;

import com.sp.init.BackroomsLevels;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {

    @Unique ServerPlayer targetPlayer;

    @Inject(method = "respawn", at = @At("HEAD"))
    private void setTargetPlayer(ServerPlayer player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir) {
        this.targetPlayer = player;
    }

    @Redirect(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnPositionAndUseSpawnBlock(ZLnet/minecraft/world/level/portal/DimensionTransition$PostDimensionTransition;)Lnet/minecraft/world/level/portal/DimensionTransition;"))
    private DimensionTransition redirectRespawnTransition(ServerPlayer instance, boolean bl, DimensionTransition.PostDimensionTransition postDimensionTransition) {
        if (BackroomsLevels.isInBackrooms(targetPlayer.level().dimension())) {
            ServerLevel currentWorld = (ServerLevel) targetPlayer.level();

            Vec3 respawnPos;
            if (targetPlayer.getLastDeathLocation().isPresent()) {
                Vec3 lastDeathPos = targetPlayer.getLastDeathLocation().get().pos().getCenter();
                if (lastDeathPos.y < 0) {
                    respawnPos = BlockPos.containing(BackroomsLevels.getCurrentLevelsOrigin(currentWorld.dimension())).getCenter();
                } else {
                    respawnPos = lastDeathPos;
                }
            } else {
                respawnPos = BlockPos.containing(BackroomsLevels.getCurrentLevelsOrigin(currentWorld.dimension())).getCenter();
            }

            return new DimensionTransition(
                    currentWorld,
                    respawnPos,
                    Vec3.ZERO,
                    targetPlayer.getYRot(),
                    targetPlayer.getXRot(),
                    false,
                    postDimensionTransition
            );
        }

        return instance.findRespawnPositionAndUseSpawnBlock(bl, postDimensionTransition);
    }
}
