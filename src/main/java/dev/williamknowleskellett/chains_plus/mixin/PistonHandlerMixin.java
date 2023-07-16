package dev.williamknowleskellett.chains_plus.mixin;

import com.google.common.collect.Lists;

import dev.williamknowleskellett.chains_plus.ChainsPlusMod;
import dev.williamknowleskellett.chains_plus.LinkBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import net.minecraft.block.piston.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = PistonHandler.class, priority = 419)
public abstract class PistonHandlerMixin {
    @Shadow
    @Final
    private World world;
    @Shadow
    @Final
    private BlockPos posFrom;
    @Shadow
    @Final
    private Direction motionDirection;
    @Shadow
    private final List<BlockPos> movedBlocks = Lists.newArrayList();
    @Shadow
    private final List<BlockPos> brokenBlocks = Lists.newArrayList();

    private static BlockState blockStateArgument1;
    private static BlockState blockStateArgument2;
    private static Direction dirArgument;

    @Shadow
    private static boolean isBlockSticky(Block block) {
        return isBlockSticky(block);
    }

    @Inject(method = "isBlockSticky", at = @At(value = "HEAD"), cancellable = true)
    private static void addChainsSticky(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (block == ChainsPlusMod.LINK_BLOCK) {
            cir.setReturnValue(true);
        }
    }

    // Inject at each call to isAdjacentBlockStuck and capture the locals, place
    // them in blockStateArgument1 and blockStateArgument2
    // Inject into isAdjacentBlockStuck and use blockStateArgument1 and
    // blockStateArgument2 to figure out your shit

    @Inject(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"),
            // cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void tryMoveArgumentCapture(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir,
            BlockState blockState, Block block, int i, BlockPos blockPos, Block block2) {
        BlockPos blockPos1 = pos.offset(this.motionDirection.getOpposite(), i - 1);
        BlockPos blockPos2 = pos.offset(this.motionDirection.getOpposite(), i);
        blockStateArgument1 = this.world.getBlockState(blockPos1);
        blockStateArgument2 = this.world.getBlockState(blockPos2);
        dirArgument = this.motionDirection;//.getOpposite();

        // ChainsPlusMod.LOGGER.info("tryMoveArgumentCapture ran");
    }

    @Inject(method = "canMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"),
            // cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void canMoveAdjacentBlockArgumentCapture(BlockPos pos,
            CallbackInfoReturnable<Boolean> cir,
            BlockState blockState, Direction[] var3, int var4, int var5, Direction direction, BlockPos blockPos,
            BlockState blockState2) {
        blockStateArgument1 = blockState2;
        blockStateArgument2 = blockState;
        dirArgument = direction;

        // ChainsPlusMod.LOGGER.info("canMoveAdjacentBlockArgumentCapture ran");
    }

    @Inject(method = "isAdjacentBlockStuck", at = @At(value = "HEAD"), cancellable = true)
    private static void isAdjacentBlockStuckInjection(Block block1, Block block2, CallbackInfoReturnable<Boolean> cir) {

        if (blockStateArgument1.getBlock() != block1) {
            throw new RuntimeException("blockStateArgument1 messed up");
        }
        if (blockStateArgument2.getBlock() != block2) {
            throw new RuntimeException("blockStateArgument2 messed up");
        }

        if (block2 == ChainsPlusMod.LINK_BLOCK
                && !LinkBlock.hasSide(blockStateArgument2, dirArgument)) {
            cir.setReturnValue(false);
        } else if (block1 == ChainsPlusMod.LINK_BLOCK) {
            cir.setReturnValue(LinkBlock.hasSide(blockStateArgument1, dirArgument.getOpposite()));
        }
    }

    // ///////////////////////////////////////

    // @Redirect(at = @At(value = "INVOKE",
    // target="Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
    // method = "tryMove")
    // private BlockState tryMoveSetBlockStates(World world, BlockPos pos) {
    // blockStateArgument1 = blockStateArgument2;
    // blockStateArgument2 = world.getBlockState(pos);
    // return blockStateArgument2;
    // }

    // @Inject(method = "canMoveAdjacentBlock", at = @At(value = "HEAD"),locals =
    // LocalCapture.CAPTURE_FAILHARD)
    // private void setBlockStateArgument2(BlockPos pos) {
    // blockStateArgument2 = this.world.getBlockState(pos);
    // }

    // @Redirect(at = @At(value = "INVOKE",
    // target="Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
    // method = "canMoveAdjacentBlock")
    // private BlockState canMoveAdjacentBlockGetBlockState(World world, BlockPos
    // pos) {
    // blockStateArgument1 = blockStateArgument2;
    // blockStateArgument2 = world.getBlockState(pos);
    // return blockStateArgument2;
    // }

    // @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"), method = "tryMove")
    // private boolean isAdjacentBlockStuckRedirect(Block block1, Block block2) {
    //     BlockState blockState1 = blockStateArgument1;
    //     BlockState blockState2 = blockStateArgument2;
    //     Direction dir = this.motionDirection;

    //     Block block1prime = blockState1.getBlock();
    //     Block block2prime = blockState2.getBlock();
    //     if (block1 != block1prime || block2 != block2prime) {
    //         throw new RuntimeException("William, your blockstate capturing scheme didn't work...");
    //     }

    //     if (block1 == ChainsPlusMod.LINK_BLOCK && block2 == ChainsPlusMod.LINK_BLOCK) {
    //         if (canChain(blockState1, motionDirection.getOpposite(), false) &&
    //                 canChain(blockState2, motionDirection, false))
    //             return true;
    //         else
    //             return false;
    //     } else if (block1 == ChainsPlusMod.LINK_BLOCK && !canChain(blockState1, dir,
    //             true)) {
    //         return false;
    //     } else if (block2 == ChainsPlusMod.LINK_BLOCK && (block1 != Blocks.SLIME_BLOCK && block1 != Blocks.HONEY_BLOCK)
    //             && !canChain(blockState2,
    //                     dir.getOpposite(), true)) {
    //         return false;
    //     } else {
    //         return isBlockSticky(block1) || isBlockSticky(block2);
    //     }
    // }

    // @Overwrite
    // private boolean tryMove(BlockPos pos, Direction dir) {
    //     BlockState arg2 = this.world.getBlockState(pos);
    //     Block block = arg2.getBlock();
    //     if (arg2.isAir()) {
    //         return true;
    //     } else if (!PistonBlock.isMovable(arg2, this.world, pos,
    //             this.motionDirection, false, dir)) {
    //         return true;
    //     } else if (pos.equals(this.posFrom)) {
    //         return true;
    //     } else if (this.movedBlocks.contains(pos)) {
    //         return true;
    //     } else {
    //         int i = 1;
    //         if (i + this.movedBlocks.size() > 12) {
    //             return false;
    //         } else {
    //             while (isBlockSticky(block)) {
    //                 BlockPos blockPos = pos.offset(this.motionDirection.getOpposite(), i);
    //                 BlockState arg1 = arg2;
    //                 arg2 = this.world.getBlockState(blockPos);
    //                 block = arg2.getBlock();
    //                 if (arg2.isAir() || !isAdjacentBlockStuck(arg1, arg2) ||
    //                         !PistonBlock.isMovable(arg2, this.world, blockPos, this.motionDirection,
    //                                 false, this.motionDirection.getOpposite())
    //                         || blockPos.equals(this.posFrom)) {
    //                     break;
    //                 }

    //                 ++i;
    //                 if (i + this.movedBlocks.size() > 12) {
    //                     return false;
    //                 }
    //             }

    //             int j = 0;

    //             int l;
    //             for (l = i - 1; l >= 0; --l) {
    //                 this.movedBlocks.add(pos.offset(this.motionDirection.getOpposite(), l));
    //                 ++j;
    //             }

    //             l = 1;

    //             while (true) {
    //                 BlockPos blockPos2 = pos.offset(this.motionDirection, l);
    //                 int m = this.movedBlocks.indexOf(blockPos2);
    //                 if (m > -1) {
    //                     this.setMovedBlocks(j, m);

    //                     for (int n = 0; n <= m + j; ++n) {
    //                         BlockPos blockPos3 = (BlockPos) this.movedBlocks.get(n);
    //                         if (isBlockSticky(this.world.getBlockState(blockPos3).getBlock()) &&
    //                                 !this.canMoveAdjacentBlock(blockPos3)) {
    //                             return false;
    //                         }
    //                     }

    //                     return true;
    //                 }

    //                 arg2 = this.world.getBlockState(blockPos2);
    //                 if (arg2.isAir()) {
    //                     return true;
    //                 }

    //                 if (!PistonBlock.isMovable(arg2, this.world, blockPos2, this.motionDirection,
    //                         true, this.motionDirection) || blockPos2.equals(this.posFrom)) {
    //                     return false;
    //                 }

    //                 if (arg2.getPistonBehavior() == PistonBehavior.DESTROY) {
    //                     this.brokenBlocks.add(blockPos2);
    //                     return true;
    //                 }

    //                 if (this.movedBlocks.size() >= 12) {
    //                     return false;
    //                 }

    //                 this.movedBlocks.add(blockPos2);
    //                 ++j;
    //                 ++l;
    //             }
    //         }
    //     }
    // }

    // @Shadow
    // private boolean canMoveAdjacentBlock(BlockPos pos) {
    //     return canMoveAdjacentBlock(pos);
    // }

    // @Shadow
    // private boolean isAdjacentBlockStuck(Block block1, Block block2) {
    //     return isAdjacentBlockStuck(block1, block2);
    // }

    // @Shadow
    // private void setMovedBlocks(int from, int to) {
    // }

    // @Inject(method = "canMoveAdjacentBlock", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    // private void clAdjacentBlockStuck(BlockPos pos,
    //         CallbackInfoReturnable<Boolean> cir, BlockState blockState, Direction[] var3,
    //         int var4, int var5, Direction direction, BlockPos blockPos, BlockState blockState1) {
    //     if (isAdjacentBlockStuck(blockState, blockState1) && !this.tryMove(blockPos,
    //             direction)) {
    //         cir.setReturnValue(false);
    //     }
    // }
}
