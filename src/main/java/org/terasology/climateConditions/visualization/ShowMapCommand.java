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

package org.terasology.climateConditions.visualization;

import org.terasology.climateConditions.ClimateConditionsSystem;
import org.terasology.climateConditions.ConditionsBaseField;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.world.WorldProvider;

import java.util.function.Function;

import static org.terasology.climateConditions.ConditionsBaseField.HUMIDITY;
import static org.terasology.climateConditions.ConditionsBaseField.TEMPERATURE;

@Share(ShowMapCommand.class)
@RegisterSystem
public class ShowMapCommand extends BaseComponentSystem {
    public static final int SIZE_OF_IMAGE_IN_BLOCKS = 30;

    @In
    private ClimateConditionsSystem climateConditions;
    @In
    private LocalPlayer localPlayer;
    @In
    private NUIManager nuiManager;
    @In
    private WorldProvider worldProvider;

    private String type;
    private int mapHeight;

    private void prepareBases(String mapType) {
        mapHeight = (int) localPlayer.getPosition().y;

        if (climateConditions.getWorldSeed() == null) {
            setClimateSeed();
        }
        if (climateConditions.getTemperatureBaseField() == null) {
            climateConditions.configureTemperature();
        }
        if (climateConditions.getHumidityBaseField() == null) {
            climateConditions.configureHumidity();
        }

        if (mapType.equals(TEMPERATURE)) {
            type = TEMPERATURE;
        } else {
            type = HUMIDITY;
        }
    }

    /**
     * Displays the selected map at the selected height level.
     *
     * The lighter a pixel on the map, the higher temperature/humidity/whatever else.
     * @param mapType
     */
    @Command(shortDescription = "Display condition map", helpText = "Show a given map (humidity, temperature) around the player.")
    public void showClimateMap(@CommandParam("map type") String mapType) {
        prepareBases(mapType);

        nuiManager.pushScreen("ClimateConditions:displayConditionScreen");
    }

    /**
     * Gives the current temperature/humidity/etc. level at the player's position.
     *
     * @param mapType
     * @return condition value
     */
    @Command(shortDescription = "Give condition value", helpText = "Gives the temperature/humidity/etc. of the player at this moment.")
    public float climateValue(@CommandParam("map type") String mapType) {
        prepareBases(mapType);

        if (type.equals(TEMPERATURE)) {
            return climateConditions.getTemperature(localPlayer.getPosition().x, localPlayer.getPosition().y, localPlayer.getPosition().z);
        } else {
            return climateConditions.getHumidity(localPlayer.getPosition().x, localPlayer.getPosition().y, localPlayer.getPosition().z);
        }
    }

    private void setClimateSeed() {
        climateConditions.setWorldSeed(worldProvider.getSeed());
    }

    public ClimateConditionsSystem getClimateConditions() {
        return climateConditions;
    }
    public LocalPlayer getPlayer() {
        return localPlayer;
    }
    public int getMapHeight() {
        return mapHeight;
    }
    public String getType() { return type; }
}
