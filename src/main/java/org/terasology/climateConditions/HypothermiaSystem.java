// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.AffectJumpForceEvent;
import org.terasology.engine.logic.characters.GetMaxSpeedEvent;
import org.terasology.engine.logic.characters.MovementMode;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 *  Handles effects related to Hypothermia.
 *  Hypothermia occurs in case of extremely low body temperature and, e.g., slows the player's movements.
 *  For adding new effects in existing or new Hypothermia Levels, {@link HypothermiaLevelChangedEvent} should be
 *  reacted to either in this or a separate authority system for eg. {@link FrostbiteSystem}.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class HypothermiaSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(HyperthermiaSystem.class);

    /**
     * Reduces the walking/running speed of the player.
     * Is only active iff the player has a {@link HypothermiaComponent}.
     */
    @ReceiveEvent
    public void modifySpeed(GetMaxSpeedEvent event, EntityRef player, HypothermiaComponent hypothermia) {
        if (event.getMovementMode() == MovementMode.WALKING) {
            event.multiply(hypothermia.walkSpeedMultiplier);
        }
    }

    /**
     * Reduces the jump speed of the player.
     * Is only active iff the player has a {@link HypothermiaComponent}.
     */
    @ReceiveEvent
    public void modifyJumpSpeed(AffectJumpForceEvent event, EntityRef player, HypothermiaComponent hypothermia) {
        event.multiply(hypothermia.jumpSpeedMultiplier);
    }

    @ReceiveEvent
    public void hypothermiaLevelChanged(HypothermiaLevelChangedEvent event, EntityRef player,
                                        HypothermiaComponent hypothermia) {
        player.saveComponent(modifySpeedMultipliers(hypothermia, event.getNewValue()));
    }

    private HypothermiaComponent modifySpeedMultipliers(HypothermiaComponent hypothermia, int level) {
        switch (level) {
            case 1:
                hypothermia.walkSpeedMultiplier = 1;
                hypothermia.jumpSpeedMultiplier = 1;
                break;
            case 2:
                hypothermia.walkSpeedMultiplier = 0.7f;
                hypothermia.jumpSpeedMultiplier = 0.7f;
                break;
            case 3:
                hypothermia.walkSpeedMultiplier = 0.5f;
                hypothermia.jumpSpeedMultiplier = 0.6f;
                break;
            default:
                logger.warn("Unexpected Hypothermia Level.");
        }
        return hypothermia;
    }
}
