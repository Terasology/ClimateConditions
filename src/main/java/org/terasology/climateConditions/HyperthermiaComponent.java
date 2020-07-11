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

import org.terasology.entitySystem.Component;

public class HyperthermiaComponent implements Component {
    public float walkSpeedMultiplier = 0.7f;
    public float jumpSpeedMultiplier = 0.85f;
    public float regenMultiplier = 0.8f;
    public float maxHealthMultiplier = 0.8f;
    public float thirstMultiplier = 2f;
    public float hyperthermiaLevel;
    //Higher the value of the modifier, more dangerous the effects of Hyperthermia.
    public float allEffectModifier;

    HyperthermiaComponent() {
        hyperthermiaLevel = 1;
        allEffectModifier = 1;
    }

    HyperthermiaComponent(int level) {
        hyperthermiaLevel = level;
        allEffectModifier = level;
    }

    public float getEffectiveWalkSpeedMultiplier() {
        return walkSpeedMultiplier / allEffectModifier;
    }

    public float getEffectiveJumpSpeedMultiplier() {
        return jumpSpeedMultiplier / allEffectModifier;
    }

    public float getEffectiveMaxHealthMultiplier() {
        return maxHealthMultiplier / allEffectModifier;
    }

    public float getEffectiveRegenMultiplier() {
        return regenMultiplier / allEffectModifier;
    }

    public float getEffectiveThirstMultiplier() {
        return thirstMultiplier * allEffectModifier;
    }

    public float getAllEffectModifier() {
        return allEffectModifier;
    }
}
