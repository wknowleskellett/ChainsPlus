package dev.williamknowleskellett.chains_plus;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LinkBlock extends PillarBlock implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty EDIT_DOWN = BooleanProperty.of("edit_down");
    public static final BooleanProperty EDIT_UP = BooleanProperty.of("edit_up");
    public static final BooleanProperty EDIT_NORTH = BooleanProperty.of("edit_north");
    public static final BooleanProperty EDIT_SOUTH = BooleanProperty.of("edit_south");
    public static final BooleanProperty EDIT_WEST = BooleanProperty.of("edit_west");
    public static final BooleanProperty EDIT_EAST = BooleanProperty.of("edit_east");
    protected static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(6.5D, 0.0D, 6.5D, 9.5D, 6.5D, 9.5D);
    protected static final VoxelShape UP_SHAPE = Block.createCuboidShape(6.5D, 6.5D, 6.5D, 9.5D, 16.0D, 9.5D);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(6.5D, 6.5D, 0.0D, 9.5D, 9.5D, 6.5D);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(6.5D, 6.5D, 9.5D, 9.5D, 9.5D, 16.0D);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0D, 6.5D, 6.5D, 6.5D, 9.5D, 9.5D);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(9.5D, 6.5D, 6.5D, 16.0D, 9.5D, 9.5D);
    protected static final VoxelShape CENTER_SHAPE = Block.createCuboidShape(6.5D, 6.5D, 6.5D, 9.5D, 9.5D, 9.5D);

    public LinkBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(
                (BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) this.stateManager
                        .getDefaultState()).with(AXIS, Direction.Axis.Y)).with(WATERLOGGED, false))
                        .with(EDIT_DOWN, false)).with(EDIT_UP, false)).with(EDIT_NORTH, false)).with(EDIT_SOUTH, false))
                        .with(EDIT_WEST, false)).with(EDIT_EAST, false));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape CHAIN_SHAPE = CENTER_SHAPE;
        if (state.get(EDIT_DOWN) != (state.get(AXIS) == Direction.Axis.Y))
            CHAIN_SHAPE = VoxelShapes.union(CHAIN_SHAPE, DOWN_SHAPE);
        if (state.get(EDIT_UP) != (state.get(AXIS) == Direction.Axis.Y))
            CHAIN_SHAPE = VoxelShapes.union(CHAIN_SHAPE, UP_SHAPE);
        if (state.get(EDIT_NORTH) != (state.get(AXIS) == Direction.Axis.Z))
            CHAIN_SHAPE = VoxelShapes.union(CHAIN_SHAPE, NORTH_SHAPE);
        if (state.get(EDIT_SOUTH) != (state.get(AXIS) == Direction.Axis.Z))
            CHAIN_SHAPE = VoxelShapes.union(CHAIN_SHAPE, SOUTH_SHAPE);
        if (state.get(EDIT_WEST) != (state.get(AXIS) == Direction.Axis.X))
            CHAIN_SHAPE = VoxelShapes.union(CHAIN_SHAPE, WEST_SHAPE);
        if (state.get(EDIT_EAST) != (state.get(AXIS) == Direction.Axis.X))
            CHAIN_SHAPE = VoxelShapes.union(CHAIN_SHAPE, EAST_SHAPE);
        return CHAIN_SHAPE;
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        boolean down = ctx.getSide() == Direction.DOWN;
        boolean up = ctx.getSide() == Direction.UP;
        boolean north = ctx.getSide() == Direction.NORTH;
        boolean south = ctx.getSide() == Direction.SOUTH;
        boolean west = ctx.getSide() == Direction.WEST;
        boolean east = ctx.getSide() == Direction.EAST;
        return (BlockState) super.getPlacementState(ctx).with(WATERLOGGED, bl).with(AXIS, ctx.getSide().getAxis())
                .with(EDIT_DOWN, down).with(EDIT_UP, up).with(EDIT_NORTH, north).with(EDIT_SOUTH, south)
                .with(EDIT_WEST, west).with(EDIT_EAST, east);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (item == ChainsPlusMod.LINK_ITEM) {
            if (state.getBlock() == ChainsPlusMod.LINK_BLOCK && !hasSide(state, hit.getSide())) {
                world.playSound(player, pos, SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return addDir(state, world, pos, hit.getSide());
            }
            return ActionResult.FAIL;
        }
        if (item == Items.SHEARS) {
            HitResult hitResult = world.rayTrace(new RayTraceContext(player.getCameraPosVec(1.0F),
                    player.getCameraPosVec(1.0F)
                            .add((double) (MathHelper.sin(-player.yaw * 0.017453292F - 3.1415927F)
                                    * -MathHelper.cos(-player.pitch * 0.017453292F)) * 5.0D,
                                    (double) (MathHelper.sin(-player.pitch * 0.017453292F)) * 5.0D,
                                    (double) (MathHelper.cos(-player.yaw * 0.017453292F - 3.1415927F)
                                            * -MathHelper.cos(-player.pitch * 0.017453292F)) * 5.0D),
                    RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.SOURCE_ONLY, player));
            Vec3d hitPos = hitResult.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
            return removeDir(state, world, pos, player, hitPos);
        }
        return ActionResult.FAIL;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState,
            WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if ((Boolean) state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, EDIT_DOWN, EDIT_UP, EDIT_NORTH, EDIT_SOUTH, EDIT_WEST, EDIT_EAST, AXIS);
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean) state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public static boolean hasSide(BlockState state, Direction side) {
        switch (side) {
            case DOWN:
                return state.get(Properties.AXIS).equals(Direction.Axis.Y) != state.get(EDIT_DOWN);
            case UP:
                return state.get(Properties.AXIS).equals(Direction.Axis.Y) != state.get(EDIT_UP);
            case NORTH:
                return state.get(Properties.AXIS).equals(Direction.Axis.Z) != state.get(EDIT_NORTH);
            case SOUTH:
                return state.get(Properties.AXIS).equals(Direction.Axis.Z) != state.get(EDIT_SOUTH);
            case WEST:
                return state.get(Properties.AXIS).equals(Direction.Axis.X) != state.get(EDIT_WEST);
            case EAST:
                return state.get(Properties.AXIS).equals(Direction.Axis.X) != state.get(EDIT_EAST);
        }
        return false;
    }

    public static ActionResult addDir(BlockState state, World world, BlockPos pos, Direction side) {
        if (hasSide(state, side))
            return ActionResult.FAIL;
        ChainsPlusMod.LOGGER.info("addDir "+side);
        
        switch (side) {
            case DOWN:
                world.setBlockState(pos, state.with(EDIT_DOWN, !state.get(EDIT_DOWN)));
                break;
            case UP:
                world.setBlockState(pos, state.with(EDIT_UP, !state.get(EDIT_UP)));
                break;
            case NORTH:
                world.setBlockState(pos, state.with(EDIT_NORTH, !state.get(EDIT_NORTH)));
                break;
            case SOUTH:
                world.setBlockState(pos, state.with(EDIT_SOUTH, !state.get(EDIT_SOUTH)));
                break;
            case WEST:
                world.setBlockState(pos, state.with(EDIT_WEST, !state.get(EDIT_WEST)));
                break;
            case EAST:
                world.setBlockState(pos, state.with(EDIT_EAST, !state.get(EDIT_EAST)));
                break;
        }
        return ActionResult.success(world.isClient());
    }

    public static ActionResult removeDir(BlockState state, World world, BlockPos pos, PlayerEntity player,
            Vec3d hitPos) {
        double x = hitPos.getX();
        double y = hitPos.getY();
        double z = hitPos.getZ();

        BooleanProperty editDirection = null;
        
        if (y < 6.5 / 16 && hasSide(state, Direction.DOWN)) {
            editDirection = EDIT_DOWN;
        } else if (y > 9.5 / 16 && hasSide(state, Direction.UP)) {
            editDirection = EDIT_UP;
        } else if (z < 6.5 / 16 && hasSide(state, Direction.NORTH)) {
            editDirection = EDIT_NORTH;
        } else if (z > 9.5 / 16 && hasSide(state, Direction.SOUTH)) {
            editDirection = EDIT_SOUTH;
        } else if (x < 6.5 / 16 && hasSide(state, Direction.WEST)) {
            editDirection = EDIT_WEST;
        } else if (x > 9.5 / 16 && hasSide(state, Direction.EAST)) {
            editDirection = EDIT_EAST;
        }

        if (editDirection == null)
            return ActionResult.FAIL;

        world.playSound(player, pos, SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.setBlockState(pos, state.with(editDirection, !state.get(editDirection)));
        return ActionResult.success(true);
    }
}
