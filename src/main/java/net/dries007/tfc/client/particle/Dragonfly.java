/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public enum Dragonfly
{
    ALPINE_EMERALD(-5f, 6f, 180f, 400f),
    AZURE_HAWKER(-4f, 8f,300f, 500f),
    BANDED_GROUNDLING(-5f, 6f, 20f, 200f),
    COMMON_FLANGETAIL(20f, 35f, 120f, 410f),
    EBONY_JEWELWING(6f, 24f, 150f, 400f),
    EPAULET_SKIMMER(6f, 30f, 0f, 150f),
    GLOBE_WANDERER(18f, 32f, 200f, 500f),
    GREEN_DARNER(4f, 18f, 100f, 500f),
    KIRBY_DROPWING(3f, 20f, 0f, 170f),
    PINCERTAIL(15f, 25f, 150f, 400f),
    SCARLET_DARTER(12f, 22f, 220f, 500f),
    SIBERIAN_WINTERDAMSEL(0f, 10f, 100f, 370f),
    VAGRANT_EMPEROR(10f, 20f, 210f, 500f),
    VARIEGATED_FLUTTERER(22f, 34f, 200f, 500f),
    WHITE_FACED_DARTER(-2f, 12f, 50f, 250f);

    public static final Dragonfly[] VALUES = Dragonfly.values();

    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;

    Dragonfly(float minTemp, float maxTemp, float minRain, float maxRain)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
    }

    @Nullable
    public static Dragonfly getRandomDragonfly(float temp, float rain, RandomSource random)
    {
        final Dragonfly fly = VALUES[random.nextInt(VALUES.length)];
        if (fly.minTemp < temp && temp < fly.maxTemp && fly.minRain < rain && rain < fly.maxRain)
        {
            return fly;
        }
        return null;
    }
}
