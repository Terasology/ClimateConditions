// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.climateConditions.igloo;

import org.terasology.core.world.CoreBiome;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.math.Region3i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetBorder;
import org.terasology.world.generation.FacetProviderPlugin;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.SeaLevelFacet;
import org.terasology.world.generation.facets.SurfacesFacet;
import org.terasology.world.generator.plugin.RegisterPlugin;

@RegisterPlugin
@Requires({
        @Facet(value = SurfacesFacet.class, border = @FacetBorder(sides = Igloo.SIZE * 2, bottom = 1)),
        @Facet(value = BiomeFacet.class, border = @FacetBorder(sides = Igloo.SIZE * 2)),
        @Facet(value = SeaLevelFacet.class, border = @FacetBorder(sides = Igloo.SIZE * 2))
})
@Produces(IglooFacet.class)
public class IglooProvider implements FacetProviderPlugin {
    private Noise noise;
    private static final int ARBITRARY_OVERLAP_OFFSET = 3;
    private static final int SNOW_BIOME_THRESHOLD = 96;
    //TODO: Get the snow biome threshold from the SolidRasterizer in CoreWorlds.

    @Override
    public void setSeed(long seed) {
        noise = new WhiteNoise(seed + ARBITRARY_OVERLAP_OFFSET);
    }

    /**
     * Places the Igloo Structure in the Snow Biome with a very low probability of spawn.
     */
    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(IglooFacet.class).extendBy(0, Igloo.SIZE * 2,
                Igloo.SIZE * 2);

        IglooFacet facet = new IglooFacet(region.getRegion(), border);
        SurfacesFacet surfaceHeightFacet = region.getRegionFacet(SurfacesFacet.class);
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        BiomeFacet biomeFacet = region.getRegionFacet(BiomeFacet.class);

        Region3i worldRegion = surfaceHeightFacet.getWorldRegion();

        for (int wz = worldRegion.minZ(); wz <= worldRegion.maxZ(); wz++) {
            for (int wx = worldRegion.minX(); wx <= worldRegion.maxX(); wx++) {
                for (int surfaceHeight : surfaceHeightFacet.getWorldColumn(wx, wz)) {
                    int seaLevel = seaLevelFacet.getSeaLevel();
                    // check if height is within this region
                    if (surfaceHeight >= facet.getWorldRegion().minY() &&
                            surfaceHeight <= facet.getWorldRegion().maxY()) {
                        // Sea Level + 96 is the height at which snow blocks are placed in all biomes.
                        if (noise.noise(wx, surfaceHeight, wz) > 0.9999f && (surfaceHeight >= seaLevel + SNOW_BIOME_THRESHOLD ||
                                biomeFacet.getWorld(wx, wz).getId().equals(CoreBiome.SNOW))) {
                            facet.setWorld(wx, surfaceHeight, wz, new Igloo());
                        }
                    }
                }
            }
        }
        region.setRegionFacet(IglooFacet.class, facet);
    }
}
