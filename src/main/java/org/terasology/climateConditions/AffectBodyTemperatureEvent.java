/*
 * Copyright 2020 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.climateConditions;

import org.terasology.entitySystem.event.AbstractValueModifiableEvent;

/**
 * This event is sent out by the {@link BodyTemperatureSystem} to allow for other systems to
 * modify change in body temperature.
 */
public class AffectBodyTemperatureEvent extends AbstractValueModifiableEvent {
    public AffectBodyTemperatureEvent(float baseValue) {
        super(baseValue);
    }
}

