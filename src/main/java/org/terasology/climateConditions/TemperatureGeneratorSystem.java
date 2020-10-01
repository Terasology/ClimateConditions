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
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.math.JomlUtil;
import org.terasology.registry.In;
import org.terasology.world.block.BlockComponent;

import java.util.Map;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TemperatureGeneratorSystem extends BaseComponentSystem {
    @In
    private ClimateConditionsSystem environmentSystem;

    private Map<Vector3i, TemperatureGeneratorComponent> activeComponents = Maps.newHashMap();

    @Override
    public void preBegin() {
        environmentSystem.addTemperatureModifier(1000,
            (value, x, y, z) -> getValue(value, x, y, z));
    }

    @ReceiveEvent
    public void componentActivated(OnActivatedComponent event, TemperatureGeneratorComponent generator, BlockComponent block) {
        activeComponents.put(JomlUtil.from(block.position), generator);
    }

    @ReceiveEvent
    public void componentUpdated(OnChangedComponent event, TemperatureGeneratorComponent generator, BlockComponent block) {
        activeComponents.put(JomlUtil.from(block.position), generator);
    }

    @ReceiveEvent
    public void componentDeactivated(BeforeDeactivateComponent event, TemperatureGeneratorComponent generator, BlockComponent block) {
        activeComponents.remove(JomlUtil.from(block.position));
    }

    private float getValue(float baseValue, float x, float y, float z) {
        float value = baseValue;
        for (Map.Entry<Vector3i, TemperatureGeneratorComponent> entry : activeComponents.entrySet()) {
            Vector3i location = entry.getKey();
            TemperatureGeneratorComponent generator = entry.getValue();

            if ((generator.temperature > value && generator.heater)
                || (generator.temperature < value && !generator.heater)) {
                float distance = Vector3f.distance(location.x, location.y, location.z, x, y, z);
                if (distance <= generator.flatRange) {
                    value = generator.temperature;
                } else if (distance < generator.maxRange) {
                    float distanceFactor = 1f - (distance - generator.flatRange) / (generator.maxRange - generator.flatRange);
                    value = value + (float) ((generator.temperature - value) * Math.pow(distanceFactor, 1 / 3f));
                }
            }
        }

        return value;
    }
}
