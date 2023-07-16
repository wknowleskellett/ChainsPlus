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
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void tryMoveArgumentCapture(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir,
            BlockState blockState, Block block, int i, BlockPos blockPos, Block block2) {
        BlockPos blockPos1 = pos.offset(this.motionDirection.getOpposite(), i - 1);
        BlockPos blockPos2 = pos.offset(this.motionDirection.getOpposite(), i);
        blockStateArgument1 = this.world.getBlockState(blockPos1);
        blockStateArgument2 = this.world.getBlockState(blockPos2);
        dirArgument = this.motionDirection;
    }

    @Inject(method = "canMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void canMoveAdjacentBlockArgumentCapture(BlockPos pos,
            CallbackInfoReturnable<Boolean> cir,
            BlockState blockState, Direction[] var3, int var4, int var5, Direction direction, BlockPos blockPos,
            BlockState blockState2) {
        blockStateArgument1 = blockState2;
        blockStateArgument2 = blockState;
        dirArgument = direction;
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
}
