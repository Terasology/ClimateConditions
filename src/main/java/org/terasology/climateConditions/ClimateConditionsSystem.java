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
import org.terasology.biomesAPI.BiomeRegistry;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.Share;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockManager;

import java.util.Map;

@RegisterSystem(value = RegisterMode.AUTHORITY)
@Share(value = ClimateConditionsSystem.class)
public class ClimateConditionsSystem extends BaseComponentSystem {
    @In
    private WorldProvider world;

    private ConditionsBaseField temperatureBaseField;
    private ConditionsBaseField humidityBaseField;

    private Map<Float, ConditionModifier> temperatureModifiers = Maps.newTreeMap();
    private Map<Float, ConditionModifier> humidityModifiers = Maps.newTreeMap();

    private float temperatureMinimum;
    private float temperatureMaximum;

    private float humidityMinimum;
    private float humidityMaximum;

    float seaLevel;

    private String worldSeed;

    public void setWorldSeed(String worldSeed) {
        this.worldSeed = worldSeed;
    }

    public void addTemperatureModifier(float order, ConditionModifier temperatureModifier) {
        temperatureModifiers.put(order, temperatureModifier);
    }

    public void addHumidityModifier(float order, ConditionModifier humidityModifier) {
        humidityModifiers.put(order, humidityModifier);
    }

    public void configureTemperature() {
        configureTemperature(32, 10, -10, 10);
    }

    public void configureHumidity() {
        configureHumidity(32, 10, 0, 1);
    }

    public void configureTemperature(int seaLevel, float diversity, float minimumValue, float maximumValue) {
        temperatureBaseField = new ConditionsBaseField(ConditionsBaseField.TEMPERATURE);

        temperatureMinimum = minimumValue;
        temperatureMaximum = maximumValue;

        this.seaLevel = seaLevel;
    }

    public void configureHumidity(int seaLevel, float diversity, float minimumValue, float maximumValue) {
        humidityBaseField = new ConditionsBaseField(ConditionsBaseField.HUMIDITY);

        humidityMinimum = minimumValue;
        humidityMaximum = maximumValue;

        this.seaLevel = seaLevel;
    }

    public ConditionsBaseField getHumidityBaseField() {
        return humidityBaseField;
    }

    public ConditionsBaseField getTemperatureBaseField() {
        return temperatureBaseField;
    }

    public float getTemperature(float x, float y, float z) {
        return temperatureBaseField.get(x, y, z, true);
    }

    public float getHumidity(float x, float y, float z) {
        return humidityBaseField.get(x, y, z, true);
    }

    public float getTemperature(Vector3f position) {
        return getTemperature(position.getX(), position.getY(), position.getZ());
    }

    public float getHumidity(Vector3f position) {
        return getHumidity(position.getX(), position.getY(), position.getZ());
    }

    public String getWorldSeed() {
        return worldSeed;
    }
    public WorldProvider getWorld() { return world; }
}
