package mcTextureGen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import mcTextureGen.data.TextureGroup;

public class MC4k1Generator implements TextureGenerator {

    @Override
    public String getGeneratorName() {
        return "Minecraft 4k-1";
    }

    @Override
    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] {xorTextures()};
    }

    /** Minecraft 4k-1's "XOR fractal" texture generation, for each light level */
    public TextureGroup xorTextures() {
        int maxLightLevel = 2;
        int tileWidth = 16;
        int tileHeight = 16;
        int andThing = 0xFF;
        int slashThing = 0xFF;
        BufferedImage[] xorImages = new BufferedImage[maxLightLevel + 1];

        for (int tileLightLevel = 0; tileLightLevel <= maxLightLevel; tileLightLevel++) {
            BufferedImage tile = new BufferedImage(tileWidth, tileHeight, 1);
            int[] tileIntData = ((DataBufferInt) tile.getRaster().getDataBuffer()).getData();

            for (int tileX = 0; tileX < tileWidth; tileX++) {
                for (int tileY = 0; tileY < tileHeight; tileY++) {
                    float tileLightModifier = 1.0F;
                    tileLightModifier *= 1.0F - (tileLightLevel * 0.2F);
                    int brightness = 0xFF;
                    brightness = (int) (tileLightModifier * 0xFF);

                    if (brightness < 0) {
                        brightness = 0;
                    }

                    if (brightness > 0xFF) {
                        brightness = 0xFF;
                    }

                    int tileRBG = ((tileX ^ tileY) * 8) + 128;
                    tileRBG |= (tileRBG << 16) | (tileRBG << 8);
                    int tileRed = (((tileRBG >> 16) & andThing) * brightness) / slashThing;
                    int tileGreen = (((tileRBG >> 8) & andThing) * brightness) / slashThing;
                    int tileBlue = ((tileRBG & andThing) * brightness) / slashThing;
                    tileIntData[(tileX * tileWidth) + tileY] = (tileRed << 16) | (tileGreen << 8) | tileBlue;
                }
            }

            xorImages[tileLightLevel] = tile;
        }

        return new TextureGroup ("XOR Block Texture", xorImages);
    }

}
