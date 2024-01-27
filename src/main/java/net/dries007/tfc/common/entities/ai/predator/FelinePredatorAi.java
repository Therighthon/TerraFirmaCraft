/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.List;
import java.util.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;

import net.dries007.tfc.common.entities.ai.SetLookTarget;

import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.predator.FelinePredator;
import net.dries007.tfc.common.entities.predator.Predator;

public class FelinePredatorAi
{
    public static final int STALK_START_DIST_SQR = 400;

    public static final ImmutableList<SensorType<? extends Sensor<? super FelinePredator>>> SENSOR_TYPES = Util.make(() -> {
        List<SensorType<? extends Sensor<? super FelinePredator>>> list = Lists.newArrayList(PredatorAi.SENSOR_TYPES);
        return ImmutableList.copyOf(list);
    });

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = Util.make(() -> {
        List<MemoryModuleType<?>> list = Lists.newArrayList(PredatorAi.MEMORY_TYPES);
        return ImmutableList.copyOf(list);
    });

    public static Brain<?> makeBrain(Brain<? extends Predator> brain, Predator predator)
    {
        initCoreActivity(brain);
        initHuntActivity(brain);
        PredatorAi.initRetreatActivity(brain);
        PredatorAi.initRestActivity(brain);
        if (predator instanceof FelinePredator)
        {
            initFightActivity(brain, ((FelinePredator) predator).getCrouchSpeed(), ((FelinePredator) predator).getSprintSpeed(), ((FelinePredator) predator).getPounceDistanceSquared());
        }
        else
        {
            initFightActivity(brain, 0.52f, 1.15f, 25);
        }

        brain.setSchedule(predator.diurnal ? TFCBrain.DIURNAL.get() : TFCBrain.NOCTURNAL.get());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(TFCBrain.HUNT.get());
        brain.setActiveActivityIfPossible(TFCBrain.HUNT.get());
        brain.updateActivityFromSchedule(predator.level().getDayTime(), predator.level().getGameTime());

        return brain;
    }

    public static void initCoreActivity(Brain<? extends Predator> brain)
    {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            new AggressiveSwim(0.8F),
            new LookAtTargetSink(45, 90),
            new MoveToTargetSink(),
            new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
        ));
    }

    public static void initHuntActivity(Brain<? extends Predator> brain)
    {
        brain.addActivity(TFCBrain.HUNT.get(), 10, ImmutableList.of(
            PredatorBehaviors.becomePassiveIf(p -> p.getBrain().hasMemoryValue(MemoryModuleType.TEMPTING_PLAYER), 400),
            StartAttacking.create(PredatorAi::getAttackTarget),
            SetLookTarget.create(8.0F, UniformInt.of(30, 60)),
            PredatorBehaviors.findNewHome(),
            BabyFollowAdult.create(UniformInt.of(5, 16), 1.25F), // babies follow any random adult around
            PredatorAi.createIdleMovementBehaviors(),
            PredatorBehaviors.tickScheduleAndWake()
        ));
    }

    public static void initFightActivity(Brain<? extends Predator> brain, float crouchSpeed, float sprintSpeed, int stalkEndDistSquared)
    {
        brain.addActivityAndRemoveMemoriesWhenStopped(Activity.FIGHT, ImmutableList.of(
            Pair.of(0, PredatorBehaviors.becomePassiveIf(p -> p.getHealth() < 5f, 200)),
            Pair.of(1, new StalkTarget<>(STALK_START_DIST_SQR, stalkEndDistSquared, crouchSpeed)),
            Pair.of(2, new LeapAtAttackTargetBehavior<>(4, stalkEndDistSquared, 0.4f)),
            Pair.of(3, SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(sprintSpeed)),
            Pair.of(4, MeleeAttack.create(40)),
            Pair.of(5, PredatorBehaviors.stopAttackingIfTooFarFromHome())
        ),
            ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)),
            ImmutableSet.of(MemoryModuleType.ATTACK_TARGET));
    }

}
