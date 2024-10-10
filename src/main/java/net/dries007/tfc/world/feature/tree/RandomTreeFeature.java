/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import net.dries007.tfc.mixin.accessor.StructureTemplateAccessor;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class RandomTreeFeature extends Feature<RandomTreeConfig>
{
    public RandomTreeFeature(Codec<RandomTreeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RandomTreeConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final RandomSource random = context.random();
        final RandomTreeConfig config = context.config();

        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos);
        final StructureTemplateManager manager = TreeHelpers.getStructureManager(level);
        final StructurePlaceSettings settings = TreeHelpers.getPlacementSettingsNoTransforms(level, chunkPos, random);
        final ResourceLocation structureId = config.structureNames().get(random.nextInt(config.structureNames().size()));
        final StructureTemplate structure = manager.getOrCreate(structureId);
        if (((StructureTemplateAccessor) structure).accessor$getPalettes().isEmpty())
        {
            throw new IllegalStateException("Empty structure: " + structureId);
        }

        if (TreeHelpers.isValidLocation(level, pos, settings, config.placement()))
        {
            final boolean placeTree = config.rootSystem().map(roots -> TreeHelpers.placeRoots(level, pos.below(), roots, random) || !roots.required()).orElse(true);
            if (placeTree)
            {
                config.trunk().ifPresent(trunk -> {
                    final int height = TreeHelpers.placeTrunk(level, mutablePos, random, settings, trunk);
                    mutablePos.move(0, height, 0);
                });
                TreeHelpers.placeTemplate(structure, settings.setRotation(getRiparianRotation(pos.mutable(), level.getLevel())), level, mutablePos.subtract(TreeHelpers.transformCenter(structure.getSize(), settings)));
                return true;
            }
            return false;
        }
        return false;
    }

    private Rotation getRiparianRotation(BlockPos.MutableBlockPos pos, Level level)
    {
        final float water = ChunkData.get(level, pos).getGroundwater(pos);
        pos.move(0, 0, 1);
        final float southDelta = ChunkData.get(level, pos).getGroundwater(pos) - water;
        pos.move(1, 0, -1);
        final float eastDelta = ChunkData.get(level, pos).getGroundwater(pos) - water;

        if (Math.abs(southDelta) > Math.abs(eastDelta))
        {
            return southDelta > 0 ? Rotation.CLOCKWISE_180 : Rotation.NONE;
        }
        else
        {
            return eastDelta > 0 ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
        }
    }
}