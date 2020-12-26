// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.climateConditions.igloo;

import org.terasology.world.block.BlockRegion;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.facets.base.SparseObjectFacet3D;

/**
 * Used to place Igloos in the Snow Biome by the {@link IglooProvider} and {@link IglooRasterizer}.
 */
public class IglooFacet extends SparseObjectFacet3D<Igloo> {
    public IglooFacet(BlockRegion targetRegion, Border3D border) {
        super(targetRegion, border);
    }
}
