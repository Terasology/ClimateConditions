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

import org.joml.Vector3f;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.logic.delay.PeriodicActionTriggeredEvent;
import org.terasology.engine.logic.location.Location;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.particles.components.generators.VelocityRangeGeneratorComponent;
import org.terasology.engine.registry.In;

/**
 * Adds a {@link VisibleBreathComponent} to the player.
 * Visible breath is a periodic action adding a visible breath particle effect.
 * Is active only iff the player has a {@link HypothermiaComponent}
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class VisibleBreathingSystem extends BaseComponentSystem {
    public static final String VISIBLE_BREATH_ACTION_ID = "Visible Breath";

    @In
    private EntityManager entityManager;
    @In
    private DelayManager delayManager;

    private int initialDelay = 5000;
    private int breathInterval = 7000;


    @ReceiveEvent(components = {HypothermiaComponent.class})
    public void onHypothermia(OnAddedComponent event, EntityRef player) {
        delayManager.addPeriodicAction(player, VisibleBreathingSystem.VISIBLE_BREATH_ACTION_ID, initialDelay,
            breathInterval);
    }

    @ReceiveEvent(components = HypothermiaComponent.class)
    public void beforeRemoveHypothermia(BeforeRemoveComponent event, EntityRef player) {
        delayManager.cancelPeriodicAction(player, VisibleBreathingSystem.VISIBLE_BREATH_ACTION_ID);
    }

    @ReceiveEvent(components = {HypothermiaComponent.class})
    public void onPeriodicBreath(PeriodicActionTriggeredEvent event, EntityRef player, LocationComponent location) {
        if (event.getActionId().equals(VISIBLE_BREATH_ACTION_ID)) {
            updateVisibleBreathEffect(player, location);
        }
    }

    private void updateVisibleBreathEffect(EntityRef player, LocationComponent targetLoc) {
        EntityRef particleEntity = entityManager.create("climateConditions:VisibleBreathEffect");
        LocationComponent childLoc = particleEntity.getComponent(LocationComponent.class);
        childLoc.setWorldPosition(targetLoc.getWorldPosition(new Vector3f()));
        Location.attachChild(player, particleEntity);
        particleEntity.setOwner(player);
        Vector3f direction = targetLoc.getLocalDirection(new Vector3f());
        direction.normalize();
        particleEntity.upsertComponent((VelocityRangeGeneratorComponent.class), maybeComponent -> {
            VelocityRangeGeneratorComponent velocity = maybeComponent.orElse(new VelocityRangeGeneratorComponent());
            direction.mul(0.5f);
            direction.y += 0.5f;
            velocity.minVelocity = new Vector3f(direction);
            direction.mul(1.5f);
            velocity.maxVelocity = new Vector3f(direction);
            return velocity;
        });
        player.upsertComponent((VisibleBreathComponent.class), maybeComponent -> {
            VisibleBreathComponent component = maybeComponent.orElse(new VisibleBreathComponent());
            component.particleEntity = particleEntity;
            return component;
        });
    }
}
