// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.climateConditions.visualization;

import org.terasology.climateConditions.ClimateConditionsSystem;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.console.commandSystem.annotations.Command;
import org.terasology.engine.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;

@RegisterSystem
public class ShowMapCommand extends BaseComponentSystem {
    public static final int SIZE_OF_IMAGE = 300;

    @In
    ClimateMapDisplaySystem climateSystem;

    @In
    private ClimateConditionsSystem climateConditions;

    @In
    private NUIManager nuiManager;

    /**
     * Displays the selected map at the selected height level.
     *
     * The lighter a pixel on the map, the higher temperature/humidity/whatever else.
     * @param mapType
     */
    @Command(shortDescription = "Display condition map", helpText = "Show a given map (humidity, temperature) at a given height level.")
    public void showClimateMap(@CommandParam("map type") String mapType, @CommandParam("height to look at") int height) {
        climateSystem.setMapHeight(height);

        if (mapType.equals("temperature")) {
            climateSystem.setClimateConditionsBase(climateConditions.getTemperatureBaseField());
        } else {
            climateSystem.setClimateConditionsBase(climateConditions.getHumidityBaseField());
        }

        nuiManager.pushScreen("ClimateConditions:displayConditionScreen");
    }
}
