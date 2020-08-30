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
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.world.WorldProvider;

public class ConditionsBaseField {

    @In
    ClimateConditionsSystem climateConditionsSystem;
    @In
    WorldProvider worldProvider;

    public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";

    private String type;

    public ConditionsBaseField(String type) {
        this.type = type;
        worldProvider = CoreRegistry.get(WorldProvider.class);
    }

    public float get(float x, float y, float z, boolean clamp) {
        if (clamp) {
            return TeraMath.clamp(getConditionAlpha(x, y, z), 0f, 1f);
        } else {
            return getConditionAlpha(x, y, z);
        }
    }

    private float getConditionAlpha(float x, float y, float z) {
        if (type.equals(TEMPERATURE)) {
            return ((float) worldProvider.getExtraData("coreWorlds.temperature", new Vector3i(x, y, z))) / 1000 - .001f * y;
        } else {
            return ((float) worldProvider.getExtraData("coreWorlds.humidity", new Vector3i(x, y, z))) / 1000;
        }
    }
}
