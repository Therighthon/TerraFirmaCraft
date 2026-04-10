/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import it.unimi.dsi.fastutil.HashCommon;

/**
 * Work in this file is modified from the work of Rune Johansen, unless otherwise noted.
 *
 * Advanced Terrain Erosion Filter copyright (c) 2025 Rune Skovbo Johansen
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * https://blog.runevision.com/2026/03/fast-and-gorgeous-erosion-filter.html
 * https://www.shadertoy.com/view/wXcfWn
 */
public final class ErosionNoise
{
    // TODO: Input noise should be scaled -1 to 1, I think. Confirm and write a comment
    public static Noise2D mountainErosion(long seed, Noise2D noiseIn)
    {
        return (x, y) ->
        {
            // Scaling down inputs is better for working with cellular noise
            final FastNoiseLite.Vector2 p = new FastNoiseLite.Vector2(x, y);
            final double heightIn = noiseIn.noise(x, y);
            // We get slopes by sampling adjacent points, not from a derivative
            final FastNoiseLite.Vector3 heightAndSlope = new FastNoiseLite.Vector3(heightIn, 0.5 * (noiseIn.noise(x + 1, y) - noiseIn.noise(x - 1, y)), 0.5 * (noiseIn.noise(x, y + 1) - noiseIn.noise(x, y - 1)));
            final double fadeTarget = Math.clamp(heightIn / 0.6, -1, 1);
            // The strength of the erosion effect, affecting the magnitude of all octaves,
            // and indirectly affecting the directions of the gullies as a result.
            final double erosionStrength = 0.22;
            // The magnitude of the gullies as a weight value from 0 to 1.
            // A value of 0 can sharpen peaks and valleys but feature virtually no gullies.
            // A value of 1 produces full gullies but may leave peaks and valleys rounded.
            // Adjusting erosion gully weight while inversely adjusting erosion scale can be
            // used to control the sharpness of peaks and valleys while leaving gully
            // magnitudes largely untocuhed.
            final double erosionGullyWeight = 0.5;
            final double erosionDetail = 1.5;
            // Separate rounding control of ridges and creases.
            //  x: Rounding of ridges.
            //  y: Rounding of creases.
            //  z: Multiplier applied to the initial height function.
            //     E.g. if the height function has noise of 5 times lower frequency
            //     than the largest gullies, a value of 0.2 can compensate for that.
            //  w: Multiplier applied to each subsequent gully octave after the first.
            //     Setting it to the same value as the erosion lacunarity will produce
            //     consistent rounding of all octaves.
            final FastNoiseLite.Vector4 erosionRounding = new FastNoiseLite.Vector4(0.1, 0.0, 0.1, 2.0);
            // Control over how far away from ridges/creases the erosion takes effect.
            //  x: Onset used on the initial height function.
            //  y: Onset used on each gully octave.
            //  z: RidgeMap-specific onset used on the initial height function.
            //  w: RidgeMap-specific onset used on each gully octave.
            final FastNoiseLite.Vector4 erosionOnset = new FastNoiseLite.Vector4(1.25, 1.25, 2.8, 1.5);
            // Control over the assumed slope of the initial height function.
            // In practise, assuming a slope can work better than using the input slope,
            // since the final terrain can be shaped quite differently than the input.
            //  x: An assumed slope value to override the actual slope.
            //  y: The amount (from 0 to 1) to override the actual slope.
            final FastNoiseLite.Vector2 erosionAssumedSlope = new FastNoiseLite.Vector2(0.7, 1.0);
            // The scale of the erosion effect, affecting it both horizontally and vertically.
            final double erosionScale = 60;
            // Control over the erosion octaves, with each successive octave layering
            // smaller gullies onto the terrain.
            final int octaves = 3;
            // The lacunarity controls the frequency (the inverse
            // horizontal scale) of each octave relative to the last.
            final double lacunarity = 2.0;
            // The gain controls the magnitude (the vertical
            // scale) of each octave relative to the last.
            final double gain = 0.5;
            // Gullies are based on stripes within Voronoi-like cells in the Phacelle noise
            // function. The cell scale parameter controls the sizes of the cells relative
            // to the overall erosion scale, while keeping the stripe widths unaffected.
            // Values close to 1 usually produce good results. Smaller values produce more
            // grainy gullies while larger values produce longer unbroken gullies, but too
            // large values produce chaotic curved gullies that are not aligned with the
            // slopes. Value changes can cause abrupt changes in output, especially far away
            // from the origin, so this parameter is not well suited for animation or for
            // modulation by other functions.
            final double cellScale = 0.7;
            // The degree of normalization applied in the Phacelle noise, between 0 and 1.
            // The erosion filter depends on a certain consistency in magnitude of the
            // Phacelle output. However, high values can create loopy results where ridges
            // and creases meet up at a point, which produces unnatural looking results.
            final double normalization = 0.5;

            // TODO: Return directly
            final double erodedShape = erosionFilter(p, heightAndSlope, fadeTarget, erosionStrength, erosionGullyWeight, erosionDetail, erosionRounding, erosionOnset, erosionAssumedSlope, erosionScale, octaves, lacunarity, gain, cellScale, normalization, seed);
            return erodedShape;
        };
    }

    public static double erosionFilter(FastNoiseLite.Vector2 p, FastNoiseLite.Vector3 heightAndSlopeIn, double fadeTarget,
                                        double strength, double gullyWeight, double detail, FastNoiseLite.Vector4 rounding,
                                        FastNoiseLite.Vector4 onset, FastNoiseLite.Vector2 assumedSlope, double scale, int octaves,
                                        double lacunarity, double gain, double cellScale, double normalization, long seed)
    {
        strength *= scale;
        fadeTarget = Math.clamp(fadeTarget, -1, 1);

        FastNoiseLite.Vector3 heightAndSlope = heightAndSlopeIn;

        double freq = 1.0 / (scale * cellScale);
        double slopeLength = Math.max(heightAndSlopeIn.yz().mag(), 0.0);
        double roundingMult = 1.0;

        double roundingForInput = mix1d(rounding.y, rounding.x, Math.clamp(fadeTarget + 0.5, 0, 1)) * rounding.z;
        double combiMask = easeOut(smoothStart(slopeLength * onset.x, roundingForInput * onset.x));

        double ridgeMapCombiMask = easeOut(slopeLength * onset.z);
        double ridgeMapFadeTarget = fadeTarget;

        FastNoiseLite.Vector2 gullySlope = mix2d(heightAndSlopeIn.yz(), heightAndSlopeIn.yz().scale(1 / slopeLength * assumedSlope.x), assumedSlope.y);

        for (int i = 0; i < octaves; i++)
        {
            // Calculate and add gullies to the height and slope.
            FastNoiseLite.Vector4 phacelle = phacelleNoise(p.scale(freq), safeNormalize(gullySlope), cellScale, 0.25, normalization, seed);

            // Multiply with freq since p was multiplied with freq.
            // Negate since we use slope directions that point down.
            phacelle.z *= -freq;
            phacelle.w *= -freq;

            // Amount of slope as value from 0 to 1.
            double sloping = Math.abs(phacelle.y);

            // Add non-masked, normalized slope to gullySlope, for use by subsequent octaves.
            // It's normalized to use the steepest part of the sine wave everywhere.
            gullySlope = gullySlope.plus(phacelle.zw().scale(Math.signum(phacelle.y) * strength * gullyWeight));

            // Handle height offset and approximate output slope.

            // Gullies has height offset (from -1 to 1) in x and derivative in yz.
            FastNoiseLite.Vector3 gullies = new FastNoiseLite.Vector3(phacelle.x, phacelle.zw().scale(phacelle.y));

            // Fade gullies towards fadeTarget based on combiMask.
            FastNoiseLite.Vector3 fadedGullies = mix3d(new FastNoiseLite.Vector3(fadeTarget, 0, 0), gullies.scale(gullyWeight), combiMask);

            // Apply height offset and derivative (slope) according to strength of current octave.
            heightAndSlope.plusEquals(fadedGullies.scale(strength));
            double seven = heightAndSlope.z;

            // Update fadeTarget to include the new octave.
            fadeTarget = fadedGullies.x;

            // Update the mask to include the new octave.
            double roundingForOctave = mix1d(rounding.y, rounding.x, Math.clamp(phacelle.x + 0.5, 0, 1)) * roundingMult;
            double newMask = easeOut(smoothStart(sloping * onset.y, roundingForOctave * onset.y));
            combiMask = powInv(combiMask, detail) * newMask;

            // Update the ridgeMap fadeTarget and mask.
            ridgeMapFadeTarget = mix1d(ridgeMapFadeTarget, gullies.x, ridgeMapCombiMask);
            double newRidgeMapMask = easeOut(sloping * onset.w);
            ridgeMapCombiMask = ridgeMapCombiMask * newRidgeMapMask;

            // Prepare the next octave.
            strength *= gain;
            freq *= lacunarity;
            roundingMult *= rounding.w;
        }

        return heightAndSlope.x;
    }


    public static FastNoiseLite.Vector4 phacelleNoise(FastNoiseLite.Vector2 p, FastNoiseLite.Vector2 normDir, double freq, double offset, double normalization, long seed)
    {
        final FastNoiseLite.Vector2 sideDir = new FastNoiseLite.Vector2(-normDir.y, normDir.x).scale(freq * 2 * Math.PI);

        final FastNoiseLite.Vector2 pInt = new FastNoiseLite.Vector2(Math.floor(p.x), Math.floor(p.y));
        final FastNoiseLite.Vector2 pFract = p.minus(pInt);
        FastNoiseLite.Vector2 phaseDir = new FastNoiseLite.Vector2(0, 0);
        double weightSum = 0.0;

        for (int i = -1; i <= 2; i++)
        {
            for (int j = -1; j <= 2; j++)
            {

                final FastNoiseLite.Vector2 gridOffset = new FastNoiseLite.Vector2(i, j);
                final FastNoiseLite.Vector2 gridPoint = pInt.plus(gridOffset);
                final FastNoiseLite.Vector2 randOffset = getRandOffsetFromCenter(seed, (int) gridPoint.x, (int) gridPoint.y);
                final FastNoiseLite.Vector2 vectorFromCellPoint = pFract.minus(gridOffset).minus(randOffset);
                final double sqrDist = vectorFromCellPoint.mag2();
                double weight = Math.exp(-sqrDist * 2.0);
                weight = Math.max(0.0, weight - 0.01111);
                weightSum += weight;
                double waveInput = vectorFromCellPoint.dot(sideDir) + offset;
                final FastNoiseLite.Vector2 partialPhaseDir = new FastNoiseLite.Vector2(Math.cos(waveInput), Math.sin(waveInput));
                phaseDir.plus(partialPhaseDir.scale(weight));
            }
        }

        final FastNoiseLite.Vector2 interpolated = phaseDir.scale(1 / weightSum);
        double magnitude = interpolated.mag();
        magnitude = Math.max(1 - normalization, magnitude);
        return new FastNoiseLite.Vector4(interpolated.scale(1 / magnitude), sideDir);
    }

    // Flip, raise to the specified power, and flip back.
    public static double powInv(double t, double power)
    {
        return 1.0 - Math.pow(1.0 - Math.clamp(t, 0, 1), power);
    }

    public static double easeOut(double t) {
        // Flip by subtracting from one.
        double v = 1.0 - Math.clamp(t, 0, 1);
        // Raise to a power of two and flip back.
        return 1.0 - v * v;
    }

    public static double smoothStart(double t, double smoothing) {
        if (t >= smoothing)
            return t - 0.5 * smoothing;
        return 0.5 * t * t / smoothing;
    }

    public static FastNoiseLite.Vector2 safeNormalize(FastNoiseLite.Vector2 n) {
        // A div-by-zero-safe replacement for normalize.
        double l = n.mag();
        return (Math.abs(l) > 1e-10) ? (n.scale(1 / l)) : n;
    }

    /**
     * Mirrors cell noise behavior above for getting cell centers from grid coordinates only
     * Modified from {@link FastNoiseLite#SingleCellular(int, double, double)}
     */
    public static FastNoiseLite.Vector2 getRandOffsetFromCenter(long seed, int cx, int cy)
    {
        final int primeX = 501125321;
        final int primeY = 1136930381;
        final int hash = FastNoiseLite.Hash(HashCommon.long2int(seed), cx * primeX, cy * primeY);
        final int idx = hash & (255 << 1);
        return new FastNoiseLite.Vector2(FastNoiseLite.RandVecs2D[idx] * 0.4, FastNoiseLite.RandVecs2D[idx | 1] * 0.4);
    }

    // Vector functions needed to run this in Java

    // Linear interpolation between two vectors, u to v, from 0 to 1
    public static double mix1d(double u, double v, double t)
    {
        return (u * (1 - t)) + (v * t);
    }

    public static FastNoiseLite.Vector2 mix2d(FastNoiseLite.Vector2 u, FastNoiseLite.Vector2 v, double t)
    {
        return (u.scale(1 - t)).plus(v.scale(t));
    }

    public static FastNoiseLite.Vector3 mix3d(FastNoiseLite.Vector3 u, FastNoiseLite.Vector3 v, double t)
    {
        return (u.scale(1 - t)).plus(v.scale(t));
    }

}
