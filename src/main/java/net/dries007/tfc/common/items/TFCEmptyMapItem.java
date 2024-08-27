package net.dries007.tfc.common.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EmptyMapItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TFCEmptyMapItem extends EmptyMapItem
{
    public TFCEmptyMapItem(Properties properties)
    {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(itemstack);
        } else {
            itemstack.consume(1, player);
            player.awardStat(Stats.ITEM_USED.get(this));
            player.level().playSound((Player)null, player, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, player.getSoundSource(), 1.0F, 1.0F);
            ItemStack itemstack1 = TFCMapItem.create(level, player.getBlockX(), player.getBlockZ(), (byte)0, true, false);
            if (itemstack.isEmpty()) {
                return InteractionResultHolder.consume(itemstack1);
            } else {
                if (!player.getInventory().add(itemstack1.copy())) {
                    player.drop(itemstack1, false);
                }

                return InteractionResultHolder.consume(itemstack);
            }
        }
    }
}
