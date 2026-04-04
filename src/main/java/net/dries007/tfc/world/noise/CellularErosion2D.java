/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import it.unimi.dsi.fastutil.HashCommon;

import net.dries007.tfc.util.Helpers;

/**
 * Modified from {@link FastNoiseLite#SingleCellular(int, double, double)}
 */
public class CellularErosion2D implements Noise2D
{
    private final double jitter;
    private final int seed;
    private double frequency;
    private Noise2D noiseIn;

    final int primeX = 501125321;
    final int primeY = 1136930381;

    public CellularErosion2D(long seed, Noise2D noiseIn)
    {
        this(seed, 0.43701595f, noiseIn);
    }


    public CellularErosion2D(long seed, float jitter, Noise2D noiseIn)
    {
        this.seed = HashCommon.long2int(seed);
        this.frequency = 1;
        this.jitter = jitter;
        this.noiseIn = noiseIn;
    }

    @Override
    public double noise(double x, double y)
    {
        return cell(x, y).noise();
    }

    @Override
    public CellularErosion2D spread(double scaleFactor)
    {
        frequency *= scaleFactor;
        return this;
    }

    public Noise2D then(ToDoubleFunction<Cell> f)
    {
        return (x, y) -> f.applyAsDouble(cell(x, y));
    }

    public Cell cell(double x, double y)
    {
        x *= frequency;
        y *= frequency;

        int xr = FastNoiseLite.FastFloor(x);
        int yr = FastNoiseLite.FastFloor(y);

        double distance0 = Double.MAX_VALUE;
        double distance1 = Double.MAX_VALUE;
        double angle0 = -1;
        double thisCenterX = 0;
        double thisCenterY = 0;
        double closestNeighborCenterX = 0;
        double closestNeighborCenterY = 0;
        int closestHash = 0;
        int noJitterCenterX = 0;
        int noJitterCenterY = 0;

        // First, we need to work out the basic cell noise, for which we use a sample radius of 2
        for (int xi = xr - 2; xi <= xr + 2; xi++)
        {
            for (int yi = yr - 2; yi <= yr + 2; yi++)
            {
                int hash = FastNoiseLite.Hash(seed, (xi * primeX), (yi * primeY));
                int idx = hash & (255 << 1);

                double vecX = xi + FastNoiseLite.RandVecs2D[idx] * jitter;
                double vecY = yi + FastNoiseLite.RandVecs2D[idx | 1] * jitter;

                double newDistanceX = vecX - x;
                double newDistanceY = vecY - y;
                double newAngle = Helpers.diamondAngle(newDistanceX, newDistanceY);
                double newDistance = newDistanceX * newDistanceX + newDistanceY * newDistanceY;

                final double oldDist1 = distance1;
                distance1 = FastNoiseLite.FastMax(FastNoiseLite.FastMin(distance1, newDistance), distance0);
                if (newDistance < distance0)
                {
                    // If the current distance is the smallest so far, update data
                    distance0 = newDistance;
                    angle0 = newAngle;
                    closestHash = hash;

                    // Store the last computed centers
                    closestNeighborCenterX = thisCenterX; // Cell 2 X
                    closestNeighborCenterY = thisCenterY; // Cell 2 Y
                    thisCenterX = vecX; // Cell 1 X
                    thisCenterY = vecY; // Cell 1 Y
                    noJitterCenterX = xi; // Cell 1 Grid X
                    noJitterCenterY = yi; // Cell 1 Grid Y
                }
                else if (distance1 != oldDist1)
                {
                    // If the distance to Cell 2 changed, without updating Cell 1, update data
                    closestNeighborCenterX = vecX;
                    closestNeighborCenterY = vecY;
                }
            }


        }

        // Now we do the drainage "network"

        // Each cell has one outlet (the center of a neighboring cell) and we get the outlet of all 8 neighboring cells
        // This ensures that we will account for all streams that could possibly influence this point
        final List<Valley> valleys = getValleys(noJitterCenterX, noJitterCenterY);

        return new Cell(thisCenterX / frequency, thisCenterY / frequency, noJitterCenterX, noJitterCenterY, closestNeighborCenterX / frequency, closestNeighborCenterY / frequency, FastNoiseLite.FastFloor(closestNeighborCenterX), FastNoiseLite.FastFloor(closestNeighborCenterY), distance0, distance1, closestHash * (1 / 2147483648.0f), angle0, valleys, frequency);
    }

    /**
     * @param x     "X"-coordinate of cell center
     * @param y     "Y"-coordinate of cell center
     * @param cx    "X"-coordinate of cell center before jitter, unscaled
     * @param cy    "Y"-coordinate of cell center before jitter, unscaled
     * @param nx    "X"-coordinate of neighboring cell center
     * @param ny    "Y"-coordinate of neighboring cell center
     * @param ncx   "X"-coordinate of neighboring cell center before jitter, unscaled
     * @param ncy   "Y"-coordinate of neighboring cell center before jitter, unscaled
     * @param f1    Distance to x, y
     * @param f2    Distance to cx, cy
     * @param noise Hash value of the cell, range 0-1
     * @param angle Diamond angle to the center
     * @param valleys        All inlet cell points of the cell
     * @param frequency     Scale factor, useful to have accessible
     */
    public record Cell(double x, double y, int cx, int cy, double nx, double ny, int ncx, int ncy, double f1, double f2, double noise, double angle, List<Valley> valleys, double frequency) {}


    public Point getOutlet(int xr, int yr)
    {
        double noise = Double.MAX_VALUE;
        double x = 0, y = 0;
        int cx = 0, cy = 0;
        for (int xi = xr - 1; xi <= xr + 1; xi++)
        {
            for (int yi = yr - 1; yi <= yr + 1; yi++)
            {
                int hash = FastNoiseLite.Hash(seed, (xi * primeX), (yi * primeY));
                int idx = hash & (255 << 1);
                double vecX = xi + FastNoiseLite.RandVecs2D[idx] * jitter;
                double vecY = yi + FastNoiseLite.RandVecs2D[idx | 1] * jitter;

                final double noiseAt = noiseIn.noise(vecX, vecY);
                if (noiseAt < noise)
                {
                    noise = noiseAt;
                    x = vecX;
                    y = vecY;
                    cx = xi;
                    cy = yi;
                }
            }
        }
        return new Point(x, y, cx, cy, noise);
    }

    public List<Valley> getValleys(int xr, int yr)
    {
        List<Valley> valleys = new ArrayList<>(9);
        for (int xi = xr - 1; xi <= xr + 1; xi++)
        {
            for (int yi = yr - 1; yi <= yr + 1; yi++)
            {

                int hash = FastNoiseLite.Hash(seed, (xi * primeX), (yi * primeY));
                int idx = hash & (255 << 1);
                double vecX = xi + FastNoiseLite.RandVecs2D[idx] * jitter;
                double vecY = yi + FastNoiseLite.RandVecs2D[idx | 1] * jitter;

                // Whatever point we are sampling is upstream, as we only search for its outlet
                final Point upstream = new Point(vecX, vecY, xi, yi, noiseIn.noise(vecX, vecY));
                final Point downstream = getOutlet(xr, yr);
                valleys.add(new Valley(upstream, downstream));
            }
        }
        return valleys;
    }

    /**
     * @param x "X"-coordinate of Point
     * @param y "Y"-coordinate of Point
     * @param cx "X"-coordinate of Point's Cell center before jitter, unscaled
     * @param cy "Y"-coordinate of Point's Cell center before jitter, unscaled
     * @param h Value of initial noise at the point
     */
    public record Point(double x, double y, int cx, int cy, double h) {}

    /**
     * Set of two points between which a valley should be carved out
     * @param u Point at one end of the valley
     * @param v Point at the other end of the valley
     */
    public record Valley(Point u, Point v) {}
}