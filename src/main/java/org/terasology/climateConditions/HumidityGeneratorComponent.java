// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

@ForceBlockActive
public class HumidityGeneratorComponent implements Component<HumidityGeneratorComponent> {
    public float humidity;
    public float flatRange;
    public float maxRange;
    public boolean humidifier;

    @Override
    public void copyFrom(HumidityGeneratorComponent other) {
        this.humidity = other.humidity;
        this.flatRange = other.flatRange;
        this.maxRange = other.maxRange;
        this.humidifier = other.humidifier;
    }
}
