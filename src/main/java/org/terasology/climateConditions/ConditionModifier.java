// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.climateConditions;

public interface ConditionModifier {
    float getCondition(float value, float x, float y, float z);
}
