package org.terasology.climateConditions.visualization;

import org.slf4j.LoggerFactory;
import org.terasology.context.Context;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.widgets.UILabel;

public class DisplayConditionScreen extends CoreScreenLayer {
    @In
    private Context context;

    private String mapName;

    private UILabel UIMapName;
    private ClimateMapWidget climateMapWidget;

    public void setShowMapCommand(ShowMapCommand command) {
        climateMapWidget.setShowMapCommand(command);
    }

    @Override
    public void initialise() {
        climateMapWidget = find("climateMap", ClimateMapWidget.class);

        LoggerFactory.getLogger("").info("1: "+climateMapWidget);
        LoggerFactory.getLogger("").info("2: "+context);
        climateMapWidget.setShowMapCommand(context.get(ShowMapCommand.class));
    }
}
