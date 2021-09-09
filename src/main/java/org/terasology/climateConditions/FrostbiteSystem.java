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

import org.terasology.alterationEffects.speed.StunAlterationEffect;
import org.terasology.engine.audio.StaticSound;
import org.terasology.engine.audio.events.PlaySoundEvent;
import org.terasology.engine.context.Context;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.prefab.PrefabManager;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.CharacterSoundComponent;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.logic.delay.PeriodicActionTriggeredEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.module.health.events.DoDamageEvent;

/**
 * Adds frostbite to the player.
 * Frostbite is a periodic effect that, e.g., damages and stuns the player.
 * Is only active iff the player has a {@link HypothermiaComponent} level 3 or greater.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class FrostbiteSystem extends BaseComponentSystem {
    public static final String FROSTBITE_DAMAGE_ACTION_ID = "Frostbite Damage";

    @In
    private DelayManager delayManager;
    @In
    private PrefabManager prefabManager;
    @In
    private Context context;

    private static final int healthDecreaseInterval = 20000;
    private static final int initialDelay = 5000;
    private static final int healthDecreaseAmount = 15;
    private Random random = new FastRandom();

    /**
     * Responsible for adding and removing frostbite effect according to changes in Hypothermia Levels.
     */
    @ReceiveEvent
    public void hypothermiaLevelChanged(HypothermiaLevelChangedEvent event, EntityRef player) {
        int oldLevel = event.getOldValue();
        int newLevel = event.getNewValue();
        //Frostbite Effect remains active for Hypothermia Levels 3 and greater.
        if (newLevel == 3 && oldLevel < newLevel) {
            applyFrostbite(player);
        } else if (oldLevel == 3 && oldLevel > newLevel) {
            removeFrostbite(player);
        }
    }

    private void applyFrostbite(EntityRef player) {
        delayManager.addPeriodicAction(player, FROSTBITE_DAMAGE_ACTION_ID, initialDelay, healthDecreaseInterval);
    }

    private void removeFrostbite(EntityRef player) {
        delayManager.cancelPeriodicAction(player, FROSTBITE_DAMAGE_ACTION_ID);
    }

    @ReceiveEvent
    public void onPeriodicFrostbite(PeriodicActionTriggeredEvent event, EntityRef player,
                                    HypothermiaComponent hypothermia) {
        if (event.getActionId().equals(FROSTBITE_DAMAGE_ACTION_ID)) {
            applyFrostbiteDamagePlayer(player);
            applyStunEffect(player, 1000);
            playFrostbiteSound(player);
        }
    }

    private void applyFrostbiteDamagePlayer(EntityRef player) {
        Prefab frostbiteDamagePrefab = prefabManager.getPrefab("ClimateConditions:FrostbiteDamage");
        player.send(new DoDamageEvent(healthDecreaseAmount, frostbiteDamagePrefab));
    }

    private void applyStunEffect(EntityRef player, int duration) {
        StunAlterationEffect stunAlterationEffect = new StunAlterationEffect(context);
        //Both the instigator and the target is the player
        //the magnitude parameter is not used by StunAlterationEffect
        stunAlterationEffect.applyEffect(player, player, 0, duration);
    }

    public void playFrostbiteSound(EntityRef entity) {
        CharacterSoundComponent characterSounds = entity.getComponent(CharacterSoundComponent.class);
        if (characterSounds != null && characterSounds.deathSounds.size() > 0) {
            StaticSound sound = random.nextItem(characterSounds.deathSounds);
            entity.send(new PlaySoundEvent(entity, sound, characterSounds.deathVolume));
        }
    }
}
