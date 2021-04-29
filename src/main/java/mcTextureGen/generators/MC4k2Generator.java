package mcTextureGen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Random;
/*
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;
*/

import mcTextureGen.data.TextureGroup;

/** TODO WIP, need to split into individual texture groups */
public class MC4k2Generator implements TextureGenerator {

    private static final int COLOUR_LIGHT_BROWN = 0xFFBC9862;
    private static final int COLOUR_BROWN = 0xFF966C4A;
    private static final int COLOUR_DARK_BROWN = 0xFF675231;
    private static final int COLOUR_LIGHT_GREY = 0xFFBCAFA5;
    private static final int COLOUR_GREY = 0xFF7F7F7F;
    private static final int COLOUR_LIGHT_GREEN = 0xFF50D937;
    private static final int COLOUR_GREEN = 0xFF6AAA40;
    private static final int COLOUR_RED = 0xFFB53A15;
    private static final int COLOUR_NONE = 0x00000000;

    @Override
    public String getGeneratorName() {
        return "Minecraft 4k-2";
    }

    @Override
    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] {rawTextureDump()};
    }

    /** TODO WIP, need to split into individual texture groups */
    public TextureGroup rawTextureDump() {
        final ArrayList<BufferedImage> textureTemp = new ArrayList<>();
        final Random rand; //= new Random();
        /*rand.setSeed(18295169L);
        //final int[] useLess = new int[0x40000];

        for (int i = 0; i < 0x40000; ++i) {
            //useLess[i] = ((i / 64) % 64) > (32 + rand.nextInt(8)) ? rand.nextInt(8) + 1 : 0;
        	int useLess = ((i / 64) % 64) > (32 + rand.nextInt(8)) ? rand.nextInt(8) + 1 : 0;
        }*/
        // https://stackoverflow.com/a/29278559 was used to extract the value of the seed after these calls had been made, which turned out to be 151924357153274.
        rand = new Random(151924357153274L);
        final BufferedImage tile = new BufferedImage(16, 768, BufferedImage.TYPE_INT_ARGB);
        final int[] textureData = ((DataBufferInt) tile.getRaster().getDataBuffer()).getData();

        // Generate textures
        for (int outerLoop = 1; outerLoop < 16; ++outerLoop) {
            int randRes = 255 - rand.nextInt(96);

            for (int middleLoop = 0; middleLoop < 16 * 3; ++middleLoop) {
                for (int innerLoop = 0; innerLoop < 16; ++innerLoop) {
                    if ((outerLoop != 4) || (rand.nextInt(3) == 0)) {
                        randRes = 255 - rand.nextInt(96);
                    }

                    final boolean isTransparentPixelOnLeaf = ((outerLoop == 8) && (rand.nextInt(2) == 0));
                    final int colour;

                    switch (outerLoop) {
                    case 4: // Stone
                        colour = COLOUR_GREY;
                        break;

                    case 5: // Brick
                        if ((((innerLoop + ((middleLoop / 4) * 4)) % 8) == 0) || ((middleLoop % 4) == 0)) {
                            colour = COLOUR_LIGHT_GREY;
                        } else {
                            colour = COLOUR_RED;
                        }

                        break;

                    case 7: // Log
                        if ((innerLoop <= 0) || (innerLoop >= 15) || (((middleLoop <= 0) || (middleLoop >= 15)) && ((middleLoop <= 32) || (middleLoop >= 47)))) {
                            colour = COLOUR_DARK_BROWN;

                            if (rand.nextInt(2) == 0) {
                                randRes = (randRes * (150 - ((innerLoop & 1) * 100))) / 100;
                            }
                        } else {
                            colour = COLOUR_LIGHT_BROWN;
                            int tempOne = innerLoop - 7;
                            int tempTwo = (middleLoop & 15) - 7;

                            if (tempOne < 0) {
                                tempOne = 1 - tempOne;
                            }

                            if (tempTwo < 0) {
                                tempTwo = 1 - tempTwo;
                            }

                            if (tempTwo > tempOne) {
                                tempOne = tempTwo;
                            }

                            randRes = (0xC4 - rand.nextInt(32)) + ((tempOne % 3) * 32);
                        }

                        break;

                    case 8: // Leaf
                        if (isTransparentPixelOnLeaf) {
                            colour = COLOUR_NONE;
                        } else {
                            colour = COLOUR_LIGHT_GREEN;
                        }

                        break;

                    case 1: // Grass
                        final int grassCheck = ((((innerLoop * innerLoop * 3) + (innerLoop * 81)) >> 2) & 3);

                        if (middleLoop < (grassCheck + 18)) {
                            colour = COLOUR_GREEN;
                            break;
                        }

                        if (middleLoop < (grassCheck + 19)) {
                            randRes = (randRes * 2) / 3;
                        }

                    default: // Dirt
                        colour = COLOUR_BROWN;
                        break;
                    }

                    final int randAlter;

                    if (isTransparentPixelOnLeaf) {
                        randAlter = 255;
                    } else if (middleLoop >= 32) {
                        randAlter = randRes / 2;
                    } else {
                        randAlter = randRes;
                    }

                    textureData[innerLoop + (middleLoop * 16) + (outerLoop * 256 * 3)] = (colour & 0xFF000000) | (((((colour >> 16) & 255) * randAlter) / 255) << 16) | (((((colour >> 8) & 255) * randAlter) / 255) << 8) | (((colour & 255) * randAlter) / 255);
                }
            }
        }

        textureTemp.add(tile);
        return new TextureGroup ("rawTextureDump", textureTemp.toArray(new BufferedImage[] {new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)}));
    }

}
