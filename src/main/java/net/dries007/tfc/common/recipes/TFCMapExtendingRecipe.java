package net.dries007.tfc.common.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.MapExtendingRecipe;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.items.TFCItems;

public class TFCMapExtendingRecipe extends MapExtendingRecipe
{
    //TODO: Does not even remotely work, probably something registered wrong
    public TFCMapExtendingRecipe(CraftingBookCategory category)
    {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int i = 0;
        ItemStack itemstack = ItemStack.EMPTY;

        for(int j = 0; j < input.size(); ++j) {
            ItemStack itemstack1 = input.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(TFCItems.FILLED_MAP.get())) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (!itemstack1.is(TFCItems.MAP.get())) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !itemstack.isEmpty() && i > 0;
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int i = 0;
        ItemStack itemstack = ItemStack.EMPTY;

        for(int j = 0; j < input.size(); ++j) {
            ItemStack itemstack1 = input.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(TFCItems.FILLED_MAP.get())) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    itemstack = itemstack1;
                } else {
                    if (!itemstack1.is(TFCItems.MAP.get())) {
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        return !itemstack.isEmpty() && i >= 1 ? itemstack.copyWithCount(i + 1) : ItemStack.EMPTY;
    }
}
