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
import net.minecraft.world.phys.Vec3;

//Behavior version of LeapAtTargetGoal used by vanilla spiders, ocelots
public class LeapAtAttackTargetBehavior<E extends LivingEntity> extends Behavior<E>
{
    private final int minJumpDistanceSqr;
    private final int maxJumpDistanceSqr;
    private final float jumpHeight;

    public LeapAtAttackTargetBehavior()
    {
        this(4, 16, 0.4f);
    }

    public LeapAtAttackTargetBehavior(int minJumpDistanceSqr, int maxJumpDistanceSqr, float jumpHeight)
    {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
        this.minJumpDistanceSqr = minJumpDistanceSqr;
        this.maxJumpDistanceSqr = maxJumpDistanceSqr;
        this.jumpHeight = jumpHeight;
    }

    protected void start(ServerLevel level, LivingEntity predator, long time)
    {
        if (predator.onGround())
        {
            Brain<?> brain = predator.getBrain();
            Optional<LivingEntity> targetOpt = brain.getMemory(MemoryModuleType.ATTACK_TARGET);
            if (targetOpt.isPresent())
            {
                LivingEntity target = targetOpt.get();
                double dist = predator.distanceToSqr(target);
                if (!(dist < minJumpDistanceSqr) && !(dist > maxJumpDistanceSqr) && (predator.getRandom().nextInt(5) == 0))
                {
                    Vec3 newMovement = new Vec3(target.getX() - predator.getX(), 0.0D, target.getZ() - predator.getZ());
                    if (newMovement.lengthSqr() > 1.0E-7D)
                    {
                        //Does not take into account mob starting speed like vanilla, rather bases speed entirely on the distance to the target
                        newMovement = newMovement.scale(0.25D);
                    }
                    predator.setDeltaMovement(newMovement.x, (double) jumpHeight, newMovement.z);
                }
            }
        }
    }

    protected boolean canStillUse(ServerLevel level, LivingEntity predator, long time)
    {
        return false;
    }
}
