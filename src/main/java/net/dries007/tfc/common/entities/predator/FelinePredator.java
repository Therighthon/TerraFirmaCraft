/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.predator;


import com.mojang.serialization.Dynamic;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.entities.ai.TFCClimberNavigation;
import net.dries007.tfc.common.entities.ai.predator.FelinePredatorAi;

public class FelinePredator extends Predator
{
    //TODO: Incorporate these into behavior properly or remove
    private final float crouchSpeed;
    private final float sprintSpeed;
    private final int pounceDistanceSquared;

    public static final EntityDataAccessor<Boolean> DATA_CLIMBING = SynchedEntityData.defineId(FelinePredator.class, EntityDataSerializers.BOOLEAN);

    public static FelinePredator createCougar(EntityType<? extends Predator> type, Level level)
    {
        return new FelinePredator(type, level, false, 36, 0.52f, 1.15f, TFCSounds.COUGAR);
    }

    public static FelinePredator createLion(EntityType<? extends Predator> type, Level level)
    {
        return new FelinePredator(type, level, false, 36, 0.52f, 1.15f, TFCSounds.LION);
    }

    public static FelinePredator createSabertooth(EntityType<? extends Predator> type, Level level)
    {
        return new FelinePredator(type, level, false, 36, 0.52f, 1.15f, TFCSounds.SABERTOOTH);
    }

    public static FelinePredator createTiger(EntityType<? extends Predator> type, Level level)
    {
        return new FelinePredator(type, level, false, 36, 0.52f, 1.15f, TFCSounds.TIGER);
    }

    public FelinePredator(EntityType<? extends Predator> type, Level level, boolean diurnal, int attackDistanceSquared, float crouchSpeed, float sprintSpeed, TFCSounds.EntitySound sounds)
    {
        super(type, level, diurnal, sounds);

        this.crouchSpeed = crouchSpeed;
        this.sprintSpeed = sprintSpeed;
        this.pounceDistanceSquared = attackDistanceSquared;
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(DATA_CLIMBING, false);
    }

    @Override
    protected Brain.Provider<? extends Predator> brainProvider()
    {
        return Brain.provider(FelinePredatorAi.MEMORY_TYPES, FelinePredatorAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return FelinePredatorAi.makeBrain(brainProvider().makeBrain(dynamic), this);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.ATTACK_DAMAGE, 7).add(Attributes.KNOCKBACK_RESISTANCE, 0.25);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!level().isClientSide)
        {
            setClimbing(horizontalCollision);
        }
    }

    @Override
    public void tickAnimationStates()
    {
        if (isSleeping())
        {
            if (getRandom().nextInt(10) == 0)
            {
                level().addParticle(TFCParticles.SLEEP.get(), getX(), getY() + getEyeHeight(), getZ(), 0.01, 0.05, 0.01);
            }
            sleepingAnimation.startIfStopped(tickCount);
        }
        else
        {
            sleepingAnimation.stop();
        }
    }



    @Override
    protected PathNavigation createNavigation(Level level)
    {
        return new TFCClimberNavigation(this, level);
    }

    public void setClimbing(boolean climbing)
    {
        entityData.set(DATA_CLIMBING, climbing);
    }

    public boolean isClimbing()
    {
        return entityData.get(DATA_CLIMBING);
    }

    @Override
    public boolean onClimbable()
    {
        return isClimbing();
    }

    public int getPounceDistanceSquared()
    {
        return this.pounceDistanceSquared;
    }

    public float getCrouchSpeed()
    {
        return this.crouchSpeed;
    }

    public float getSprintSpeed()
    {
        return this.sprintSpeed;
    }
}