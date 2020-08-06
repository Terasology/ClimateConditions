
// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.climateConditions.alterationEffects;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;

/**
 * This handles the application of the body temperature alteration effect, which modifies the change in body temperature
 * (based on the magnitude) for a specified duration.
 */
public class BodyTemperatureAlterationEffect implements AlterationEffect {

    public static final String BODY_TEMPERATURE = "BodyTemperature";
    private DelayManager delayManager;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context The context which this effect will be executed on.
     */
    public BodyTemperatureAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * This will apply the body temperature alteration effect on the given entity. This method will send out an event to
     * the other applicable effect systems so that they can contribute with their own body temperature alteration effect
     * related modifiers.
     *
     * @param instigator The entity who applied the body temperature alteration effect.
     * @param entity The entity that the body temperature alteration effect is being applied on.
     * @param id Not applicable for this effect.
     * @param magnitude The magnitude of the body temperature alteration effect.
     * @param duration The duration of the body temperature alteration effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, "", magnitude, duration, TemperatureAlterationCondition.ALWAYS);
    }

    /**
     * This will apply the body temperature alteration effect on the given entity by calling the method {@link
     * #applyEffect(EntityRef, EntityRef, String id, float, long)}.
     *
     * @param instigator The entity who applied the body temperature alteration effect.
     * @param entity The entity that the body temperature alteration effect is being applied on.
     * @param magnitude The magnitude of the body temperature alteration effect.
     * @param duration The duration of the body temperature alteration effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        applyEffect(instigator, entity, "", magnitude, duration, TemperatureAlterationCondition.ALWAYS);
    }

    /**
     * @param instigator The entity who applied the body temperature alteration effect.
     * @param entity The entity that the body temperature alteration effect is being applied on.
     * @param id Not applicable for this effect.
     * @param magnitude The magnitude of the body temperature alteration effect.
     * @param duration The duration of the body temperature alteration effect.
     * @param condition Stores information regarding type of body temperature change alteration - change depends on
     * whether temperature is decreasing or increasing.
     */
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration, TemperatureAlterationCondition condition) {
        // First, determine if the entity already has a swim speed component attached. If so, just replace the speed
        // multiplier, and then save the component. Otherwise, create a new one and attach it to the entity.
        AffectBodyTemperatureComponent affectBodyTemperature =
                entity.getComponent(AffectBodyTemperatureComponent.class);
        if (affectBodyTemperature == null) {
            affectBodyTemperature = new AffectBodyTemperatureComponent();
        }
        affectBodyTemperature.postMultiplier = magnitude;
        affectBodyTemperature.condition = condition;
        entity.addOrSaveComponent(affectBodyTemperature);

        // Send out this event to collect all the duration and magnitude modifiers and multipliers that can affect this
        // effect.
        OnEffectModifyEvent effectModifyEvent = entity.send(new OnEffectModifyEvent(instigator, entity, magnitude, duration, this, id));
        long modifiedDuration = 0;
        boolean modifiersFound = false;

        // If the effect modify event is consumed, don't apply this effect.
        if (!effectModifyEvent.isConsumed()) {
            /*
            Get the magnitude result value and the shortest duration, and assign them to the modifiedMagnitude and
            modifiedDuration respectively.

            The shortest duration is used as the effect modifier associated with that will expire in the shortest
            amount of time, meaning that this effect's total magnitude and next remaining duration will have to be
            recalculated.
            */
            float modifiedMagnitude = effectModifyEvent.getMagnitudeResultValue();
            modifiedDuration = effectModifyEvent.getShortestDuration();

            // If there's at least one duration and magnitude modifier, set the effect's magnitude and the
            // modifiersFound flag.
            if (!effectModifyEvent.getDurationModifiers().isEmpty() && !effectModifyEvent.getMagnitudeModifiers().isEmpty()) {
                affectBodyTemperature.postMultiplier = modifiedMagnitude;
                modifiersFound = true;
            }
        }

        // If the modified duration is between the accepted values (0 and Long.MAX_VALUE), and the base duration is
        // not infinite,
        // add a delayed action to the DelayManager using the new system.
        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE) {
            String effectID = effectModifyEvent.getEffectIDWithShortestDuration();
            delayManager.addDelayedAction(entity,
                    AlterationEffects.EXPIRE_TRIGGER_PREFIX + BODY_TEMPERATURE + "|" + effectID,
                    modifiedDuration);
        }
        // Otherwise, if the duration is greater than 0, there are no modifiers found, and the effect modify event
        // was not consumed,
        // add a delayed action to the DelayManager using the old system.
        else if (duration > 0 && !modifiersFound && !effectModifyEvent.isConsumed()) {
            delayManager.addDelayedAction(entity,
                    AlterationEffects.EXPIRE_TRIGGER_PREFIX + BODY_TEMPERATURE, duration);
        }
        // Otherwise, if there are either no modifiers found, or none of the modifiers collected in the event have
        // infinite
        // duration, remove the component associated with this body temperature alteration effect.
        else if ((!modifiersFound || !effectModifyEvent.getHasInfDuration()) && (duration != AlterationEffects.DURATION_INDEFINITE)) {
            entity.removeComponent(AffectBodyTemperatureComponent.class);
        }
        // If this point is reached and none of the above if-clauses were met, that means there was at least one
        // modifier
        // collected in the event which has infinite duration.
    }
}
