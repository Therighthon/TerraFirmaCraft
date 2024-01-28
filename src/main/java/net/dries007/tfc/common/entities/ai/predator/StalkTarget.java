/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.predator.FelinePredator;

public class StalkTarget<E extends LivingEntity> extends Behavior<E>
{
    private final int startStalkDistanceSqr;
    private final int endStalkDistanceSqr;
    private final float sneakSpeed;

    public StalkTarget(int startStalkDistanceSqr, int endStalkDistanceSqr, float sneakSpeed)
    {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
        this.sneakSpeed = sneakSpeed;
        this.endStalkDistanceSqr = endStalkDistanceSqr;
        this.startStalkDistanceSqr = startStalkDistanceSqr;
    }

    protected boolean canStillUse(ServerLevel level, LivingEntity predator, long time)
    {
        Brain<?> brain = predator.getBrain();
        //Should not sneak up on something that has already attacked them
        Optional<LivingEntity> targetOpt = brain.getMemory(MemoryModuleType.ATTACK_TARGET);
        if (targetOpt.isPresent())
        {
            LivingEntity target = targetOpt.get();
            if (brain.getMemory(MemoryModuleType.HURT_BY_ENTITY).isPresent() && target == brain.getMemory(MemoryModuleType.HURT_BY_ENTITY).get())
            {
                return false;
            }
            int targetDist = (int)predator.distanceToSqr(target);
            return (targetDist >= endStalkDistanceSqr && targetDist <= startStalkDistanceSqr);
        }
        return false;
    }

    protected void tick(ServerLevel level, E predator, long time)
    {
        Brain<?> brain = predator.getBrain();
        Optional<LivingEntity> targetOpt = brain.getMemory(MemoryModuleType.ATTACK_TARGET);
        if (targetOpt.isPresent())
        {
            LivingEntity target = targetOpt.get();
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(target.blockPosition(), this.sneakSpeed, 0));
        }
    }
}
