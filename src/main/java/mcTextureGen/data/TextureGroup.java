package mcTextureGen.data;

import java.awt.image.RenderedImage;

public final class TextureGroup {

    public final String textureGroupName;
    public final RenderedImage[] textureImages;

    public TextureGroup(final String textureName, final RenderedImage textureImage) {
        this(textureName, new RenderedImage[] { textureImage });
    }

    public TextureGroup(final String textureName, final RenderedImage[] textureImages) {
        textureGroupName = textureName;
        this.textureImages = textureImages;
    }

}
