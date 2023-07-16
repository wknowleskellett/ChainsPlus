package dev.williamknowleskellett.chains_plus.mixin;

import com.google.common.collect.Lists;

import dev.williamknowleskellett.chains_plus.ChainsPlusMod;
import dev.williamknowleskellett.chains_plus.LinkBlock;
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

    private static Direction dirArgument;

    @Inject(method = "isBlockSticky", at = @At(value = "HEAD"), cancellable = true)
    private static void addChainsSticky(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.isOf(ChainsPlusMod.LINK_BLOCK)) {
            cir.setReturnValue(true);
        }
    }

    // Inject at each call to isAdjacentBlockStuck and capture the locals, place
    // them in blockStateArgument1 and blockStateArgument2

    // Inject into isAdjacentBlockStuck and use blockStateArgument1 and
    // blockStateArgument2 to figure out your shit

    @Inject(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
    private void tryMoveArgumentCapture(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        dirArgument = this.motionDirection;
    }

    @Inject(method = "tryMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void canMoveAdjacentBlockArgumentCapture(BlockPos pos,
            CallbackInfoReturnable<Boolean> cir,
            BlockState blockState, Direction[] var3, int var4, int var5, Direction direction, BlockPos blockPos, BlockState blockState2) {
        dirArgument = direction;
    }

    @Inject(method = "isAdjacentBlockStuck", at = @At(value = "HEAD"), cancellable = true)
    private static void isAdjacentBlockStuckInjection(BlockState blockState1, BlockState blockState2, CallbackInfoReturnable<Boolean> cir) {

        if (blockState2.isOf(ChainsPlusMod.LINK_BLOCK)
                && !LinkBlock.hasSide(blockState2, dirArgument)) {
            cir.setReturnValue(false);
        } else if (blockState1.isOf(ChainsPlusMod.LINK_BLOCK)) {
            cir.setReturnValue(LinkBlock.hasSide(blockState1, dirArgument.getOpposite()));
        }
    }
}
