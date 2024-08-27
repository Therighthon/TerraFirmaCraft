/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import net.minecraft.util.Mth;
import net.minecraft.world.level.material.MapColor;

/**
 * This is purely used for decoration
 * Loosely based on https://en.wikipedia.org/wiki/K%C3%B6ppen_climate_classification
 * But not nearly as loosely as it was in 1.20 and before because we not have seasonal rainfall.
 */
public enum KoppenClimateClassification
{
    AF(MapColor.PLANT, MapColor.Brightness.HIGH),
    AM(MapColor.LAPIS, MapColor.Brightness.LOW),
    AW(MapColor.LAPIS, MapColor.Brightness.HIGH),
    AS(MapColor.COLOR_CYAN, MapColor.Brightness.HIGH),
    BWH(MapColor.FIRE, MapColor.Brightness.NORMAL),
    BWK(MapColor.CRIMSON_NYLIUM, MapColor.Brightness.NORMAL),
    BSH(MapColor.COLOR_ORANGE, MapColor.Brightness.HIGH),
    BSK(MapColor.COLOR_ORANGE, MapColor.Brightness.LOW),
    CSA(MapColor.COLOR_YELLOW, MapColor.Brightness.NORMAL),
    CSB(MapColor.COLOR_YELLOW, MapColor.Brightness.LOW),
    CSC(MapColor.COLOR_YELLOW, MapColor.Brightness.LOWEST),
    CWA(MapColor.DIAMOND, MapColor.Brightness.HIGH),
    CWB(MapColor.DIAMOND, MapColor.Brightness.NORMAL),
    CWC(MapColor.DIAMOND, MapColor.Brightness.LOW),
    CFA(MapColor.GRASS, MapColor.Brightness.HIGH),
    CFB(MapColor.GRASS, MapColor.Brightness.NORMAL),
    CFC(MapColor.GRASS, MapColor.Brightness.LOW),
    DSA(MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.HIGH),
    DSB(MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.NORMAL),
    DSC(MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.LOW),
    DSD(MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.LOWEST),
    DWA(MapColor.ICE, MapColor.Brightness.HIGH),
    DWB(MapColor.ICE, MapColor.Brightness.NORMAL),
    DWC(MapColor.ICE, MapColor.Brightness.LOW),
    DWD(MapColor.ICE, MapColor.Brightness.LOWEST),
    DFA(MapColor.COLOR_GREEN, MapColor.Brightness.HIGH),
    DFB(MapColor.COLOR_GREEN, MapColor.Brightness.NORMAL),
    DFC(MapColor.COLOR_GREEN, MapColor.Brightness.LOW),
    DFD(MapColor.COLOR_GREEN, MapColor.Brightness.LOWEST),
    ET(MapColor.SNOW, MapColor.Brightness.LOW),
    EF(MapColor.COLOR_LIGHT_GRAY, MapColor.Brightness.LOW);

    private final MapColor color;
    private final MapColor.Brightness brightness;

    KoppenClimateClassification(MapColor color, MapColor.Brightness brightness)
    {
        this.color = color;
        this.brightness = brightness;
    }

    public MapColor getColor()
    {
        return this.color;
    }

    public MapColor.Brightness getBrightness()
    {
        return this.brightness;
    }

    public static KoppenClimateClassification classify(float averageTemperature, float rainfall, float rainVar)
    {
        // True Koppen: When none of the year is above freezing, temp var when avg = -17C ~= 17C
        if (averageTemperature < -17f)
        {
            return EF;
        }
        // True Koppen: When summer temps don't exceed 10C, but this would be avg temp = -1C which would make for massive tundras
        else if (averageTemperature <= -12f)
        {
            return ET;
        }
        else if (rainfall < 75f)
        {
            if (averageTemperature > 18f)
            {
                return BWH;
            }
            else
            {
                return BWK;
            }
        }
        else if (rainfall < 150f)
        {
            if (averageTemperature > 18)
            {
                return BSH;
            }
            else
            {
                return BSK;
            }
        }
        // True Koppen: Lowest monthly temp > 18C, temp var when avg = 21C ~= 3C
        else if (averageTemperature > 21f)
        {
            if (rainfall * (1 + rainVar) > 900f)
            {
                return AM;
            }
            else if (rainVar > 0.5f)
            {
                return AW;
            }
            else if (rainVar < -0.5f)
            {
                return AS;
            }
            else
            {
                return AF;
            }
        }
        // True Koppen: Lowest monthly temp > 0C, temp var when avg = 8C ~= 8C
        else if (averageTemperature > 8f)
        {
            if (averageTemperature > 17f)
            {
                if (rainVar > 0.5f)
                {
                    return CWA;
                }
                else if (rainVar < -0.5f)
                {
                    return CSA;
                }
                else
                {
                    return CFA;
                }
            }
            else if (averageTemperature > 12f)
            {
                if (rainVar > 0.5f)
                {
                    return CWB;
                }
                else if (rainVar < -0.5f)
                {
                    return CSB;
                }
                else
                {
                    return CFB;
                }
            }
            else
            {
                if (rainVar > 0.5f)
                {
                    return CWC;
                }
                else if (rainVar < -0.5f)
                {
                    return CSC;
                }
                else
                {
                    return CFC;
                }
            }

        }
        // Otherwise, has to be in Group D
        else if (averageTemperature > 3f)
        {
            if (rainVar > 0.5f)
            {
                return DWA;
            }
            else if (rainVar < -0.5f)
            {
                return DSA;
            }
            else
            {
                return DFA;
            }
        }
        else if (averageTemperature > -2f)
        {
            if (rainVar > 0.5f)
            {
                return DWB;
            }
            else if (rainVar < -0.5f)
            {
                return DSB;
            }
            else
            {
                return DFB;
            }
        }
        else if (averageTemperature > -8f)
        {
            if (rainVar > 0.5f)
            {
                return DWC;
            }
            else if (rainVar < -0.5f)
            {
                return DSC;
            }
            else
            {
                return DFC;
            }
        }
        else if (rainVar > 0.5f)
        {
            return DWD;
        }
        else if (rainVar < -0.5f)
        {
            return DSD;
        }
        else
        {
            return DFD;
        }
    }
}