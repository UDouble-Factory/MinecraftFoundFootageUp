package com.sp.mixin.respawnsystem;

import com.sp.init.BackroomsLevels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {

    @Shadow @Final private MinecraftServer server;

    @Unique ServerPlayer targetPlayer;

    @Inject(method = "respawn", at = @At("HEAD"))
    private void setTargetPlayer(ServerPlayer player, boolean alive, CallbackInfoReturnable<ServerPlayer> cir){
        this.targetPlayer = player;
    }

    @Redirect(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getRespawnPosition()Lnet/minecraft/core/BlockPos;"))
    private BlockPos setSpawnPointPos(ServerPlayer instance){
        if(BackroomsLevels.isInBackrooms(targetPlayer.level().dimension())) {
            return targetPlayer.getLastDeathLocation().isPresent() ? targetPlayer.getLastDeathLocation().get().pos() : instance.getRespawnPosition();
        }
        return instance.getRespawnPosition();
    }


    @Redirect(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"))
    private @Nullable ServerLevel getCurrentWorld(MinecraftServer instance, ResourceKey<Level> key){
        if(BackroomsLevels.isInBackrooms(targetPlayer.level().dimension())) {
            return instance.getLevel(targetPlayer.level().dimension());
        }

        return instance.getLevel(key);
    }



    @Redirect(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;findRespawnPositionAndUseSpawnBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;FZZ)Ljava/util/Optional;"))
    private Optional<Vec3> respawn(ServerLevel world, BlockPos pos, float angle, boolean forced, boolean alive){
        if(BackroomsLevels.isInBackrooms(targetPlayer.level().dimension())) {
            if (targetPlayer.getLastDeathLocation().isPresent()) {
                Vec3 lastDeathPos = targetPlayer.getLastDeathLocation().get().pos().getCenter();
                if(lastDeathPos.y < 0){
                    return Optional.of(BlockPos.containing(BackroomsLevels.getCurrentLevelsOrigin(world.dimension())).getCenter());
                }
                return Optional.of(targetPlayer.getLastDeathLocation().get().pos().getCenter());
            }
        }
        return Player.findRespawnPositionAndUseSpawnBlock(world, pos, angle, forced, alive);
    }


    @ModifyArgs(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;moveTo(DDDFF)V"))
    private void setSpawnAngle(Args args){
        if(BackroomsLevels.isInBackrooms(targetPlayer.level().dimension())) {
            args.set(3, targetPlayer.getYRot());
            args.set(4, targetPlayer.getXRot());
        }
    }


    @ModifyArgs(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setRespawnPosition(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FZZ)V"))
    private void respawn2(Args args){
        if(BackroomsLevels.isInBackrooms(targetPlayer.level().dimension())) {
            ServerLevel currentWorld = this.server.getLevel(targetPlayer.level().dimension());
            Optional<GlobalPos> lastDeathPos = targetPlayer.getLastDeathLocation();

            if (currentWorld != null) {
                if (lastDeathPos.isPresent()) {
                    BlockPos pos = lastDeathPos.get().pos();

                    if (pos.getY() < 0){
                        pos = BlockPos.containing(BackroomsLevels.getCurrentLevelsOrigin(currentWorld.dimension()));
                    }

                    args.set(0, currentWorld.dimension());
                    args.set(1, pos);
                    args.set(2, args.get(2));
                    args.set(3, args.get(3));
                    args.set(4, args.get(4));
                }
            }
        }
    }

}
