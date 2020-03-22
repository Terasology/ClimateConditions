package org.terasology.climateConditions.visualization;

import org.slf4j.LoggerFactory;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreWidget;

import static org.terasology.climateConditions.visualization.ShowMapCommand.SIZE_OF_IMAGE;

public class ClimateMapWidget extends CoreWidget {
    private ShowMapCommand showMapCommand;

    @Override
    public void onDraw(Canvas canvas) {
        if (showMapCommand != null) {
            canvas.drawFilledRectangle(Rect2i.createFromMinAndMax(new Vector2i(0, 0), new Vector2i(SIZE_OF_IMAGE, SIZE_OF_IMAGE)), Color.WHITE);
            for (int i = 0; i < SIZE_OF_IMAGE; i++) {
                for (int j = 0; j < SIZE_OF_IMAGE; j++) {
                    float color = (showMapCommand.getClimateConditionsBase().get(showMapCommand.getPlayer().getPosition().x -
                            (SIZE_OF_IMAGE / 2) + i, showMapCommand.getMapHeight(), showMapCommand.getPlayer().getPosition().z - (SIZE_OF_IMAGE / 2) + j));
                    canvas.drawLine(i, j, i+1, j+1, new Color(color, color, color));
                }
            }
        }
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return new Vector2i(SIZE_OF_IMAGE, SIZE_OF_IMAGE);
    }

    public void setShowMapCommand(ShowMapCommand command) {
        showMapCommand = command;
        LoggerFactory.getLogger("").info("3: "+showMapCommand);
    }
}
