package com.sp.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureBlockEntity.class)
public class StructureBlockEntityMixin {
    @Shadow
    private Vec3i structureSize;

    @Inject(method = "loadAdditional", at = @At("RETURN"))
    public void readNbt(CompoundTag nbt, HolderLookup.Provider provider, CallbackInfo ci) {
        int l = nbt.getInt("sizeX");
        int m = nbt.getInt("sizeY");
        int n = nbt.getInt("sizeZ");
        this.structureSize = new Vec3i(l, m, n);
    }
}
