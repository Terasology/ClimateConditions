// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.climateConditions;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Provides Body Temperature to entity attached with BodyTemperatureComponent.
 * Is managed by {@link BodyTemperatureSystem}
 */
public class BodyTemperatureComponent implements Component<BodyTemperatureComponent> {

    /**
     * Body Temperature Levels corresponding to value ranges:
     *   CriticalLow      <= 0.17
     *   Low            (0.17, 0.22]
     *   Reduced        (0.22, 0.3]
     *   Normal         (0.3, 0.5)
     *   Raised         [0.5, 0.55)
     *   High           [0.55, 0.6)
     *   CriticalHigh    > = 0.6
     *   Note: These values are configured according to the current environment temperature values and can be modified
     *   in the {@link BodyTemperatureSystem}
     */

    /** Stores the current body temperature value.*/
    @Replicate
    public float current = 0.4f;

    /** Stores the current body temperature level.*/
    @Replicate
    public BodyTemperatureLevel currentLevel = BodyTemperatureLevel.NORMAL;

    @Override
    public void copyFrom(BodyTemperatureComponent other) {
        this.current = other.current;
        this.currentLevel = other.currentLevel;
    }
}
