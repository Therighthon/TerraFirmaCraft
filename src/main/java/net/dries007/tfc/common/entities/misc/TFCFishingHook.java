/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.items.TFCFishingRodItem;
import net.dries007.tfc.util.advancements.TFCAdvancements;

public class TFCFishingHook extends FishingHook implements IEntityWithComplexSpawn
{
    public static final EntityDataAccessor<ItemStack> BAIT = SynchedEntityData.defineId(TFCFishingHook.class, EntityDataSerializers.ITEM_STACK);

    public int lineStress = 0;
    private float strength = 0.04f;
    private long lastPulled = 0;
    private double lastDistance = 0;
    private float fishStamina = -1;

    public TFCFishingHook(EntityType<? extends TFCFishingHook> type, Level level)
    {
        super(type, level);
    }

    public TFCFishingHook(Player player, Level level, float strength, ItemStack bait)
    {
        this(TFCEntities.FISHING_BOBBER.get(), level);
        this.setOwner(player);
        float f = player.getXRot();
        float f1 = player.getYRot();
        float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
        double d0 = player.getX() - (double) f3 * 0.3D;
        double d1 = player.getEyeY();
        double d2 = player.getZ() - (double) f2 * 0.3D;
        this.moveTo(d0, d1, d2, f1, f);
        Vec3 vec3 = new Vec3(-f3, Mth.clamp(-(f5 / f4), -5.0F, 5.0F), -f2);
        double d3 = vec3.length();
        vec3 = vec3.multiply(0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D);
        this.setDeltaMovement(vec3);
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vec3.y, vec3.horizontalDistance()) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        // TFC
        this.strength = strength;
        setBait(bait);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(BAIT, ItemStack.EMPTY);
    }

    public void setBait(ItemStack item)
    {
        entityData.set(BAIT, item);
    }

    public ItemStack getBait()
    {
        return entityData.get(BAIT);
    }

    @Override
    public void tick()
    {
        super.tick();
        Player owner = this.getPlayerOwner();
        if (hookedIn != null && !hookedIn.isRemoved()) // due to some path-dependent client/server stuff in the big tick loop, we do this to be 100% sure the state is set correctly
        {
            if (fishStamina == -1)
            {
                fishStamina = 20 * getEntitySize(hookedIn);
            }

            currentState = FishHookState.HOOKED_IN_ENTITY;
            setPos(hookedIn.getX(), hookedIn.getY(0.8D), hookedIn.getZ());

            if (owner != null && hookedIn instanceof LivingEntity)
            {
                double distance = distanceToSqr(owner);

                TerraFirmaCraft.LOGGER.info("Ticking!");

                // Slow fish down if it's tired
                if (fishStamina < 1)
                {
                    ((LivingEntity) hookedIn).setSpeed(0.1f);
                    TerraFirmaCraft.LOGGER.info("Tired fish!");
                }
                // If fish swims away, pulling on the line, drains the fish's stamina
                if (distance > lastDistance)
                {
                    fishStamina = fishStamina--;
                    // TODO: this doesn't work because the fish stamina isn't actually saved, need to add an nbt property to make this work,
                    //  or just directly reduce the fish's speed property slightly every time
                    TerraFirmaCraft.LOGGER.info(String.valueOf(fishStamina));
                }

                //TODO: Improve
                // Kill the fish if it is near the hook's owner
                if (distance < 1 && hookedIn instanceof LivingEntity && ((LivingEntity) hookedIn).getHealth() < 6f)
                {
                    (hookedIn).hurt(owner.damageSources().playerAttack(owner), 10f);
                }

                lastDistance = distance;
            }
        }


        // gradually reduce exhaustion
        if (lineStress > 0)
        {
            lineStress--;
        }
    }

    @Override
    public void catchingFish(BlockPos pos) {} // no-op fishing

    /**
     * This is a little different from the vanilla method in that we call it on both sides. Vanilla mistakenly does not.
     */
    @Override
    public int retrieve(ItemStack stack)
    {
        Player player = getPlayerOwner();
        long diff = level().getGameTime() - lastPulled;
        if (diff < 25)
        {
            lineStress += (25 - diff) * 2;
            if (lineStress > 100)
            {
                if (player != null && level().isClientSide)
                {
                    player.displayClientMessage(Component.translatable("tfc.fishing.pulled_too_hard"), true);
                }
                eatBait();
                playSound(SoundEvents.ITEM_BREAK, 1f, 0.5f + random.nextFloat());
                discard();
                return 1;
            }
            else
            {
                // warning sound
                playSound(SoundEvents.FISHING_BOBBER_THROW, 1f, 0.5f + random.nextFloat());
            }
        }
        lastPulled = level().getGameTime();
        if (!level().isClientSide && player != null && !shouldStopFishing(player))
        {
            if (hookedIn != null)
            {
                pullEntity(hookedIn);
                if (hookedIn instanceof AbstractFish)
                {
                    player.awardStat(Stats.FISH_CAUGHT, 1);
                }
                TFCAdvancements.HOOKED_ENTITY.trigger((ServerPlayer) player, hookedIn);
                level().broadcastEntityEvent(this, (byte) 31);
            }
            if (hookedIn == null || hookedIn.isRemoved())
            {
                discard(); // change from vanilla -- lets you keep tugging on the thing while it's alive.
            }
            return onGround() ? 2 : 1;
        }
        return 0;
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer)
    {
        Entity owner = getOwner();
        buffer.writeVarInt(owner == null ? this.getId() : owner.getId());
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData)
    {
        int id = additionalData.readVarInt();
        Entity entity = this.level().getEntity(id);
        if (entity != null)
        {
            this.setOwner(entity);
        }
        if (getPlayerOwner() == null)
        {
            TerraFirmaCraft.LOGGER.error("Failed to recreate fishing hook on client. {} (id: {}) is not a valid owner.", this.level().getEntity(id), id);
            kill();
        }
    }

    @Override
    protected void pullEntity(Entity entity)
    {
        Entity owner = this.getOwner();

        if (owner != null)
        {
            // The reeled entity is considered larger if it is in water
            final float fishSize = getEntitySize(entity) * (entity.isInFluidType() ? 1.6f : 1f);

            // TODO: Increase player weight for higher tier rods?
            Entity ownerVehicle = owner.getVehicle();

            double dx = owner.getX() - this.getX();
            double dy = owner.getY() - this.getY();
            double dz = owner.getZ() - this.getZ();
            if (dy > 0 && dx < 3 && dz < 3)
            {
                dy = 3; // helps you pull the thing onto shore.
            }
            entity.setDeltaMovement(new Vec3(dx / fishSize, dy / fishSize, dz / fishSize).normalize().scale(strength));
            if (ownerVehicle != null)
            {
                final float ownerSize = getEntitySize(ownerVehicle) + 2f;
                ownerVehicle.setDeltaMovement(new Vec3(-dx / ownerSize, -dy / ownerSize, -dz / ownerSize).normalize().scale(strength));
            }
            else
            {
                final float ownerSize = 2f;
                owner.addDeltaMovement(new Vec3(-dx / ownerSize, -dy / ownerSize, -dz / ownerSize).normalize().scale(strength));
            }
        }
    }

    private float getEntitySize(Entity entity)
    {
        final float height = entity.getBbHeight();
        final float width = entity.getBbHeight();
        return height * width * width;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putLong("lastPulled", lastPulled);
        tag.putInt("exhaustion", lineStress);
        tag.putFloat("strength", strength);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        lastPulled = tag.getLong("lastPulled");
        lineStress = tag.getInt("exhaustion");
        strength = tag.getFloat("strength");
    }

    public void eatBait()
    {
        Player owner = getPlayerOwner();
        if (owner != null)
        {
            final ItemStack main = owner.getMainHandItem();
            final ItemStack off = owner.getOffhandItem();
            ItemStack use = ItemStack.EMPTY;
            if (main.getItem() instanceof TFCFishingRodItem)
            {
                use = main;
            }
            else if (off.getItem() instanceof TFCFishingRodItem)
            {
                use = off;
            }
            if (!use.isEmpty())
            {
                use.remove(TFCComponents.BAIT);
            }
            playSound(SoundEvents.GENERIC_EAT, 1f, 1f);
        }
    }
}
