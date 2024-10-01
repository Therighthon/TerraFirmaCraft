/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.aquatic.Fish;

import static net.dries007.tfc.common.TFCTags.Entities.*;

public class BuiltinEntityTags extends EntityTypeTagsProvider
{
    public BuiltinEntityTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> provider)
    {
        super(event.getGenerator().getPackOutput(), provider, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        // ===== Vanilla Tags ===== //

        // ===== TFC Tags ===== //

        tag(MONSTERS)
            .addTag(EntityTypeTags.UNDEAD)
            .add(
                EntityType.CREEPER,
                EntityType.SPIDER,
                EntityType.WITCH,
                EntityType.SLIME
            );

        tag(SMALL_FISH)
            .add(
                TFCEntities.FRESHWATER_FISH.get(Fish.BLUEGILL).key(),
                TFCEntities.FRESHWATER_FISH.get(Fish.CRAPPIE).key(),
                TFCEntities.FRESHWATER_FISH.get(Fish.LAKE_TROUT).key(),
                TFCEntities.FRESHWATER_FISH.get(Fish.LARGEMOUTH_BASS).key(),
                TFCEntities.FRESHWATER_FISH.get(Fish.RAINBOW_TROUT).key(),
                TFCEntities.FRESHWATER_FISH.get(Fish.SMALLMOUTH_BASS).key(),
                TFCEntities.TROPICAL_FISH.key(),
                TFCEntities.PUFFERFISH.key()
            );

        tag(LARGE_FISH)
            .add(
                TFCEntities.FRESHWATER_FISH.get(Fish.SALMON).key(),
                TFCEntities.COD.key()
            );

        tag(HUGE_FISH)
            .add(
                TFCEntities.ORCA.key(),
                TFCEntities.DOLPHIN.key()
            );

        tag(NEEDS_NO_FISHING_BAIT)
            .addTag(SMALL_FISH);
        tag(NEEDS_SMALL_FISHING_BAIT)
            .addTags(LARGE_FISH);
        tag(NEEDS_LARGE_FISHING_BAIT)
            .addTags(HUGE_FISH);
    }
}
