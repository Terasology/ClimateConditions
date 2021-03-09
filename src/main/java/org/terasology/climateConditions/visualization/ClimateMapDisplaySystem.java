package org.terasology.climateConditions.visualization;

import org.terasology.climateConditions.ClimateConditionsSystem;
import org.terasology.climateConditions.ConditionsBaseField;
import org.terasology.engine.context.Context;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.WorldProvider;

@Share(ClimateMapDisplaySystem.class)
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
            climateConditions.configureHumidity(0, 200, 10, input -> input, 0, 1);
        }
        if (climateConditions.getTemperatureBaseField() == null) {
            climateConditions.configureTemperature(0, 200, 10, input -> input, 0, 1);
        }
    }

    private void setClimateSeed() {
        climateConditions.setWorldSeed(worldProvider.getSeed());
    }

    public ConditionsBaseField getClimateConditionsBase() {
        return base;
    }
    public void setClimateConditionsBase(ConditionsBaseField conditionsBase) { base = conditionsBase; }
    public void setMapHeight(int height) { mapHeight = height; }
    public LocalPlayer getPlayer() {
        return localPlayer;
    }
    public int getMapHeight() {
        return mapHeight;
    }
}
