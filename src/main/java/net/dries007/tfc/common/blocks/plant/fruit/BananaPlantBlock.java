/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class BananaPlantBlock extends SeasonalPlantBlock implements IBushBlock, HoeOverlayBlock
{
    public static void kill(Level level, BlockPos pos)
    {
        // picking bananas kills the plant. this propagates death to the whole stalk.
        Block deadBlock = TFCBlocks.DEAD_BANANA_PLANT.get();
        if (!level.isClientSide)
        {
            BlockState deadState = deadBlock.defaultBlockState();
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos().set(pos.below());
            while (true)
            {
                BlockState foundState = level.getBlockState(mutable);
                if (!Helpers.isBlock(foundState, TFCBlocks.BANANA_PLANT.get())) break;
                level.setBlockAndUpdate(mutable, deadState.setValue(STAGE, foundState.getValue(STAGE)));
                mutable.move(Direction.DOWN);
            }
        }
    }

    public static final VoxelShape PLANT = box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    private static final VoxelShape TRUNK_0 = box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    private static final VoxelShape TRUNK_1 = box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);

    public BananaPlantBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages)
    {
        super(properties, ClimateRanges.BANANA_PLANT, productItem, stages);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final ItemInteractionResult result =super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (result.consumesAction())
        {
            kill(level, pos);
        }
        return result;
    }

    @Override
    public BlockState stateAfterPicking(BlockState state)
    {
        return TFCBlocks.DEAD_BANANA_PLANT.get().defaultBlockState().setValue(STAGE, 2);
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, Consumer<Component> text, boolean isDebug)
    {
        if (level.getBlockEntity(pos) instanceof BerryBushBlockEntity bush)
        {
            final ClimateRange range = climateRange.get();
            final BlockPos sourcePos = bush.getStemPos().below();
            text.accept(FarmlandBlock.getHydrationTooltip(level, sourcePos, range, false));
            text.accept(FarmlandBlock.getTemperatureTooltip(level, sourcePos, range, false));
        }
    }

    @Override
    public ItemStack getProductItem(RandomSource random)
    {
        return new ItemStack(productItem.get(), Mth.nextInt(random, 3, 6));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(STAGE))
            {
                case 0 -> TRUNK_0;
                case 1 -> TRUNK_1;
                default -> PLANT;
            };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(STAGE) == 2 ? Shapes.empty() : getShape(state, level, pos, context);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        // no op the superclass
    }

    @Override
    public void onUpdate(Level level, BlockPos pos, BlockState state)
    {
        // Bananas grow vertically as long as they can, until they reach stage 2
        // At that point the top block is able to fruit and flower
        // Once it is picked, the top block dies. And the plant is therefore dead.

        if (level.getBlockEntity(pos) instanceof BerryBushBlockEntity bush)
        {
            Lifecycle expectedLifecycle = getLifecycleForCurrentMonth(level, pos);
            Lifecycle previousLifecycle = state.getValue(LIFECYCLE);

            // First, check if we are even in a valid climate
            final BlockPos stemPos = bush.getStemPos();
            final ClimateRange range = climateRange.get();
            final int hydration = FarmlandBlock.getHydrationFromStormHydration(level, stemPos.below(), (int) ChunkData.get(level, pos).getStormHydration());
            final float temperature = Climate.getAverageTemperature(level, stemPos);

            // If plant can't grow here, bypass the other checks and set it to dormant
            if (!range.checkBoth(hydration, temperature, false))
            {
                level.setBlockAndUpdate(pos, state.setValue(LIFECYCLE, Lifecycle.DORMANT));
            }
            else
            {
                // Otherwise, we do a point-by-point evaluation of how the bush should have grown.
                // We only do this up to a year. Why? Because bananas only grow so big anyways.
                final long deltaTicks = Math.min(bush.getTicksSinceBushUpdate(), Calendars.SERVER.getCalendarTicksInYear());
                final long currentCalendarTick = Calendars.SERVER.getCalendarTicks();
                final long lastUpdateTick = currentCalendarTick - deltaTicks;

                final long ticksPerCycle = ICalendar.CALENDAR_TICKS_IN_DAY * 5;
                // Check how many times to run the cycle
                final int cyclesToRun = (int) (bush.getTicksSinceBushUpdate() / ticksPerCycle);
                // Preserve any remainder ticks
                bush.setLastBushTick(lastUpdateTick + cyclesToRun * ticksPerCycle);

                // Actually run cycles
                for (int cycle = 1; cycle <= cyclesToRun ; cycle++)
                {
                    final long cycleCalendarTick = lastUpdateTick + (long) cycle * ticksPerCycle;

                    // Only do anything this cycle if the plant would not have been dormant at this time
                    final Lifecycle targetLifecycle = getLifecycleForMonth(ICalendar.getMonthOfYear(cycleCalendarTick, Calendars.SERVER.getCalendarDaysInMonth()));
                    final Lifecycle lifecycle = previousLifecycle.advanceTowards(targetLifecycle);

                    if (!(targetLifecycle == Lifecycle.DORMANT))
                    {
                        int stage = state.getValue(STAGE);
                        if (stage < 2)
                        {
                            BlockPos abovePos = pos.above();
                            // Advance both the stage (randomly, if the previous month was healthy), and lifecycle (if the at-the-time conditions were valid)
                            if (previousLifecycle.active())
                            {
                                BlockPos downPos = pos.below(3);
                                // increase the stage 1/3 of the time, or always if we realize we're starting to get tall
                                if (!Helpers.isBlock(level.getBlockState(abovePos), this) && (level.random.nextInt(4) == 0 || Helpers.isBlock(level.getBlockState(downPos), this)))
                                {
                                    stage++;
                                }

                                // we don't allow the trunk blocks to fruit or flower
                                previousLifecycle = Lifecycle.HEALTHY;
                            }

                            // Always set to healthy because if it is dormant it won't grow, and these blocks should never fruit
                            BlockState newState = state.setValue(STAGE, stage).setValue(LIFECYCLE, Lifecycle.HEALTHY);

                            // bananas only grow for stages 0 and 1
                            if (previousLifecycle.active())
                            {
                                if (level.isEmptyBlock(abovePos) && level.canSeeSky(abovePos))
                                {
                                    level.setBlockAndUpdate(abovePos, newState);
                                    if (level.getBlockEntity(abovePos) instanceof BerryBushBlockEntity newBush)
                                    {
                                        newBush.setLastBushTick(cycleCalendarTick);
                                        newBush.setStemPos(stemPos);
                                        // TODO: Replace this with something that actually catches up on ticks
                                        if (level.getBlockState(abovePos).getBlock() instanceof BananaPlantBlock newPlant)
                                        {
                                            newPlant.onUpdate(level, pos, state);
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            level.setBlockAndUpdate(pos, state.setValue(STAGE, stage).setValue(LIFECYCLE, lifecycle));
                        }
                    }

                    previousLifecycle = lifecycle;
                }

                //TODO: Might want to replace this
                checkAndSetDormant(level, pos, state, previousLifecycle, expectedLifecycle);
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getValue(STAGE) == 2 && newState.isAir())
        {
            kill(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return Helpers.isBlock(belowState, TFCTags.Blocks.BUSH_PLANTABLE_ON) || Helpers.isBlock(belowState, this);
    }
}
