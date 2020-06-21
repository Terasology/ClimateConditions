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

import org.terasology.math.TeraMath;
import org.terasology.registry.In;
import org.terasology.utilities.procedural.SimplexNoise;

public class ConditionsBaseField {

    @In
    ClimateConditionsSystem climateConditionsSystem;

    public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";

    private String type;
    private SimplexNoise noiseTable;
    private float noiseMultiplier;

    public ConditionsBaseField(String type, float noiseMultiplier, long conditionSeed) {
        this.type = type;
        this.noiseMultiplier = noiseMultiplier;
        noiseTable = new SimplexNoise(conditionSeed);
    }

    //TODO: see where this is used and replace with ClimateConditionsSystem's getTemperature/getHumidity
    public float get(float x, float y, float z, boolean clamp) {
        if (clamp) {
            return TeraMath.clamp(getConditionAlpha(x, y, z), 0, 1);
        } else {
            return getConditionAlpha(x, y, z);
        }
    }

    private float getConditionAlpha(float x, float y, float z) {
        return noiseTable.noise(x * noiseMultiplier, z * noiseMultiplier);
    }
}
