/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.predator.FelinePredator;
import net.dries007.tfc.common.entities.predator.Predator;

public class FelinePredatorModel<E extends FelinePredator> extends HierarchicalAnimatedModel<E>
{
    private final AnimationDefinition sleep;
    private final AnimationDefinition walk;
    private final AnimationDefinition run;
    private final AnimationDefinition sneak;
    private final AnimationDefinition attack;

    public FelinePredatorModel(ModelPart root, AnimationDefinition sleep, AnimationDefinition walk, AnimationDefinition run, AnimationDefinition attack, AnimationDefinition sneak)
    {
        super(root);
        this.sleep = sleep;
        this.walk = walk;
        this.run = run;
        this.attack = attack;
        this.sneak = sneak;
    }

    @Override
    public void setupAnim(E predator, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(predator, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (predator.sleepingAnimation.isStarted())
        {
            setupSleeping();
            this.animate(predator.sleepingAnimation, sleep, ageInTicks);
        }
        else
        {
            // swimming is animated as walking. animations can be swapped with no consequences!
            if (predator.isInWaterOrBubble() || !predator.isAggressive() || !EntityHelpers.isMovingOnLand(predator))
            {
                this.animateWalk(walk, limbSwing, limbSwingAmount, 2.5f, 2.5f);
            }
            else
            {
                final float speed = getAdjustedLandSpeed(predator);
                if (speed < 0.8f)
                {
                    this.animateWalk(sneak, limbSwing, limbSwingAmount, 1f, 2.5f);
                }
                else
                {
                    this.animateWalk(run, limbSwing, limbSwingAmount, 1f, 2.5f);
                }
            }
            this.animate(predator.attackingAnimation, attack, ageInTicks);
            setupHeadRotations(netHeadYaw, headPitch);
        }
    }

    public void setupHeadRotations(float yaw, float pitch)
    {

    }

    public void setupSleeping()
    {

    }
}
