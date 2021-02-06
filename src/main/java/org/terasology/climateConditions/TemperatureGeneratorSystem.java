// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

import com.google.common.collect.Maps;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.world.block.BlockComponent;

import java.util.Map;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TemperatureGeneratorSystem extends BaseComponentSystem {
    @In
    private ClimateConditionsSystem environmentSystem;

    private Map<Vector3ic, TemperatureGeneratorComponent> activeComponents = Maps.newHashMap();

    @Override
    public void preBegin() {
        environmentSystem.addTemperatureModifier(1000,
            (value, x, y, z) -> getValue(value, x, y, z));
    }

    @ReceiveEvent
    public void componentActivated(OnActivatedComponent event, TemperatureGeneratorComponent generator, BlockComponent block) {
        activeComponents.put(block.getPosition(new Vector3i()), generator);
    }

    @ReceiveEvent
    public void componentUpdated(OnChangedComponent event, TemperatureGeneratorComponent generator, BlockComponent block) {
        activeComponents.put(block.getPosition(new Vector3i()), generator);
    }

    @ReceiveEvent
    public void componentDeactivated(BeforeDeactivateComponent event, TemperatureGeneratorComponent generator, BlockComponent block) {
        activeComponents.remove(block.getPosition());
    }

    private float getValue(float baseValue, float x, float y, float z) {
        float value = baseValue;
        for (Map.Entry<Vector3ic, TemperatureGeneratorComponent> entry : activeComponents.entrySet()) {
            Vector3ic location = entry.getKey();
            TemperatureGeneratorComponent generator = entry.getValue();

            if ((generator.temperature > value && generator.heater)
                || (generator.temperature < value && !generator.heater)) {
                float distance = Vector3f.distance(location.x(), location.y(), location.z(), x, y, z);
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
