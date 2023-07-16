package dev.williamknowleskellett.chains_plus;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LinkItem extends BlockItem {
    public LinkItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ChainsPlusMod.LOGGER.info("link used on block");
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockPos oppPos = blockPos.offset(context.getSide());
        BlockState blockState = world.getBlockState(oppPos);
        // if (blockState.isOf(ChainsPlusMod.LINK_BLOCK)) {
        //     ChainsPlusMod.LOGGER.info("link used on link");
        if (blockState.isOf(ChainsPlusMod.LINK_BLOCK) && !LinkBlock.hasSide(blockState, context.getSide().getOpposite())) {
            ChainsPlusMod.LOGGER.info("link used on link valid side");
            world.playSound(context.getPlayer(), oppPos, SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.BLOCKS, 1.0F,
                    1.0F);
            return LinkBlock.addDir(blockState, world, oppPos, context.getSide().getOpposite());
        }
        return super.useOnBlock(context);
    }

    // private boolean canAddSide(BlockState state, Direction side) {
    //     switch (side) {
    //         case DOWN:
    //             return state.get(Properties.AXIS).equals(Direction.Axis.Y) == state.get(LinkBlock.EDIT_DOWN);
    //         case UP:
    //             return state.get(Properties.AXIS).equals(Direction.Axis.Y) == state.get(LinkBlock.EDIT_UP);
    //         case NORTH:
    //             return state.get(Properties.AXIS).equals(Direction.Axis.Z) == state.get(LinkBlock.EDIT_NORTH);
    //         case SOUTH:
    //             return state.get(Properties.AXIS).equals(Direction.Axis.Z) == state.get(LinkBlock.EDIT_SOUTH);
    //         case WEST:
    //             return state.get(Properties.AXIS).equals(Direction.Axis.X) == state.get(LinkBlock.EDIT_WEST);
    //         case EAST:
    //             return state.get(Properties.AXIS).equals(Direction.Axis.X) == state.get(LinkBlock.EDIT_EAST);
    //     }
    //     return false;
    // }

    private ActionResult addDdsir(BlockState state, World world, BlockPos pos, Direction side) {
        switch (side) {
            case DOWN:
                if (state.get(Properties.AXIS).equals(Direction.Axis.Y) == state.get(LinkBlock.EDIT_DOWN)) {
                    world.setBlockState(pos,
                            (BlockState) state.with(LinkBlock.EDIT_DOWN, !state.get(LinkBlock.EDIT_DOWN)));
                    return ActionResult.success(true);
                }
            case UP:
                if (state.get(Properties.AXIS).equals(Direction.Axis.Y) == state.get(LinkBlock.EDIT_UP)) {
                    world.setBlockState(pos, (BlockState) state.with(LinkBlock.EDIT_UP, !state.get(LinkBlock.EDIT_UP)));
                    return ActionResult.success(true);
                }
            case NORTH:
                if (state.get(Properties.AXIS).equals(Direction.Axis.Z) == state.get(LinkBlock.EDIT_NORTH)) {
                    world.setBlockState(pos,
                            (BlockState) state.with(LinkBlock.EDIT_NORTH, !state.get(LinkBlock.EDIT_NORTH)));
                    return ActionResult.success(true);
                }
            case SOUTH:
                if (state.get(Properties.AXIS).equals(Direction.Axis.Z) == state.get(LinkBlock.EDIT_SOUTH)) {
                    world.setBlockState(pos,
                            (BlockState) state.with(LinkBlock.EDIT_SOUTH, !state.get(LinkBlock.EDIT_SOUTH)));
                    return ActionResult.success(true);
                }
            case WEST:
                if (state.get(Properties.AXIS).equals(Direction.Axis.X) == state.get(LinkBlock.EDIT_WEST)) {
                    world.setBlockState(pos,
                            (BlockState) state.with(LinkBlock.EDIT_WEST, !state.get(LinkBlock.EDIT_WEST)));
                    return ActionResult.success(true);
                }
            case EAST:
                if (state.get(Properties.AXIS).equals(Direction.Axis.X) == state.get(LinkBlock.EDIT_EAST)) {
                    world.setBlockState(pos,
                            (BlockState) state.with(LinkBlock.EDIT_EAST, !state.get(LinkBlock.EDIT_EAST)));
                    return ActionResult.success(true);
                }
        }
        return ActionResult.FAIL;
    }
}
