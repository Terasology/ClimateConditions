package org.terasology.climateConditions.visualization;

import org.terasology.biomesAPI.BiomeRegistry;
import org.terasology.climateConditions.ClimateConditionsSystem;
import org.terasology.climateConditions.ConditionsBaseField;
import org.terasology.context.Context;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;

import static org.terasology.climateConditions.ConditionsBaseField.TEMPERATURE;

@RegisterSystem
public class ClimateMapDisplaySystem extends BaseComponentSystem {

    @In
    private ClimateConditionsSystem climateConditions;
    @In
    private LocalPlayer localPlayer;
    @In
    private Context context;
    @In
    private WorldProvider worldProvider;
    @In
    private BiomeRegistry biomeManager;

    private ConditionsBaseField base;
    private int mapHeight;

    /**
     * Configures temperature/humidity if necessary.
     */
    // TODO: change the initialization so that maps reflect the corresponding world location
    public void initialise() {
        setClimateSeed();

        if (climateConditions.getHumidityBaseField() == null) {
            climateConditions.configureHumidity(0, 10, 0, 1, worldProvider, biomeManager);
        }
        if (climateConditions.getTemperatureBaseField() == null) {
            climateConditions.configureTemperature(0, 10, 0, 1, worldProvider, biomeManager);
        }
    }

    private void setClimateSeed() {
        climateConditions.setWorldSeed(worldProvider.getSeed());
    }

    public ConditionsBaseField getClimateConditionsBase() {
        return base;
    }
    public void setClimateConditionsBase(String typeOfBase) {
        if (typeOfBase.equals(TEMPERATURE)) {
            base = climateConditions.getTemperatureBaseField();
        } else {
            base = climateConditions.getHumidityBaseField();
        }
    }
    public void setMapHeight(int height) { mapHeight = height; }
    public LocalPlayer getPlayer() {
        return localPlayer;
    }
    public int getMapHeight() {
        return mapHeight;
    }
}
