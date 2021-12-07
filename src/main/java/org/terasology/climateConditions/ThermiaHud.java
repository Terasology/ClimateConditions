// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.climateConditions;

import org.terasology.engine.rendering.assets.texture.TextureRegion;
import org.terasology.engine.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.nui.widgets.UIImage;
import org.terasology.nui.widgets.UILabel;


/**
 * The UI class for the Thermia HUD used to convey extreme body temperatures.
 */
public class ThermiaHud extends CoreHudWidget {

    private UILabel label;
    private UIImage image;

    @Override
    public void initialise() {
        this.setVisible(false);
        label = find("thermiaLevelLabel", UILabel.class);
        image = find("thermiaIcon", UIImage.class);
    }

    /**
     * Used to set the the text label in the thermia UIBox equal to the level of the respective thermia.
     */
    public void setLabelText(int amount) {
        label.setText(String.valueOf(amount));
    }

    /**
     * Used to set the image in the thermia UIBox to the respective thermia icon.
     */
    public void setImage(TextureRegion newImage) {
        image.setImage(newImage);
    }
}
