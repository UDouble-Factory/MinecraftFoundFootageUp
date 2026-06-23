package com.sp.mixin.rain;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sp.init.BackroomsLevels;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerWorldNoHorseTrapMixin {
    @WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public boolean spbrevamped$noHorseTrapsInBackroomsLevel(ServerLevel instance, Entity entity, Operation<Boolean> original) {
        if (BackroomsLevels.isInBackroomsLevel((ServerLevel) (Object) this, BackroomsLevels.LEVEL324_BACKROOMS_LEVEL)) {
            return false;
        }

        return original.call(instance, entity);
    }
}
