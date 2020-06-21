/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.climateConditions;

import org.terasology.biomesAPI.Biome;
import org.terasology.biomesAPI.BiomeRegistry;
import org.terasology.core.world.CoreBiome;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3i;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;

public class ConditionsBaseField {
    public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";

    private String type;
    private SimplexNoise noiseTable;
    private int seaLevel;
    private float temperatureBase;
    private float noiseMultiplier;
    private BlockManager blockManager;
    private WorldProvider worldProvider;
    private BiomeRegistry biomeManager;

    public ConditionsBaseField(String type, int seaLevel, float noiseMultiplier, float temperatureBase,
                               long conditionSeed, BlockManager block, BiomeRegistry biome, WorldProvider world) {
        this.type = type;
        this.seaLevel = seaLevel;
        this.noiseMultiplier = noiseMultiplier;
        this.temperatureBase = temperatureBase;
        noiseTable = new SimplexNoise(conditionSeed);
        worldProvider = world;
        blockManager = block;
        biomeManager = biome;
    }

    public float get(float x, float y, float z, boolean clamp) {
        if (clamp) {
            return TeraMath.clamp(getConditionAlpha(x, y, z), 0, 1);
        } else {
            return getConditionAlpha(x, y, z);
        }
    }

    private float getConditionAlpha(float x, float y, float z) {
        float initialResult = noiseTable.noise(x * noiseMultiplier, z * noiseMultiplier);

        if (type.equals(TEMPERATURE)) {
            return getTemperature(new Vector3i(x, y, z), initialResult);
        } else {
            return getHumidity( new Vector3i(x, y, z), initialResult);
        }
    }

    /**
     * Gets the temperature at a given location.
     *
     * Returns the temperature as a decimal value in Celsius, where 1 == 100 degrees and 0 == 0 degrees
     */
    private float getTemperature(Vector3i position, float noise) {
        if (position.y <= seaLevel) {
            return temperatureBase;
        } else {
            // The higher above sea level - the colder (changes ~.03 degrees C per 1 meter change)
            // Temperature decreased by the height above sea level times .00006 as an exaggerated approximation
            float modifier = temperatureBase * (1 + noise * .07f) - (position.y - seaLevel) * .00006f + .07f;
            Block currentBlock = worldProvider.getBlock(position);
            if (currentBlock != null && biomeManager.getBiome(position).isPresent()) {
                Biome currentBiome = biomeManager.getBiome(position).get();

                // Block-by-block modification
                if (currentBlock.getDisplayName().contains("Lava")) {
                    modifier += 12;
                }
                // Biome-by-biome modification
                if (currentBiome.equals(CoreBiome.DESERT)) {
                    modifier += .05;
                } else if (currentBiome.equals(CoreBiome.SNOW) ||
                        ((currentBiome.equals(CoreBiome.PLAINS) || currentBiome.equals(CoreBiome.FOREST)
                                || currentBiome.equals(CoreBiome.MOUNTAINS)) && position.y > 96 + seaLevel)) {
                    modifier -= .30;
                } else if (currentBiome.equals(CoreBiome.MOUNTAINS)) {
                    modifier -= .15;
                } else if (currentBiome.equals(CoreBiome.OCEAN)) {
                    modifier -= .1;
                }
                return modifier;
            }
        }
        return -100;
    }

    /**
     * Gets the humidity at a given location.
     */
    private float getHumidity(Vector3i position, float noise) {
        //Perfectly humid if the block is water
        Block currentBlock = worldProvider.getBlock(position);
        if (currentBlock != null) {
            if (currentBlock.getDisplayName().contains("Water")) {
                return 1;
            }
            // Reduce humidity (humidity = relative humidity) when temperature is higher
            float modifier = (temperatureBase - getTemperature(position, noise)) * .05f * (1 + noise);

            if (biomeManager.getBiome(position).isPresent()) {
                // Different biomes indicate different humidities
                Biome currentBiome = biomeManager.getBiome(position).get();
                if (currentBiome.equals(CoreBiome.OCEAN) || currentBiome.equals(CoreBiome.SNOW)) {
                    return TeraMath.clamp(.9f + modifier, 0, 1);
                } else if (currentBiome.equals(CoreBiome.BEACH)) {
                    return TeraMath.clamp(.8f + modifier, 0, 1);
                } else if (currentBiome.equals(CoreBiome.FOREST)) {
                    return TeraMath.clamp(.7f + modifier, 0, 1);
                } else if (currentBiome.equals(CoreBiome.PLAINS)) {
                    return TeraMath.clamp(.55f + modifier, 0, 1);
                } else if (currentBiome.equals(CoreBiome.MOUNTAINS)) {
                    return TeraMath.clamp(.3f + modifier, 0, 1);
                } else if (currentBiome.equals(CoreBiome.DESERT)) {
                    return TeraMath.clamp(.15f + modifier, 0, 1);
                } else {
                    return TeraMath.clamp(noise + modifier, 0, 1);
                }
            }
        }
        return -100;
    }
}
