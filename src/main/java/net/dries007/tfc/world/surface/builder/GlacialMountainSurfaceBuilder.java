/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.minecraft.util.Mth;

import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

public class GlacialMountainSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = GlacialMountainSurfaceBuilder::new;

    private final Noise2D variantNoise;

    protected GlacialMountainSurfaceBuilder(long seed)
    {
        // TODO: Fix up and remove redundancy
        final double spread = 0.003;
        final Noise2D glaciers = new OpenSimplex2D(seed).spread(spread).map(Math::abs).scaled(0, 2);
        final Noise2D surface = BiomeNoise.glacialMountainsSurface(seed, spread, glaciers);
        this.variantNoise = glaciers.add(surface.map(y -> -y));
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        // Adjust slope, shores should have high relief carving where they intersect with the landmass
        final double slope = context.getSlope();
        context.setSlope(slope * (slope + 0.2));

        final float variantNoiseValue = (float) variantNoise.noise(context.pos().getX(), context.pos().getZ());
        if (variantNoiseValue > 0.0f)
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.GLACIER, SurfaceStates.GLACIER, SurfaceStates.GLACIER);
        }
        else
        {
            MountainSurfaceBuilder.INSTANCE.apply(context.getSeed());
        }
    }
}