// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Increases the game difficulty in locations with extreme cold climate.
 * Is added/removed by the {@link HypothermiaSystem} when the player enters/leaves a "danger zone".
 */
public class HypothermiaComponent implements Component<HypothermiaComponent> {
    public float walkSpeedMultiplier = 0.6f;
    public float jumpSpeedMultiplier = 0.7f;
    /**
     * The level represents the degree of challenges faced due to Hypothermia.
     * <p>
     * The level is expected to be a positive integer. As of now only levels one to three are supported. Increasing
     * level denotes increasing difficulty, i.e., level 1 being the least challenging and level 3 the most.
     * <p>
     * Level 0 represents no Hypothermia, i.e., the HypothermiaComponent should be removed when this value is supposed
     * to become 0.
     */
    public int level;

    HypothermiaComponent() {
        level = 1;
    }

    HypothermiaComponent(int level) {
        this.level = level;
    }

    @Override
    public void copy(HypothermiaComponent other) {
        this.walkSpeedMultiplier = other.walkSpeedMultiplier;
        this.jumpSpeedMultiplier = other.jumpSpeedMultiplier;
        this.level = other.level;
    }
}
