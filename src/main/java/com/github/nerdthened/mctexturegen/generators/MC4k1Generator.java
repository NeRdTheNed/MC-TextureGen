package com.github.nerdthened.mctexturegen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import com.github.nerdthened.mctexturegen.data.TextureGroup;

/**
 * This class generates the only textures used by Minecraft 4k-1 (the "XOR fractal" textures).
 */
public final class MC4k1Generator extends AbstractTextureGenerator {

    /** How many light levels to generate textures for. */
    private static final int MAX_LIGHT_LEVEL = 2;

    /**
     * Generates Minecraft 4k-1's "XOR fractal" textures, for each light level.
     *
     * @todo   this code is unreadable, what was I thinking?
     * @return the generated "XOR fractal" texture group
     */
    private static TextureGroup xorTextures() {
        final BufferedImage[] xorImages = new BufferedImage[MAX_LIGHT_LEVEL + 1];

        for (int tileLightLevel = 0; tileLightLevel <= MAX_LIGHT_LEVEL; tileLightLevel++) {
            final BufferedImage tile = new BufferedImage(STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, BufferedImage.TYPE_BYTE_GRAY);
            final byte[] tileByteData = ((DataBufferByte) tile.getRaster().getDataBuffer()).getData();

            for (int tileX = 0; tileX < STANDARD_IMAGE_SIZE; tileX++) {
                for (int tileY = 0; tileY < STANDARD_IMAGE_SIZE; tileY++) {
                    tileByteData[(tileX * STANDARD_IMAGE_SIZE) + tileY] = (byte) ((((((tileX ^ tileY) * 8) + 128) & 0xFF) * (int) ((1.0F - (tileLightLevel * 0.2F)) * 0xFF)) / 0xFF);
                }
            }

            xorImages[tileLightLevel] = tile;
        }

        return new TextureGroup("XOR_Block_Texture", xorImages);
    }

    public String getGeneratorName() {
        return "Minecraft_4k_1";
    }

    /**
     * Gets the generated "XOR fractal" textures.
     *
     * @return the generated "XOR fractal" texture group
     */
    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] { xorTextures() };
    }

}
