package net.cwjn.idf.rpg.bonfire;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.gui.BonfireScreen;
import net.cwjn.idf.gui.CreateBonfireScreen;
import net.cwjn.idf.gui.StatsScreen;
import net.cwjn.idf.rpg.RpgPlayer;
import net.cwjn.idf.rpg.bonfire.entity.BonfireBlockEntity;
import net.cwjn.idf.rpg.bonfire.entity.BonfireEntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BonfireBlock extends FallingBlock implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Property<Boolean> ACTIVE = BooleanProperty.create("active");
    private static final VoxelShape BASE_SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    private static final VoxelShape SWORD_SHAPE = Block.box(5, 2, 5, 11, 20, 11);

    protected BonfireBlock(Properties properties) {
        super(properties.lightLevel(BonfireBlock::getLightValue));
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
    }

    public static int getLightValue(BlockState state) {
        return state.getValue(ACTIVE) ? 8 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(ACTIVE, false);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BonfireEntityRegistry.BONFIRE_BASE.get().create(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getShadeBrightness(BlockState p_60472_, BlockGetter p_60473_, BlockPos p_60474_) {
        return 1;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (level.getBlockEntity(pos) != null) {
            if (level.getBlockEntity(pos) instanceof BonfireBlockEntity) {
                if (((BonfireBlockEntity)level.getBlockEntity(pos)).isActive()) {
                    return Shapes.join(BASE_SHAPE, SWORD_SHAPE, BooleanOp.OR);
                }
            }
        }
        return BASE_SHAPE;
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return getShape(state, level, pos, ctx);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.getBlockEntity(pos) instanceof BonfireBlockEntity be) {
            RpgPlayer rpgPlayer = (RpgPlayer)player;
            if (be.isActive()) {
                if (level.isClientSide) {
                    Minecraft.getInstance().setScreen(new BonfireScreen());
                    //Minecraft.getInstance().setScreen(new StatsScreen(true));
                } else {
                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                }
                return InteractionResult.SUCCESS;
            } else {
                if (level.isClientSide) {
                    if (player.getItemInHand(hand).getAttributeModifiers(EquipmentSlot.MAINHAND).
                            get(IDFAttributes.FIRE_DAMAGE.get()).stream().mapToDouble(AttributeModifier::getAmount).sum() > 0) {
                        Minecraft.getInstance().setScreen(new CreateBonfireScreen(be));
                        return InteractionResult.SUCCESS;
                    } else {
                        return InteractionResult.FAIL;
                    }
                } else {
                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.use(state, level, pos, player, hand, result);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof BonfireBlockEntity be) {
            return !be.isActive();
        }
        return super.canHarvestBlock(state, level, pos, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (newState.getBlock() == Blocks.AIR) {
            if (!level.isClientSide()) {
                BonfireBlockEntity be = (BonfireBlockEntity) level.getBlockEntity(pos);
                if (be != null && be.isActive()) {
                    if (be.getOwner() != null) {
                        RpgPlayer rpgPlayer = (RpgPlayer) level.getPlayerByUUID(be.getOwner());
                        rpgPlayer.removeBonfire(be.getId());
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) != null) {
            BonfireBlockEntity be = (BonfireBlockEntity) level.getBlockEntity(pos);
            if (be.isActive()) {
                double d0 = (double)pos.getX() + 0.5D + random.nextDouble() * 3.0D / 16.0D;
                double d1 = (double)pos.getY() + 0.2D;
                double d2 = (double)pos.getZ() + 0.5D + random.nextDouble() / 16.0D;
                double d4 = random.nextDouble() * 0.6D - 0.3D;
                double d5 = random.nextDouble() * 0.6D - 0.3D;
                double d00 = (double)pos.getX() + 0.5D + random.nextDouble() * 3.0D / 16.0D;
                double d01 = (double)pos.getY() + 0.2D;
                double d02 = (double)pos.getZ() + 0.5D + random.nextDouble() / 16.0D;
                double d04 = random.nextDouble() * 0.6D - 0.3D;
                double d05 = random.nextDouble() * 0.6D - 0.3D;
                if (random.nextDouble() < 0.1D) {
                    level.playLocalSound((double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F, 1.0F, false);
                }
                level.addParticle(ParticleTypes.FLAME, d00 + d05, d01, d02 + d04, 0.0D, 0.05D, 0.0D);
                level.addParticle(ParticleTypes.FLAME, d00 + d05, d01, d02 + d04, 0.0D, 0.05D, 0.0D);
                level.addParticle(ParticleTypes.FLAME, d0 + d5, d1, d2 + d4, 0.0D, 0.05D, 0.0D);
                level.addParticle(ParticleTypes.FLAME, d0 + d5, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
            }
        }
        super.animateTick(state, level, pos, random);
    }

}
