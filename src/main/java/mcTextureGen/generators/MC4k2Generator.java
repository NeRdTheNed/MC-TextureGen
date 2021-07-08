package mcTextureGen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;
/*
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;
*/

import mcTextureGen.data.TextureGroup;

/** TODO WIP, need to split into individual texture groups */
public final class MC4k2Generator extends TextureGenerator {

    private static final int COLOUR_LIGHT_BROWN = 0xFFBC9862;
    private static final int COLOUR_BROWN = 0xFF966C4A;
    private static final int COLOUR_DARK_BROWN = 0xFF675231;
    private static final int COLOUR_LIGHT_GREY = 0xFFBCAFA5;
    private static final int COLOUR_GREY = 0xFF7F7F7F;
    private static final int COLOUR_LIGHT_GREEN = 0xFF50D937;
    private static final int COLOUR_GREEN = 0xFF6AAA40;
    private static final int COLOUR_RED = 0xFFB53A15;
    private static final int COLOUR_NONE = 0x00000000;

    private static final int TEXTURE_GRASS = 1;
    private static final int TEXTURE_STONE = 4;
    private static final int TEXTURE_BRICK = 5;
    private static final int TEXTURE_LOG = 7;
    private static final int TEXTURE_LEAF = 8;

    private static final int MAX_TEXTURE_IDS = 16;
    private static final int TEXTURE_SIZE = 16;
    private static final int TEXTURES_PER_ID = 3;
    private static final int TEXTURE_OFFSET = 1;

    @Override
    public String getGeneratorName() {
        return "Minecraft_4k_2";
    }

    @Override
    public TextureGroup[] getTextureGroups() {
        return rawTextureDump();
    }

    /** TODO refactor */
    public TextureGroup[] rawTextureDump() {
        final TextureGroup[] textureGroups = new TextureGroup[MAX_TEXTURE_IDS - TEXTURE_OFFSET];
        final Random rand; //= new Random();
        /*rand.setSeed(18295169L);
        //final int[] useLess = new int[0x40000];

        for (int i = 0; i < 0x40000; i++) {
            //useLess[i] = ((i / 64) % 64) > (32 + rand.nextInt(8)) ? rand.nextInt(8) + 1 : 0;
        	int useLess = ((i / 64) % 64) > (32 + rand.nextInt(8)) ? rand.nextInt(8) + 1 : 0;
        }*/
        // https://stackoverflow.com/a/29278559 was used to extract the value of the seed after these calls had been made, which turned out to be 151924357153274.
        rand = new Random(151924357153274L);

        // Generate textures
        for (int textureID = TEXTURE_OFFSET; textureID < MAX_TEXTURE_IDS; textureID++) {
            int randomVariation = 0xFF - rand.nextInt(0x60);
            final BufferedImage[] blockTexturesForID = new BufferedImage[TEXTURES_PER_ID];
            final String textureGroupName;

            // TODO repeated code
            switch (textureID) {
            case TEXTURE_STONE:
                textureGroupName = "STONE";
                break;

            case TEXTURE_BRICK:
                textureGroupName = "BRICK";
                break;

            case TEXTURE_LOG:
                textureGroupName = "LOG";
                break;

            case TEXTURE_LEAF:
                textureGroupName = "LEAF";
                break;

            case TEXTURE_GRASS:
                textureGroupName = "GRASS";
                break;

            default:
                textureGroupName = "DIRT";
                break;
            }

            for (int subTexture = 0; subTexture < TEXTURES_PER_ID; subTexture++) {
                final BufferedImage tile = new BufferedImage(TEXTURE_SIZE, TEXTURE_SIZE, BufferedImage.TYPE_INT_ARGB);
                final int[] textureData = ((DataBufferInt) tile.getRaster().getDataBuffer()).getData();

                for (int yPixel = 0; yPixel < TEXTURE_SIZE; yPixel++) {
                    for (int xPixel = 0; xPixel < TEXTURE_SIZE; xPixel++) {
                        // The stone texture generates "stripes" by only varying the texture 1/4 of the time this loop runs.
                        // Otherwise, the same "variation" is used in this position as the last X position.
                        if ((textureID != TEXTURE_STONE) || (rand.nextInt(3) == 0)) {
                            randomVariation = 0xFF - rand.nextInt(0x60);
                        }

                        // TODO refactor
                        final boolean isTransparentPixelOnLeaf = (textureID == TEXTURE_LEAF) && (rand.nextInt(2) == 0);
                        final int colour;

                        switch (textureID) {
                        case TEXTURE_STONE:
                            colour = COLOUR_GREY;
                            break;

                        case TEXTURE_BRICK:

                            // These generate the horizontal & vertical lines on brick textures respectively.
                            if ((((xPixel + ((yPixel / 4) * 4)) % 8) == 0) || ((yPixel % 4) == 0)) {
                                colour = COLOUR_LIGHT_GREY;
                            } else {
                                colour = COLOUR_RED;
                            }

                            break;

                        case TEXTURE_LOG:

                            // Log texture generation depends on what side of the log texture is being generated.
                            // TODO refactor
                            if ((xPixel <= 0) || (xPixel >= 15) || (yPixel <= 0) || (yPixel >= 15)  || (subTexture == 1)) {
                                colour = COLOUR_DARK_BROWN;

                                if (rand.nextInt(2) == 0) {
                                    randomVariation = (randomVariation * (150 - ((xPixel & 1) * 100))) / 100;
                                }
                            } else {
                                colour = COLOUR_LIGHT_BROWN;
                                int tempOne = xPixel - 7;
                                int tempTwo = (yPixel & 15) - 7;

                                if (tempOne < 0) {
                                    tempOne = 1 - tempOne;
                                }

                                if (tempTwo < 0) {
                                    tempTwo = 1 - tempTwo;
                                }

                                if (tempTwo > tempOne) {
                                    tempOne = tempTwo;
                                }

                                randomVariation = (0xC4 - rand.nextInt(32)) + ((tempOne % 3) * 32);
                            }

                            break;

                        case TEXTURE_LEAF:
                            if (isTransparentPixelOnLeaf) {
                                colour = COLOUR_NONE;
                            } else {
                                colour = COLOUR_LIGHT_GREEN;
                            }

                            break;

                        case TEXTURE_GRASS:
                            // On the side of the grass block, grass elements only generate until a certain threshold.
                            final int grassCheck = (((xPixel * xPixel * 3) + (xPixel * 81)) >> 2) & 3;

                            if ((yPixel + (subTexture * TEXTURE_SIZE)) < (grassCheck + 18)) {
                                colour = COLOUR_GREEN;
                                break;
                            }

                            // If this threshold has just been exceeded, "blend" the grass texture into the dirt texture by darkening the dirt below the grass.
                            if ((yPixel + (subTexture * TEXTURE_SIZE)) < (grassCheck + 19)) {
                                randomVariation = (randomVariation * 2) / 3;
                            }

                        // The fall through allows grass texture generation to re-use the dirt texture generation code for non-grass elements.

                        default:
                            // The dirt texture is generated for any texture without an explicitly defined case.
                            // It therefore occupies block IDs 2, 3, 6 and 9-15.
                            // Each dirt texture is actually used in game, as each ID is allowed during world generation.
                            colour = COLOUR_BROWN;
                            break;
                        }

                        final int randomVariationWithBlockSideLight;

                        // TODO refactor
                        if (isTransparentPixelOnLeaf) {
                            randomVariationWithBlockSideLight = 0xFF;
                        } else if (subTexture == 2) { // If this is the third texture (the underneath of a block) of any ID, darken it.
                            randomVariationWithBlockSideLight = randomVariation / 2;
                        } else {
                            randomVariationWithBlockSideLight = randomVariation;
                        }

                        // Modify RGB data & merge into textureData
                        textureData[xPixel + (yPixel * TEXTURE_SIZE)] =
                            (colour            & 0xFF000000)                                               |
                            (((((colour >> 16) & 0xFF) * randomVariationWithBlockSideLight) / 0xFF) << 16) |
                            (((((colour >> 8)  & 0xFF) * randomVariationWithBlockSideLight) / 0xFF) <<  8) |
                            (((  colour        & 0xFF) * randomVariationWithBlockSideLight) / 0xFF);
                    }
                }

                blockTexturesForID[subTexture] = tile;
            }

            textureGroups[textureID - TEXTURE_OFFSET] = new TextureGroup("ID_" + textureID + "_" + textureGroupName, blockTexturesForID);
        }

        return textureGroups;
    }

}
