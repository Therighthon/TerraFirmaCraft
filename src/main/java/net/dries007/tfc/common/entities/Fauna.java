/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.placement.ClimatePlacement;

/**
 * A data driven way to make spawning conditions for animals player configurable.
 */
public record Fauna(
    int chance,
    int distanceBelowSeaLevel,
    ClimatePlacement climate,
    boolean solidGround,
    int maxBrightness,
    List<Month> months
) {
    public static final Codec<Fauna> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.INT.optionalFieldOf("chance", 1).forGetter(c -> c.chance),
        Codec.INT.optionalFieldOf("distance_below_sea_level", -1).forGetter(c -> c.distanceBelowSeaLevel),
        ClimatePlacement.CODEC.forGetter(c -> c.climate),
        Codec.BOOL.optionalFieldOf("solid_ground", false).forGetter(c -> c.solidGround),
        Codec.INT.optionalFieldOf("max_brightness", -1).forGetter(c -> c.maxBrightness),
        Month.CODEC.listOf().optionalFieldOf("months", Collections.emptyList()).forGetter(c -> c.months)
    ).apply(i, Fauna::new));

    public static final DataManager<Fauna> MANAGER = new DataManager<>(Helpers.identifier("fauna"), CODEC);

    public static final class Builder
    {
        float minTemperature = Float.NEGATIVE_INFINITY, maxTemperature = Float.POSITIVE_INFINITY;
        float minRainfall = Float.NEGATIVE_INFINITY, maxRainfall = Float.POSITIVE_INFINITY;
        float minRainVariance = -1f, maxRainVariance = 1f;
        boolean rainVarianceAbsolute = false;
        int minForest = 0, maxForest = 4;
        List<ForestType> forests = new ArrayList<>();
        boolean fuzzy = false;
        int chance = 1;
        int distanceBelowSeaLevel = -1;
        boolean solidGround = false;
        int maxBrightness = -1;
        List<Month> months = new ArrayList<>();

        public Builder minTemperature(float min) { return temperature(min, Float.POSITIVE_INFINITY); }
        public Builder maxTemperature(float max) { return temperature(Float.NEGATIVE_INFINITY, max); }
        public Builder temperature(float min, float max)
        {
            minTemperature = min;
            maxTemperature = max;
            return this;
        }

        public Builder minGroundwater(float min) { return groundwater(min, Float.POSITIVE_INFINITY); }
        public Builder maxGroundwater(float max) { return groundwater(Float.NEGATIVE_INFINITY, max); }
        public Builder groundwater(float min, float max)
        {
            minRainfall = min;
            maxRainfall = max;
            return this;
        }

        public Builder minRainVariance(float min) { return rainVariance(min, 1f, false); }
        public Builder maxRainVariance(float max) { return rainVariance(-1f, max, false); }
        public Builder rainVariance(float min, float max, boolean isAbsolute)
        {
            minRainfall = min;
            maxRainfall = max;
            rainVarianceAbsolute = isAbsolute;

            return this;
        }

        public Builder minForest(int min) { return forest(min, 4); }
        public Builder maxForest(int max) { return forest(0, max); }
        public Builder forest(int min, int max)
        {
            minForest = min;
            maxForest = max;
            return this;
        }
        public Builder forestType(ForestType... types)
        {
            forests.addAll(List.of(types));
            return this;
        }
        public Builder months(List<Month> months)
        {
            this.months.addAll(months);
            return this;
        }

        public Builder chance(int value) { chance = value; return this; }
        public Builder distanceBelowSeaLevel(int value) { distanceBelowSeaLevel = value; return this; }
        public Builder maxBrightness(int value) { maxBrightness = value; return this; }

        public Builder fuzzy() { this.fuzzy = true; return this; }
        public Builder solid() { this.solidGround = true; return this; }

        public Fauna build()
        {
            return new Fauna(chance, distanceBelowSeaLevel, new ClimatePlacement(minTemperature, maxTemperature, minRainfall, maxRainfall, minRainVariance, maxRainVariance, rainVarianceAbsolute, minForest, maxForest, forests, fuzzy), solidGround, maxBrightness, months);
        }
    }
}
