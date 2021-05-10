/*
 * Copyright 2020 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.climateConditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.AffectJumpForceEvent;
import org.terasology.engine.logic.characters.GetMaxSpeedEvent;
import org.terasology.module.health.components.HealthComponent;
import org.terasology.module.health.events.BeforeRegenEvent;
import org.terasology.module.health.events.ChangeMaxHealthEvent;
import org.terasology.thirst.event.AffectThirstEvent;

import static org.terasology.module.health.core.BaseRegenAuthoritySystem.BASE_REGEN;

/**
 * Handles effects related to Hyperthermia. Hyperthermia occurs in case of extremely high body temperatures and, e.g.,
 * slows the player's movements. For adding new effects in existing or new Hyperthermia Levels, {@link
 * HyperthermiaLevelChangedEvent} should be reacted to either in this or a separate authority system for eg. {@link
 * FrostbiteSystem}, a hypothermia effect.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class HyperthermiaSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(HyperthermiaSystem.class);

    /**
     * Reduces the walking/running speed of the player. Is only active iff the player has a {@link
     * HyperthermiaComponent}.
     */
    @ReceiveEvent
    public void modifySpeed(GetMaxSpeedEvent event, EntityRef player, HyperthermiaComponent hyperthermia) {
        event.multiply(hyperthermia.walkSpeedMultiplier);
    }

    /**
     * Reduces the jump speed of the player. Is only active iff the player has a {@link HyperthermiaComponent}.
     */
    @ReceiveEvent
    public void modifyJumpSpeed(AffectJumpForceEvent event, EntityRef player, HyperthermiaComponent hyperthermia) {
        event.multiply(hyperthermia.jumpSpeedMultiplier);
    }

    /**
     * Increases the thirst decay per second of the player. Is only active iff the player has a {@link
     * HyperthermiaComponent}.
     */
    @ReceiveEvent
    public void modifyThirst(AffectThirstEvent event, EntityRef player, HyperthermiaComponent hyperthermia) {
        event.multiply(hyperthermia.thirstMultiplier);
    }

    /**
     * Weakens the player by reducing the maxHealth and regeneration of the player.
     */
    private void applyWeakening(EntityRef player, HealthComponent health, HyperthermiaComponent hyperthermia) {
        player.send(new ChangeMaxHealthEvent(hyperthermia.maxHealthMultiplier * health.maxHealth));
        health.currentHealth = Math.min(health.currentHealth, health.maxHealth);
        player.saveComponent(health);
    }

    /**
     * Applies the hypothermia regen multiplier to the base regeneration for an entity if their hypothermia level is at
     * the maximum. This only affects the base regeneration action. All other registered regeneration actions are
     * ignored.
     *
     * @param event The collector event for regeneration actions, called before an entity's health is about to
     *         be regenerated.
     * @param entity The entity whose health is about to be regenerated.
     * @param hyperthermia The entity's hyperthermia configuration.
     */
    @ReceiveEvent
    public void beforeBaseRegen(BeforeRegenEvent event, EntityRef entity, HyperthermiaComponent hyperthermia) {
        if (event.getId().equals(BASE_REGEN)) {
            // TODO: Ideally, we should handle this the same as the other modifiers:
            //  If hyperthermia is active (i.e. HyperthermiaComponent is present), only apply the multiplier here and
            //  let the modifyHyperthermiaMultipliers adjust the multipliers.
            if (hyperthermia.level >= 3) {
                event.multiply(hyperthermia.regenMultiplier);
            }
        }
    }

    /**
     * Reverts the player weakening by restoring the maxHealth and regeneration of the player to the original value.
     */
    private void revertWeakening(EntityRef player, HealthComponent health, HyperthermiaComponent hyperthermia) {
        player.send(new ChangeMaxHealthEvent(player.getParentPrefab().getComponent(HealthComponent.class).maxHealth));
        player.saveComponent(health);
    }

    @ReceiveEvent
    public void hyperthermiaLevelChanged(HyperthermiaLevelChangedEvent event, EntityRef player,
                                         HyperthermiaComponent hyperthermia, HealthComponent health) {
        int oldLevel = event.getOldValue();
        int newLevel = event.getNewValue();
        player.saveComponent(modifyHyperthermiaMultipliers(hyperthermia, newLevel));
        //Weakening effect remains active for Hyperthermia levels 3 and greater.
        if (newLevel == 3 && oldLevel < newLevel) {
            applyWeakening(player, health, hyperthermia);
        } else if (oldLevel == 3 && oldLevel > newLevel) {
            revertWeakening(player, health, hyperthermia);
        }
    }

    private HyperthermiaComponent modifyHyperthermiaMultipliers(HyperthermiaComponent hyperthermia, int level) {
        switch (level) {
            case 1:
                hyperthermia.walkSpeedMultiplier = 1;
                hyperthermia.jumpSpeedMultiplier = 1;
                hyperthermia.thirstMultiplier = 1.5f;
                break;
            case 2:
                hyperthermia.walkSpeedMultiplier = 0.7f;
                hyperthermia.jumpSpeedMultiplier = 0.85f;
                hyperthermia.thirstMultiplier = 2f;
                break;
            case 3:
                hyperthermia.walkSpeedMultiplier = 0.6f;
                hyperthermia.jumpSpeedMultiplier = 0.7f;
                hyperthermia.thirstMultiplier = 2.25f;
                break;
            default:
                logger.warn("Unexpected Hyperthermia Level.");
        }
        return hyperthermia;
    }
}
