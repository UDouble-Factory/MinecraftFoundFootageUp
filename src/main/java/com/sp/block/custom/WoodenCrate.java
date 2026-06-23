package com.sp.block.custom;

import com.sp.block.entity.WoodenCrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class WoodenCrate extends BaseEntityBlock {
    public WoodenCrate(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof WoodenCrateBlockEntity) {
                player.openMenu((WoodenCrateBlockEntity) blockEntity);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Container) {
                Containers.dropContents(world, pos, (Container)blockEntity);
            }

            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WoodenCrateBlockEntity(pos, state );
    }
}
