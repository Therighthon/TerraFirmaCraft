/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;

import net.dries007.tfc.common.component.food.FoodCapability;

public enum NotRottenIngredient implements PreciseIngredient
{
    INSTANCE;

    public static final MapCodec<NotRottenIngredient> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, NotRottenIngredient> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public boolean test(ItemStack stack)
    {
        return !FoodCapability.isRotten(stack);
    }

    @Override
    public ItemStack modifyStackForDisplay(ItemStack stack)
    {
        return FoodCapability.setInvisibleNonDecaying(stack);
    }

    @Override
    public IngredientType<?> getType()
    {
        return TFCIngredients.NOT_ROTTEN.get();
    }
}
