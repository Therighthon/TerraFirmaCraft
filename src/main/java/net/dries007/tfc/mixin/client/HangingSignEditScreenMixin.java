/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.client.render.blockentity.TFCHangingSignBlockEntityRenderer;
import net.dries007.tfc.common.blocks.wood.TFCCeilingHangingSignBlock;
import net.dries007.tfc.common.blocks.wood.TFCWallHangingSignBlock;

@Mixin(HangingSignEditScreen.class)
public abstract class HangingSignEditScreenMixin
{
    @Mutable
    @Final
    @Shadow
    private ResourceLocation texture;

    @Inject(method = "<init>(Lnet/minecraft/world/level/block/entity/SignBlockEntity;ZZ)V", at = @At("TAIL"))
    public void inject$constructor(SignBlockEntity signBlockEntity, boolean isFrontText, boolean filter, CallbackInfo ci)
    {
        final SignBlock block = (SignBlock) signBlockEntity.getBlockState().getBlock();
        if (block instanceof TFCCeilingHangingSignBlock || block instanceof TFCWallHangingSignBlock)
        {
            this.texture = TFCHangingSignBlockEntityRenderer.RESOURCE_LOCATION.apply(block);
        }
    }
}
