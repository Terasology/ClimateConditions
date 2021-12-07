// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

import com.google.common.base.Function;
import org.terasology.engine.utilities.procedural.Noise2D;
import org.terasology.engine.utilities.procedural.SimplexNoise;
import org.terasology.math.TeraMath;

public class ConditionsBaseField {
    private Noise2D noiseTable;
    private int seaLevel;
    private int maxLevel;
    private float noiseMultiplier;
    private Function<Float, Float> function;

    public ConditionsBaseField(int seaLevel, int maxLevel, float noiseMultiplier,
                               Function<Float, Float> function, long conditionSeed) {
        this.seaLevel = seaLevel;
        this.maxLevel = maxLevel;
        this.noiseMultiplier = noiseMultiplier;
        this.function = function;
        noiseTable = new SimplexNoise(conditionSeed);
    }

    public float get(float x, float y, float z) {
        return TeraMath.clamp(getConditionAlpha(x, y, z), 0, 1);
    }

    private float getConditionAlpha(float x, float y, float z) {
        float result = noiseTable.noise(x * noiseMultiplier, z * noiseMultiplier);
        float temperatureBase = function.apply(TeraMath.clamp((result + 1.0f) / 2.0f));
        if (y <= seaLevel) {
            return temperatureBase;
        } else if (y >= maxLevel) {
            return 0;
        } else {
            // The higher above see level - the colder
            return temperatureBase * (1f * (maxLevel - y) / (maxLevel - seaLevel));
        }
    }
}
