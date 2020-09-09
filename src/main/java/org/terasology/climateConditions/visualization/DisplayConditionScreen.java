// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.climateConditions.visualization;

import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.CoreScreenLayer;

public class DisplayConditionScreen extends CoreScreenLayer {
    @In
    private ClimateMapDisplaySystem climateSystem;

    @Override
    public void initialise() {
        ClimateMapWidget climateMapWidget = find("climateMap", ClimateMapWidget.class);
        climateMapWidget.setShowMapCommand(climateSystem);
    }
}
