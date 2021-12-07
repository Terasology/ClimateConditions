// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.joml.Vector3fc;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.Share;

import java.util.Map;

@RegisterSystem(value = RegisterMode.AUTHORITY)
@Share(value = ClimateConditionsSystem.class)
public class ClimateConditionsSystem extends BaseComponentSystem {
    private final float minMultiplier = 0.0005f;
    private final float maxMultiplier = 0.01f;

    private ConditionsBaseField temperatureBaseField;
    private ConditionsBaseField humidityBaseField;

    private Map<Float, ConditionModifier> temperatureModifiers = Maps.newTreeMap();
    private Map<Float, ConditionModifier> humidityModifiers = Maps.newTreeMap();

    private float temperatureMinimum;
    private float temperatureMaximum;

    private float humidityMinimum;
    private float humidityMaximum;

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

    public void configureTemperature(int seaLevel, int maxLevel, float diversity, Function<Float, Float> function,
                                     float minimumValue, float maximumValue) {
        int seed = worldSeed.hashCode();

        float noiseMultiplier = minMultiplier + (maxMultiplier - minMultiplier) * diversity;

        temperatureBaseField = new ConditionsBaseField(seaLevel, maxLevel, noiseMultiplier, function, seed + 582374);

        temperatureMinimum = minimumValue;
        temperatureMaximum = maximumValue;
    }

    public void configureHumidity(int seaLevel, int maxLevel, float diversity, Function<Float, Float> function,
                                  float minimumValue, float maximumValue) {
        int seed = worldSeed.hashCode();

        float noiseMultiplier = minMultiplier + (maxMultiplier - minMultiplier) * diversity;

        humidityBaseField = new ConditionsBaseField(seaLevel, maxLevel, noiseMultiplier, function, seed + 129534);

        humidityMinimum = minimumValue;
        humidityMaximum = maximumValue;
    }

    public ConditionsBaseField getHumidityBaseField() {
        return humidityBaseField;
    }

    public ConditionsBaseField getTemperatureBaseField() {
        return temperatureBaseField;
    }

    public float getTemperature(float x, float y, float z) {
        float value = temperatureBaseField.get(x, y, z);

        value = temperatureMinimum + value * (temperatureMaximum - temperatureMinimum);

        for (ConditionModifier temperatureModifier : temperatureModifiers.values()) {
            value = temperatureModifier.getCondition(value, x, y, z);
        }

        return value;
    }

    public float getHumidity(float x, float y, float z) {
        float value = humidityBaseField.get(x, y, z);

        value = humidityMinimum + value * (humidityMaximum - humidityMinimum);

        for (ConditionModifier humidityModifier : humidityModifiers.values()) {
            value = humidityModifier.getCondition(value, x, y, z);
        }

        return value;
    }

    public float getTemperature(Vector3fc position) {
        return getTemperature(position.x(), position.y(), position.z());
    }

    public float getHumidity(Vector3fc position) {
        return getHumidity(position.x(), position.y(), position.z());
    }

    public String getWorldSeed() {
        return worldSeed;
    }
}
