package dev.williamknowleskellett.chains_plus;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
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

        if (blockState.isOf(ChainsPlusMod.LINK_BLOCK) && !LinkBlock.hasSide(blockState, context.getSide().getOpposite())) {
            ChainsPlusMod.LOGGER.info("link used on link valid side");
            world.playSound(context.getPlayer(), oppPos, SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.BLOCKS, 1.0F,
                    1.0F);
            return LinkBlock.addDir(blockState, world, oppPos, context.getSide().getOpposite());
        }
        return super.useOnBlock(context);
    }

}
