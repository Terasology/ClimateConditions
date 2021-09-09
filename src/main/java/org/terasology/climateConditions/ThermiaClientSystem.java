// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.climateConditions;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.assets.texture.TextureRegion;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.utilities.Assets;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * Manages the player's Thermia UI used to display the hypo/hyperthermia icons in case of extreme body temperatures.
 */
@RegisterSystem(RegisterMode.CLIENT)
public class ThermiaClientSystem extends BaseComponentSystem {

    @In
    private NUIManager nuiManager;

    private ThermiaHud thermiaHud;

    public void postBegin() {
        thermiaHud = (ThermiaHud) nuiManager.getHUD().addHUDElement("thermiaHud");
    }

    /**
     * Deals with the Hypothermia level indication in the thermia Hud.
     */
    @ReceiveEvent
    public void onHypothermiaLevelChanged(HypothermiaLevelChangedEvent event, EntityRef character) {
        if (event.getNewValue() > 0) {
            thermiaHud.setVisible(true);
            thermiaHud.setLabelText(event.getNewValue());
            if (event.getOldValue() == 0) {
                TextureRegion hypothermiaIcon = Assets.getTexture("ClimateConditions:hypothermia").get();
                thermiaHud.setImage(hypothermiaIcon);
            }
        } else {    // newValue = 0 and body temperature has returned to the normal range
            thermiaHud.setVisible(false);
        }
    }

    /**
     * Deals with the Hyperthermia level indication in the thermia Hud.
     */
    @ReceiveEvent
    public void onHyperthermiaLevelChanged(HyperthermiaLevelChangedEvent event, EntityRef character) {
        if (event.getNewValue() > 0) {
            thermiaHud.setVisible(true);
            thermiaHud.setLabelText(event.getNewValue());
            if (event.getOldValue() == 0) {
                TextureRegion hyperthermiaIcon = Assets.getTexture("ClimateConditions:hyperthermia").get();
                thermiaHud.setImage(hyperthermiaIcon);
            }
        } else {    // newValue = 0 and body temperature has returned to the normal range
            thermiaHud.setVisible(false);
        }
    }
}
