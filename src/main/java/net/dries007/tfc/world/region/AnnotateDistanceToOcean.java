/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.BitSet;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import org.jetbrains.annotations.Nullable;

public enum AnnotateDistanceToOcean implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final BitSet explored = new BitSet(region.size());
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();
        final IntArrayFIFOQueue riverStartQueue = new IntArrayFIFOQueue();

        // Add all ocean points to a queue
        for (final var point : region.points())
        {
            if (point != null && !point.land())
            {
                point.distanceToOcean = -1;
                queue.enqueue(point.index);
                explored.set(point.index);
            }
        }

        // For each point in the queue...
        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            final Region.Point lastPoint = region.atIndex(last);
            final int nextDistance = lastPoint.distanceToOcean + 1;

            // Get the 8 points around it...
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    final @Nullable Region.Point point = region.atOffset(last, dx, dz);
                    // And if any of these surrounding points are not ocean then...
                    if (point != null && point.land() && point.distanceToOcean == 0)
                    {
                        // Set them to shore if the enqueued point was an ocean
                        if (!lastPoint.land() && !point.island())
                        {
                            lastPoint.setShore(); // Mark as adjacent to land
                            riverStartQueue.enqueue(lastPoint.index); // Add them to the river start queue for later
                        }

                        // And add them to the queue if they haven't been previously added to the queue
                        if (!explored.get(point.index))
                        {
                            point.distanceToOcean = (byte) nextDistance;
                            queue.enqueue(point.index);
                        }
                        explored.set(point.index);
                    }
                }
            }
        }

        // For each shore point...
        while (!riverStartQueue.isEmpty())
        {
            final int last = riverStartQueue.dequeueInt();

            // Get the 8 points around it...
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    final @Nullable Region.Point point = region.atOffset(last, dx, dz);
                    // And if any of these surrounding points are ocean then...
                    if (point != null && !point.land() && !point.shore())
                    {
                        // Set them to be valid river start locations
                        point.setAllowableRiverDrain();
                        // This second loop could be revised to get distance from land for ocean points,
                        // should the need arise
                    }
                }
            }
        }
    }
}
