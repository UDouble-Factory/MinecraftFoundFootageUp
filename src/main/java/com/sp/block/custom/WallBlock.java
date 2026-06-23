package com.sp.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

@SuppressWarnings("deprecation")
public class WallBlock extends Block {
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom_wall");;

    public WallBlock(Properties settings) {
        super(settings);
    }


    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if(world.getBlockState(pos.below()).is(this.asBlock())){
            world.setBlockAndUpdate(pos, state.setValue(BOTTOM, false));
        }else{
            world.setBlockAndUpdate(pos, state.setValue(BOTTOM, true));
        }
        super.tick(state, world, pos, random);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        if(ctx.getLevel().getBlockState(ctx.getClickedPos().below()).is(this.asBlock())){
            return this.defaultBlockState().setValue(BOTTOM, false);
        }else{
            return this.defaultBlockState().setValue(BOTTOM, true);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleTick(pos, this, 0);
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BOTTOM);
    }
}
