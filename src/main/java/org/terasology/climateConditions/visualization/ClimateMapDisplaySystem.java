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

    private ConditionsBaseField base;
    private int mapHeight;

    /**
     * Configures temperature/humidity if necessary.
     */
    // TODO: change the initialization so that maps reflect the corresponding world location
    public void initialise() {
        setClimateSeed();

        if (climateConditions.getHumidityBaseField() == null) {
            climateConditions.configureHumidity(0, 10, 0, 1);
        }
        if (climateConditions.getTemperatureBaseField() == null) {
            climateConditions.configureTemperature(0, 10, 0, 1);
        }
    }

    private void setClimateSeed() {
        climateConditions.setWorldSeed(worldProvider.getSeed());
    }

    public ClimateConditionsSystem getClimateConditions() { return climateConditions; }
    public LocalPlayer getPlayer() {
        return localPlayer;
    }
}
