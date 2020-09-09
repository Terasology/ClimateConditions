// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

import org.terasology.engine.entitySystem.Component;

public class HyperthermiaComponent implements Component {
    public float walkSpeedMultiplier = 0.7f;
    public float jumpSpeedMultiplier = 0.85f;
    public float regenMultiplier = 0.8f;
    public float maxHealthMultiplier = 0.8f;
    public float thirstMultiplier = 2f;
    /**
     * The level represents the degree of challenges faced due to Hyperthermia.
     * <p>
     * The level is expected to be a positive integer. As of now only levels one to three are supported. Increasing
     * level denotes increasing difficulty, i.e., level 1 being the least challenging and level 3 the most.
     * <p>
     * Level 0 represents no Hypothermia, i.e., this component should be removed when this value is supposed to become
     * 0.
     */
    public int level;

    HyperthermiaComponent() {
        level = 1;
    }

    HyperthermiaComponent(int level) {
        this.level = level;
    }
}
