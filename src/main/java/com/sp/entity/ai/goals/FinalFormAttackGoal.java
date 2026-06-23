package com.sp.entity.ai.goals;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class FinalFormAttackGoal extends MeleeAttackGoal {
    private final SkinWalkerComponent component;

    public FinalFormAttackGoal(SkinWalkerEntity entity) {
        super(entity, 1.15, false);
        this.component = InitializeComponents.SKIN_WALKER.get(entity);
    }

    @Override
    public boolean canUse() {
        if(this.component.isInTrueForm()) {
            long l = this.mob.level().getGameTime();
            if (l - this.lastCanUseCheck < 20L) {
                return false;
            } else {
                this.lastCanUseCheck = l;
                LivingEntity livingEntity = this.mob.getTarget();
                if (livingEntity == null) {
                    this.moveToLastKnownLocation();
                    return false;
                } else if (!livingEntity.isAlive()) {
                    this.moveToLastKnownLocation();
                    return false;
                } else {
                    this.path = this.mob.getNavigation().createPath(livingEntity, 0);
                    return this.path != null
                            ? true
                            : this.getAttackReachSqr(livingEntity) >= this.mob.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                }
            }
        }

        return false;
    }

    private void moveToLastKnownLocation() {
        BlockPos pos = this.component.getLastKnownTargetLocation();

        if(pos != null){
            this.mob.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.0);
        }
    }


}
