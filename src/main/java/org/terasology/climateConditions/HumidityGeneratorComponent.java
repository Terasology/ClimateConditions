// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.world.block.ForceBlockActive;

@ForceBlockActive
public class HumidityGeneratorComponent implements Component {
    public float humidity;
    public float flatRange;
    public float maxRange;
    public boolean humidifier;
}
