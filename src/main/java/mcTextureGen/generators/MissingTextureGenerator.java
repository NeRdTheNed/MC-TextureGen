package mcTextureGen.generators;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import mcTextureGen.data.TextureGroup;

public final class MissingTextureGenerator extends AbstractTextureGenerator {

    private static final int CHECKERBOARD_TEXTURE_SIZE = 16;
    private static final int TEXT_TEXTURE_SZIE = 64;

    public String getGeneratorName() {
        return "Missing_Textures";
    }

    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] {
                   missingTextureText("Java_b1_4_to_13w01b", false, new String[] { "missingtex" }),
                   missingTextureText("Java_13w02a_to_13w17a", true, new String[] { "missing", "texture"}),
                   missingTextureCheckerboard("Java_13w18a_to_1_12_2", 0x000000, 0xF800F8),
                   missingTextureCheckerboard("Java_17w43a_to_current", 0xF800F8, 0x000000)
               };
    }

    private static TextureGroup missingTextureCheckerboard(String name, int colourOne, int colourTwo) {
        final BufferedImage missingTexture = new BufferedImage(CHECKERBOARD_TEXTURE_SIZE, CHECKERBOARD_TEXTURE_SIZE, BufferedImage.TYPE_INT_RGB);
        final int[] textureData = ((DataBufferInt) missingTexture.getRaster().getDataBuffer()).getData();

        for (int xPixel = 0; xPixel < CHECKERBOARD_TEXTURE_SIZE; ++xPixel) {
            for (int yPixel = 0; yPixel < CHECKERBOARD_TEXTURE_SIZE; ++yPixel) {
                textureData[xPixel + (yPixel * CHECKERBOARD_TEXTURE_SIZE)] = ((xPixel < (CHECKERBOARD_TEXTURE_SIZE / 2)) ^ (yPixel < (CHECKERBOARD_TEXTURE_SIZE / 2))) ? colourOne : colourTwo;
            }
        }

        return new TextureGroup("Missing_Texture_" + name, missingTexture);
    }

    /** Note: The generated TextureGroup is JVM / system dependent: the font / text rendering method chosen will vary across different platforms. */
    private static TextureGroup missingTextureText(String name, boolean repeats, String[] lines) {
        final BufferedImage[] missingTextureAsArray;

        if (nonDeterministicFrames > 0) {
            final BufferedImage missingTexture = new BufferedImage(TEXT_TEXTURE_SZIE, TEXT_TEXTURE_SZIE, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D graphics = missingTexture.createGraphics();
            // Fill background with white
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, TEXT_TEXTURE_SZIE, TEXT_TEXTURE_SZIE);

            if ((lines != null) && (lines.length > 0)) {
                // Set color to black for text rendering
                graphics.setColor(Color.BLACK);
                int fontSize = graphics.getFont().getSize();

                // Prevent infinite loops
                if (fontSize < 1) {
                    fontSize = 1;
                }

                int yPos = 10;
                int stringsDrawn = 0;

                while (yPos < TEXT_TEXTURE_SZIE) {
                    final String currentLine = lines[stringsDrawn % lines.length];
                    stringsDrawn++;
                    graphics.drawString(currentLine, 1, yPos);
                    yPos += fontSize;

                    if ((stringsDrawn % lines.length) == 0) {
                        if (!repeats) {
                            break;
                        }

                        yPos += 5;
                    }
                }
            }

            graphics.dispose();
            missingTextureAsArray = new BufferedImage[] { missingTexture };
        } else {
            missingTextureAsArray = new BufferedImage[0];
        }

        return new TextureGroup("Missing_Texture_" + name, missingTextureAsArray);
    }

}
