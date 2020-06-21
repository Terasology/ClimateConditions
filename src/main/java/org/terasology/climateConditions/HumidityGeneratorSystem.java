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

import com.google.common.collect.Maps;
import org.terasology.biomesAPI.Biome;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.ImmutableBlockLocation;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.core.world.CoreBiome;


import java.util.Map;

@RegisterSystem(RegisterMode.AUTHORITY)
public class HumidityGeneratorSystem extends BaseComponentSystem {
    @In
    private ClimateConditionsSystem environmentSystem;

    private Map<ImmutableBlockLocation, HumidityGeneratorComponent> activeComponents = Maps.newHashMap();

    @Override
    public void preBegin() {
        environmentSystem.addHumidityModifier(1000,
            new ConditionModifier() {
                @Override
                public float getCondition(float value, float x, float y, float z) {
                    Vector3i position = new Vector3i(x, y, z);
                    //Perfectly humid if the block is water
                    Block currentBlock = environmentSystem.getWorld().getBlock(position);
                    if (currentBlock != null) {
                        if (currentBlock.getDisplayName().contains("Water")) {
                            return 1;
                        }
                    }
                    // Reduce humidity (humidity = relative humidity) when temperature is higher
                    float modifier = (environmentSystem.temperatureBase - environmentSystem.getTemperature(x,
                            y, z)) * .05f * (1 + value);

                    if (environmentSystem.getBiome().getBiome(position).isPresent()) {
                        // Different biomes indicate different humidities
                        Biome currentBiome = environmentSystem.getBiome().getBiome(position).get();
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
                            return TeraMath.clamp(value + modifier, 0, 1);
                        }
                    }
                    return -1000;
                }
            });
    }

    @ReceiveEvent
    public void componentActivated(OnActivatedComponent event, HumidityGeneratorComponent generator, BlockComponent block) {
        activeComponents.put(new ImmutableBlockLocation(block.getPosition()), generator);
    }

    @ReceiveEvent
    public void componentUpdated(OnChangedComponent event, HumidityGeneratorComponent generator, BlockComponent block) {
        activeComponents.put(new ImmutableBlockLocation(block.getPosition()), generator);
    }

    @ReceiveEvent
    public void componentDeactivated(BeforeDeactivateComponent event, HumidityGeneratorComponent generator, BlockComponent block) {
        activeComponents.remove(new ImmutableBlockLocation(block.getPosition()));
    }

    private float getValue(float baseValue, float x, float y, float z) {
        float value = baseValue;
        for (Map.Entry<ImmutableBlockLocation, HumidityGeneratorComponent> entry : activeComponents.entrySet()) {
            ImmutableBlockLocation location = entry.getKey();
            HumidityGeneratorComponent generator = entry.getValue();

            if ((generator.humidity > value && generator.humidifier)
                    || (generator.humidity < value && !generator.humidifier)) {
                float distance = getDistance(x, y, z, location);
                if (distance <= generator.flatRange) {
                    value = generator.humidity;
                } else if (distance < generator.maxRange) {
                    float distanceFactor = 1f - (distance - generator.flatRange) / (generator.maxRange - generator.flatRange);
                    value = value + (float) ((generator.humidity - value) * Math.pow(distanceFactor, 1 / 3f));
                }
            }
        }

        return value;
    }

    private float getDistance(float x, float y, float z, ImmutableBlockLocation location) {
        return (float) Math.sqrt((location.x - x) * (location.x - x)
                + (location.y - y) * (location.y - y) + (location.z - z) * (location.z - z));
    }
}
