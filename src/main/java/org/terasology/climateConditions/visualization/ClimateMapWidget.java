// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.climateConditions.visualization;

import org.joml.Rectanglei;
import org.terasology.math.JomlUtil;
import org.joml.Vector2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.CoreWidget;

import static org.terasology.climateConditions.ConditionsBaseField.TEMPERATURE;
import static org.terasology.climateConditions.visualization.ShowMapCommand.SIZE_OF_IMAGE_IN_BLOCKS;

public class ClimateMapWidget extends CoreWidget {
    private ShowMapCommand climateSystem;
    private final int SIZE_OF_CANVAS = 300;

    /**
     * Converts the base climate condition values to a color, and draws them on the canvas.
     * @param canvas The canvas that the map is drawn on.
     */
    @Override
    public void onDraw(Canvas canvas) {
        if (climateSystem != null) {
            int eachColor = SIZE_OF_CANVAS / SIZE_OF_IMAGE_IN_BLOCKS; //size of image divided by the number of blocks along one side of it
            canvas.drawFilledRectangle(JomlUtil.rectangleiFromMinAndSize(0, 0, SIZE_OF_IMAGE_IN_BLOCKS, SIZE_OF_IMAGE_IN_BLOCKS), Color.WHITE);
            for (int i = 0; i < SIZE_OF_IMAGE_IN_BLOCKS; i++) {
                for (int j = 0; j < SIZE_OF_IMAGE_IN_BLOCKS; j++) {
                    Vector3f playerPosition = climateSystem.getPlayer().getPosition();
                    int height = climateSystem.getMapHeight();
                    int offsetZ =  - (SIZE_OF_IMAGE_IN_BLOCKS / 2) + j;
                    int offsetX = - (SIZE_OF_IMAGE_IN_BLOCKS / 2) + i;

                    float color;
                    Vector3f vector = new Vector3f(playerPosition.x + offsetX, height, playerPosition.z + offsetZ);
                    if (climateSystem.getType().equals(TEMPERATURE)) {
                        color = climateSystem.getClimateConditions().getTemperature(vector);
                    } else {
                        color = climateSystem.getClimateConditions().getHumidity(vector);
                    }
                    canvas.drawFilledRectangle(new Rectanglei(i * eachColor, j * eachColor, i * eachColor + eachColor, j * eachColor + eachColor), new Color(color, color, color));
                }
            }
        }
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return new Vector2i(SIZE_OF_CANVAS, SIZE_OF_CANVAS);
    }

    public void setClimateSystem(ShowMapCommand system) {
        climateSystem = system;
    }
}
