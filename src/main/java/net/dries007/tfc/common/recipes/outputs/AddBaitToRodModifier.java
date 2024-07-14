/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.BaitComponent;
import net.dries007.tfc.common.component.BaitType;
import net.dries007.tfc.common.recipes.RecipeHelpers;

public enum AddBaitToRodModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack primaryInput)
    {
        for (ItemStack input : RecipeHelpers.getCraftingInput())
        {
            if (BaitType.getType(input) != BaitType.NONE)
            {
                BaitComponent.setBait(stack, input);
                return stack;
            }
        }
        return stack;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.ADD_BAIT_TO_ROD.get();
    }
}
