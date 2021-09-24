package mcTextureGen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import mcTextureGen.data.TextureGroup;

public final class MC4k1Generator extends AbstractTextureGenerator {

    private static final int maxLightLevel = 2;
    private static final int tileSize = 16;
    private static final int unsignedByteMax = 0xFF;
    private static final float unsignedByteMaxAsFloat = 0xFF;

    /** Minecraft 4k-1's "XOR fractal" texture generation, for each light level */
    private static TextureGroup xorTextures() {
        final BufferedImage[] xorImages = new BufferedImage[maxLightLevel + 1];

        for (int tileLightLevel = 0; tileLightLevel <= maxLightLevel; tileLightLevel++) {
            final BufferedImage tile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_BYTE_GRAY);
            final byte[] tileByteData = ((DataBufferByte) tile.getRaster().getDataBuffer()).getData();

            for (int tileX = 0; tileX < tileSize; tileX++) {
                for (int tileY = 0; tileY < tileSize; tileY++) {
                    tileByteData[(tileX * tileSize) + tileY] = (byte) ((((((tileX ^ tileY) * 8) + 128) & unsignedByteMax) * (int) ((1.0F - (tileLightLevel * 0.2F)) * unsignedByteMaxAsFloat)) / unsignedByteMax);
                }
            }

            xorImages[tileLightLevel] = tile;
        }

        return new TextureGroup("XOR_Block_Texture", xorImages);
    }

    public String getGeneratorName() {
        return "Minecraft_4k_1";
    }

    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] { xorTextures() };
    }

}
