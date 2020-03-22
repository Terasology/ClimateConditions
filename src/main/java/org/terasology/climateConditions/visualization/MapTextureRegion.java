package org.terasology.climateConditions.visualization;

import org.terasology.math.geom.Rect2f;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureData;
import org.terasology.rendering.assets.texture.TextureRegion;

public class MapTextureRegion { /*implements TextureRegion {
    private TextureData texData;
    private Texture texture = new Texture() {
        @Override
        public Texture.WrapMode getWrapMode() {
            return Texture.WrapMode.CLAMP;
        }

        @Override
        public Texture.FilterMode getFilterMode() {
            return Texture.FilterMode.LINEAR;
        }

        @Override
        public TextureData getData() {
            return texData;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public int getDepth() {
            return 0;
        }

        @Override
        public boolean isLoaded() {
            return true;
        }

        @Override
        public void subscribeToDisposal(Runnable subscriber) {

        }

        @Override
        public void unsubscribeToDisposal(Runnable subscriber) {

        }

        @Override
        public Texture getTexture() {
            return this;
        }

        @Override
        public Rect2f getRegion() {
            return Texture.FULL_TEXTURE_REGION;
        }

        @Override
        public Rect2i getPixelRegion() {
            return null;
        }

        @Override
        public int getWidth() {
            return (int) Texture.FULL_TEXTURE_REGION.width();
        }

        @Override
        public int getHeight() {
            return (int) Texture.FULL_TEXTURE_REGION.height();
        }

        @Override
        public Vector2i size() {
            return new Vector2i(Texture.FULL_TEXTURE_REGION.size().x, Texture.FULL_TEXTURE_REGION.size().y);
        }
    };
    public MapTextureRegion(TextureData data) {
        texData = data;
    }
    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public Rect2f getRegion() {
        return texture.getRegion();
    }

    @Override
    public Rect2i getPixelRegion() {
        return texture.getPixelRegion();
    }

    @Override
    public int getWidth() {
        return texture.getWidth();
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    @Override
    public Vector2i size() {
        return texture.size();
    }*/
}
