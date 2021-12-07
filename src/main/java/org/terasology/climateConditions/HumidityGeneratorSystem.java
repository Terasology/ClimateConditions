// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

import com.google.common.collect.Maps;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import java.util.Map;

@RegisterSystem(RegisterMode.AUTHORITY)
public class HumidityGeneratorSystem extends BaseComponentSystem {
    @In
    private ClimateConditionsSystem environmentSystem;

    private Map<Vector3ic, HumidityGeneratorComponent> activeComponents = Maps.newHashMap();

    @Override
    public void preBegin() {
        environmentSystem.addHumidityModifier(1000, this::getValue);
    }

    @ReceiveEvent
    public void componentActivated(OnActivatedComponent event, HumidityGeneratorComponent generator, BlockComponent block) {
        activeComponents.put(block.getPosition(new Vector3i()), generator);
    }

    @ReceiveEvent
    public void componentUpdated(OnChangedComponent event, HumidityGeneratorComponent generator, BlockComponent block) {
        activeComponents.put(block.getPosition(new Vector3i()), generator);
    }

    @ReceiveEvent
    public void componentDeactivated(BeforeDeactivateComponent event, HumidityGeneratorComponent generator, BlockComponent block) {
        activeComponents.remove(block.getPosition());
    }

    private float getValue(float baseValue, float x, float y, float z) {
        float value = baseValue;
        for (Map.Entry<Vector3ic, HumidityGeneratorComponent> entry : activeComponents.entrySet()) {
            Vector3ic location = entry.getKey();
            HumidityGeneratorComponent generator = entry.getValue();

            if ((generator.humidity > value && generator.humidifier)
                || (generator.humidity < value && !generator.humidifier)) {

                float distance = Vector3f.distance(location.x(), location.y(), location.z(), x, y, z);
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
}
