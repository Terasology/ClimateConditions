// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.climateConditions;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;

/**
 * Augments the world with a visible breath particle effect. Has a desired effect only if the entity has a {@link
 * org.terasology.logic.location.LocationComponent} which determines the location and direction of the effect. Is
 * added/updated by the {@link VisibleBreathingSystem} periodically.
 */
public class VisibleBreathComponent implements Component {
    public EntityRef particleEntity = EntityRef.NULL;
}
