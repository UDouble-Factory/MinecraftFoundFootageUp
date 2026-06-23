package com.sp.mixin.collision;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class IsGroundedCheckMixin {
//    @Shadow
//    public abstract World getWorld();
//
//    @Shadow
//    public abstract void onLanding();
//
//    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/BlockHitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), locals = LocalCapture.CAPTURE_FAILSOFT)
//    public void move(MovementType movementType, Vec3d movement, CallbackInfo ci, Vec3d vec3d, double d, BlockHitResult blockHitResult) {
//        if (CollisionHelper.doesCollide((Entity) (Object) this, movement)) {
//            this.onLanding();
//        }
//    }
}
