// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

@ForceBlockActive
public class TemperatureGeneratorComponent implements Component<TemperatureGeneratorComponent> {
    public float temperature;
    public float flatRange;
    public float maxRange;
    public boolean heater;

    @Override
    public void copy(TemperatureGeneratorComponent other) {
        this.temperature = other.temperature;
        this.flatRange = other.flatRange;
        this.maxRange = other.maxRange;
        this.heater = other.heater;
    }
}
