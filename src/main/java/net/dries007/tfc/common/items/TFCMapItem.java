package net.dries007.tfc.common.items;

import java.util.Map;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.climate.KoppenClimateClassification;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.util.tracker.WorldTracker;

public class TFCMapItem extends MapItem
{
    public TFCMapItem(Properties properties)
    {
        super(properties);
    }

    public static ItemStack create(Level level, int levelX, int levelZ, byte scale, boolean trackingPosition, boolean unlimitedTracking) {
        ItemStack itemstack = new ItemStack(TFCItems.FILLED_MAP);
        MapId mapid = createNewSavedData(level, levelX, levelZ, scale, trackingPosition, unlimitedTracking, level.dimension());
        itemstack.set(DataComponents.MAP_ID, mapid);
        return itemstack;
    }

    private static MapId createNewSavedData(Level level, int x, int z, int scale, boolean trackingPosition, boolean unlimitedTracking, ResourceKey<Level> dimension) {
        MapItemSavedData mapitemsaveddata = MapItemSavedData.createFresh((double)x, (double)z, (byte)scale, trackingPosition, unlimitedTracking, dimension);
        MapId mapid = level.getFreeMapId();
        level.setMapData(mapid, mapitemsaveddata);
        return mapid;
    }

    public void update(Level level, Entity viewer, MapItemSavedData data) {
        ClimateModel climateModel = WorldTracker.get(level).getClimateModel();

        if (level.dimension() == data.dimension && viewer instanceof Player) {
            int i = 1 << data.scale;
            int j = data.centerX;
            int k = data.centerZ;
            int l = Mth.floor(viewer.getX() - (double)j) / i + 64;
            int i1 = Mth.floor(viewer.getZ() - (double)k) / i + 64;
            int j1 = 128 / i;
            if (level.dimensionType().hasCeiling()) {
                j1 /= 2;
            }

            MapItemSavedData.HoldingPlayer mapitemsaveddata$holdingplayer = data.getHoldingPlayer((Player)viewer);
            ++mapitemsaveddata$holdingplayer.step;
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos mutableBlockPos1 = new BlockPos.MutableBlockPos();
            boolean flag = false;

            for(int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
                if ((k1 & 15) == (mapitemsaveddata$holdingplayer.step & 15) || flag) {
                    flag = false;

                    for(int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1) {
                        if (k1 >= 0 && l1 >= -1 && k1 < 128 && l1 < 128) {
                            int i2 = Mth.square(k1 - l) + Mth.square(l1 - i1);
                            boolean flag1 = i2 > (j1 - 2) * (j1 - 2);
                            int j2 = (j / i + k1 - 64) * i;
                            int k2 = (k / i + l1 - 64) * i;
                            Multiset<MapColor> mapColors = LinkedHashMultiset.create();
                            Multiset<MapColor.Brightness> brightnesses = LinkedHashMultiset.create();
                            LevelChunk levelchunk = level.getChunk(SectionPos.blockToSectionCoord(j2), SectionPos.blockToSectionCoord(k2));
                            if (!levelchunk.isEmpty()) {
                                int l2 = 0;
                                double d1 = 0.0;
                                int i4;

                                for(i4 = 0; i4 < i; ++i4) {
                                    for(int j3 = 0; j3 < i; ++j3) {
                                        mutableBlockPos.set(j2 + i4, 0, k2 + j3);
                                        int k3 = levelchunk.getHeight(Heightmap.Types.WORLD_SURFACE, mutableBlockPos.getX(), mutableBlockPos.getZ()) + 1;
                                        MapColor mapColor;
                                        MapColor.Brightness brightness;

                                        do {
                                            --k3;
                                            mutableBlockPos.setY(k3);
                                            if (levelchunk.getBlockState(mutableBlockPos).getMapColor(level, mutableBlockPos) == MapColor.WATER)
                                            {
                                                mapColor =  MapColor.WATER;
                                                brightness = MapColor.Brightness.NORMAL;
                                            }
                                            else
                                            {

                                                float avgTemp = climateModel.getAverageTemperature(level, mutableBlockPos);
                                                float avgRain = climateModel.getAverageRainfall(level, mutableBlockPos);
                                                float rainVar = climateModel.getRainfallVariance(level, mutableBlockPos);
                                                KoppenClimateClassification koppen = KoppenClimateClassification.classify((float) (avgTemp - 0.16*(mutableBlockPos.getY() - OverworldClimateModel.SEA_LEVEL)), avgRain, rainVar);
                                                mapColor = koppen.getColor();
                                                brightness = koppen.getBrightness();
                                            }
                                        } while(mapColor == MapColor.NONE && k3 > level.getMinBuildHeight());

                                        data.checkBanners(level, mutableBlockPos.getX(), mutableBlockPos.getZ());
                                        d1 += (double)k3 / (double)(i * i);
                                        mapColors.add(mapColor);
                                        brightnesses.add(brightness);
                                    }
                                }

                                l2 /= i * i;
                                MapColor mapColorRead = Iterables.getFirst(Multisets.copyHighestCountFirst(mapColors), MapColor.NONE);
                                MapColor.Brightness brightnessRead = Iterables.getFirst(Multisets.copyHighestCountFirst(brightnesses), MapColor.Brightness.LOWEST);

                                if (l1 >= 0 && i2 < j1 * j1 && (!flag1 || (k1 + l1 & 1) != 0)) {
                                    flag |= data.updateColor(k1, l1, mapColorRead.getPackedId(brightnessRead));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private MapColor getTempMapColor(float avgTemp)
    {
        if (avgTemp > 20)
        {
            return MapColor.COLOR_RED;
        }
        else if (avgTemp > 10)
        {
            return MapColor.COLOR_ORANGE;
        }
        else if (avgTemp > 0)
        {
            return MapColor.COLOR_YELLOW;
        }
        else if (avgTemp > -10)
        {
            return MapColor.COLOR_GREEN;
        }
        else if (avgTemp > -17)
        {
            return MapColor.COLOR_LIGHT_BLUE;
        }
        else {
            return MapColor.COLOR_BLUE;
        }
    }
}
