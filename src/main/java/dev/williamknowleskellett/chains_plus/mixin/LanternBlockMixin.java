package dev.williamknowleskellett.chains_plus.mixin;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.williamknowleskellett.chains_plus.ChainsPlusMod;

@Mixin(LanternBlock.class)
public class LanternBlockMixin extends Block implements Waterloggable {
    @Shadow
    public static final BooleanProperty HANGING;

    public LanternBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("RETURN"), method = "getPistonBehavior(Lnet/minecraft/block/BlockState;)Lnet/minecraft/block/piston/PistonBehavior;", cancellable = true)
    public void getPistonBehavior(BlockState state, CallbackInfoReturnable<PistonBehavior> cir) {
        cir.setReturnValue(PistonBehavior.NORMAL);
    }

    @Inject(at = @At("HEAD"), method = "canPlaceAt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    public void canPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!ChainsPlusMod.isLanternGravity)
            cir.setReturnValue(true);
    }

    static {
        HANGING = Properties.HANGING;
    }
}
