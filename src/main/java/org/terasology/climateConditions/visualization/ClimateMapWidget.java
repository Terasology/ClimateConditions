// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.climateConditions.visualization;

import org.terasology.math.JomlUtil;
import org.joml.Vector2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.CoreWidget;

import static org.terasology.climateConditions.visualization.ShowMapCommand.SIZE_OF_IMAGE;

public class ClimateMapWidget extends CoreWidget {
    private ClimateMapDisplaySystem climateSystem;

    /**
     * Converts the base climate condition values to a color, and draws them on the canvas.
     * @param canvas The canvas that the map is drawn on.
     */
    @Override
    public void onDraw(Canvas canvas) {
        if (climateSystem != null) {
            canvas.drawFilledRectangle(JomlUtil.rectangleiFromMinAndSize(0, 0, SIZE_OF_IMAGE, SIZE_OF_IMAGE), Color.WHITE);
            for (int i = 0; i < SIZE_OF_IMAGE; i++) {
                for (int j = 0; j < SIZE_OF_IMAGE; j++) {
                    Vector3f playerPosition = climateSystem.getPlayer().getPosition();
                    int height = climateSystem.getMapHeight();
                    int offsetZ =  -(SIZE_OF_IMAGE / 2) + j;
                    int offsetX = -(SIZE_OF_IMAGE / 2) + i;
                    float color = climateSystem.getClimateConditionsBase().get(playerPosition.x + offsetX, height, playerPosition.z + offsetZ);
                    canvas.drawLine(i, j, i + 1, j + 1, new Color(color, color, color));
                }
            }
        }
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return new Vector2i(SIZE_OF_IMAGE, SIZE_OF_IMAGE);
    }

    public void setShowMapCommand(ClimateMapDisplaySystem command) {
        climateSystem = command;
    }
}
