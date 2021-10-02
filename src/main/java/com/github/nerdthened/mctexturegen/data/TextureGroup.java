package com.github.nerdthened.mctexturegen.data;

import java.awt.image.RenderedImage;

/**
 * A data class for storing generated textures.
 */
public final class TextureGroup {

    /** The texture group name. */
    public final String textureGroupName;

    /** All generated textures for this group. */
    public final RenderedImage[] textureImages;

    /**
     * Creates a new TextureGroup from a RenderedImage.
     *
     * @param textureName the texture group name
     * @param textureImage the generated texture image
     */
    public TextureGroup(final String textureName, final RenderedImage textureImage) {
        this(textureName, new RenderedImage[] { textureImage });
    }

    /**
     * Creates a new TextureGroup from an array of RenderedImages.
     *
     * @param textureName the texture group name
     * @param textureImages the generated texture images
     */
    public TextureGroup(final String textureName, final RenderedImage[] textureImages) {
        textureGroupName = textureName;
        this.textureImages = textureImages;
    }

}
