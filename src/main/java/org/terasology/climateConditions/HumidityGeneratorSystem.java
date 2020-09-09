// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

import com.google.common.collect.Maps;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.location.ImmutableBlockLocation;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockComponent;

import java.util.Map;

@RegisterSystem(RegisterMode.AUTHORITY)
public class HumidityGeneratorSystem extends BaseComponentSystem {
    private final Map<ImmutableBlockLocation, HumidityGeneratorComponent> activeComponents = Maps.newHashMap();
    @In
    private ClimateConditionsSystem environmentSystem;

    @Override
    public void preBegin() {
        environmentSystem.addHumidityModifier(1000,
                new ConditionModifier() {
                    @Override
                    public float getCondition(float value, float x, float y, float z) {
                        return getValue(value, x, y, z);
                    }
                });
    }

    @ReceiveEvent
    public void componentActivated(OnActivatedComponent event, HumidityGeneratorComponent generator,
                                   BlockComponent block) {
        activeComponents.put(new ImmutableBlockLocation(block.getPosition()), generator);
    }

    @ReceiveEvent
    public void componentUpdated(OnChangedComponent event, HumidityGeneratorComponent generator, BlockComponent block) {
        activeComponents.put(new ImmutableBlockLocation(block.getPosition()), generator);
    }

    @ReceiveEvent
    public void componentDeactivated(BeforeDeactivateComponent event, HumidityGeneratorComponent generator,
                                     BlockComponent block) {
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
                    float distanceFactor =
                            1f - (distance - generator.flatRange) / (generator.maxRange - generator.flatRange);
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
