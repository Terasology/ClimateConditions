package org.terasology.climateConditions.visualization;

import com.google.common.base.Function;
import org.slf4j.LoggerFactory;
import org.terasology.climateConditions.ClimateConditionsSystem;
import org.terasology.climateConditions.ConditionsBaseField;
import org.terasology.context.Context;
import org.terasology.engine.SimpleUri;
import org.terasology.engine.paths.PathManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.world.WorldProvider;

@RegisterSystem
public class ShowMapCommand extends BaseComponentSystem {
    @In
    private ClimateConditionsSystem climateConditions;
    @In
    private LocalPlayer localPlayer;
    @In
    private NUIManager nuiManager;
    @In
    private Context context;
    @In
    private WorldProvider worldProvider;

    public static final int SIZE_OF_IMAGE = 300;

    private ConditionsBaseField base;
    private int mapHeight;

    @Override
    public void postBegin() {
        context.put(ShowMapCommand.class, this);
    }

    /**
     * Displays the selected map at the selected height level.
     *
     * The lighter a pixel on the map, the higher temperature/humidity/whatever else.
     * @param mapType
     */
    @Command(shortDescription = "Display condition map", helpText = "Show a given map (humidity, temperature) at a given height level.")
    public void showMap(@CommandParam("map type") String mapType, @CommandParam("height to look at") int height) {
        mapHeight = height;

        if (climateConditions.getWorldSeed() == null) {
            setClimateSeed();
        }

        if (climateConditions.getTemperatureBaseField() == null) {
            initializeClimateTemperature();
        } else if (climateConditions.getHumidityBaseField() == null) {
            initializeClimateHumidity();
        }

        if (mapType.equals("temperature")) {
            base = climateConditions.getTemperatureBaseField();
        } else {
            base = climateConditions.getHumidityBaseField();
        }

        /*BufferedImage image = new BufferedImage(SIZE_OF_IMAGE, SIZE_OF_IMAGE, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < SIZE_OF_IMAGE; i++) {
            for (int j = 0; j < SIZE_OF_IMAGE; j++) {
                int color = (int) (255 * base.get(localPlayer.getPosition().x - (SIZE_OF_IMAGE / 2) + i, height, localPlayer.getPosition().z - (SIZE_OF_IMAGE / 2) + j));
                image.setRGB(i, j, new Color(color, color, color).getRGB());
            }
        }
        try {*/
            nuiManager.pushScreen("ClimateConditions:displayConditionScreen");
           // DisplayConditionScreen screen = context.get(DisplayConditionScreen.class);
           // screen.setMapName(mapType.toUpperCase() + " map");
           /* screen.setMapImage(Assets.getTextureRegion("ClimateConditions:mapTexture").get());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void initializeClimateHumidity() {
        climateConditions.configureHumidity(0, 200, 10, new Function<Float, Float>() {
            @Override
            public Float apply(Float input) {
                return input;
            }
        }, 0, 1);
    }
    private void initializeClimateTemperature() {
        LoggerFactory.getLogger(this.getClass()).info("humidity: "+climateConditions.getHumidityBaseField());
        climateConditions.configureTemperature(0, 200, 10, new Function<Float, Float>() {
            @Override
            public Float apply(Float input) {
                return input;
            }
        }, 0, 1);
    }
    private void setClimateSeed() {
        climateConditions.setWorldSeed(worldProvider.getSeed());
    }

    public ConditionsBaseField getClimateConditionsBase() {
        return base;
    }
    public LocalPlayer getPlayer() {
        return localPlayer;
    }
    public int getMapHeight() {
        return mapHeight;
    }
}
