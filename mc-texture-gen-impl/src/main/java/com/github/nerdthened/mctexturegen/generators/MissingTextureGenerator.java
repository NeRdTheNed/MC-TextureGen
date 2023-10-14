package com.github.nerdthened.mctexturegen.generators;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.github.nerdthened.mctexturegen.data.TextureGroup;

/**
 * This class generates all known variations of the "missing texture" texture. This includes:
 * <ul>
 * <li>The text-based "missingtex" texture used by Minecraft Beta 1.4 to snapshot 13w01b
 * <li>The text-based "missing texture" texture used by Minecraft snapshot 13w02a to snapshot 13w17a
 * <li>The "checkerboard" / "source engine" missing texture used by Minecraft snapshot 13w18a to 1.12.2
 * <li>The "checkerboard" / "source engine" missing texture used by Minecraft snapshot 17w43a to the current day
 * </ul>
 */
public final class MissingTextureGenerator extends AbstractTextureGenerator {

    /** The size of the generated text based textures. */
    private static final int TEXT_TEXTURE_SIZE = STANDARD_IMAGE_SIZE * 4;

    /** The starting y-position for text-based textures. */
    private static final int TEXT_STARTING_YPOS = 10 * STANDARD_IMAGE_SIZE_MULTIPLIER;

    /**
     * Generates a "checkerboard" texture with the provided colors.
     *
     * @param  name the name of the returned texture group
     * @param  colorOne the first color
     * @param  colorTwo the second color
     * @return the generated "checkerboard" texture
     */
    private static TextureGroup missingTextureCheckerboard(String name, int colorOne, int colorTwo) {
        final BufferedImage missingTexture = new BufferedImage(STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
        final int[] textureData = ((DataBufferInt) missingTexture.getRaster().getDataBuffer()).getData();

        for (int xPixel = 0; xPixel < STANDARD_IMAGE_SIZE; ++xPixel) {
            for (int yPixel = 0; yPixel < STANDARD_IMAGE_SIZE; ++yPixel) {
                textureData[xPixel + (yPixel * STANDARD_IMAGE_SIZE)] = (xPixel < (STANDARD_IMAGE_SIZE / 2)) ^ (yPixel < (STANDARD_IMAGE_SIZE / 2)) ? colorOne : colorTwo;
            }
        }

        return new TextureGroup("Missing_Texture_" + name, missingTexture);
    }

    /**
     * Generates a "checkerboard" texture with the provided colors, with twice the usual image resolution.
     *
     * @param  name the name of the returned texture group
     * @param  colorOne the first color
     * @param  colorTwo the second color
     * @return the generated "checkerboard" texture
     */
    private static TextureGroup missingTextureCheckerboard2x(String name, int colorOne, int colorTwo) {
        final BufferedImage missingTexture = new BufferedImage(STANDARD_IMAGE_SIZE_2X, STANDARD_IMAGE_SIZE_2X, BufferedImage.TYPE_INT_RGB);
        final int[] textureData = ((DataBufferInt) missingTexture.getRaster().getDataBuffer()).getData();

        for (int xPixel = 0; xPixel < STANDARD_IMAGE_SIZE_2X; ++xPixel) {
            for (int yPixel = 0; yPixel < STANDARD_IMAGE_SIZE_2X; ++yPixel) {
                textureData[xPixel + (yPixel * STANDARD_IMAGE_SIZE_2X)] = (xPixel % STANDARD_IMAGE_SIZE < (STANDARD_IMAGE_SIZE / 2)) ^ (yPixel % STANDARD_IMAGE_SIZE < (STANDARD_IMAGE_SIZE / 2)) ? colorOne : colorTwo;
            }
        }

        return new TextureGroup("Missing_Texture_2x_" + name, missingTexture);
    }

    /**
     * Generates a text-based texture with the provided text.
     * The generated TextureGroup is JVM / system dependent:
     * the font / text rendering method chosen will vary across different platforms.
     *
     * @param  name the name of the returned texture group
     * @param  repeats if the text repeats after all lines have been rendered
     * @param  lines the lines of text to render
     * @return the generated text-based texture
     */
    private static TextureGroup missingTextureText(String name, boolean repeats, String[] lines) {
        final BufferedImage[] missingTextureAsArray;

        if (shouldGeneratePlatformDependentTextures) {
            final BufferedImage missingTexture = new BufferedImage(TEXT_TEXTURE_SIZE, TEXT_TEXTURE_SIZE, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D graphics = missingTexture.createGraphics();
            // Really dumb code to use text anti aliasing when running on Apple's legacy java runtime on a Mac with a retina display.
            // TODO I think this should produce the right results but legacy MacOS support is hard.
            final Object contentScaleFactor = Toolkit.getDefaultToolkit().getDesktopProperty("apple.awt.contentScaleFactor");

            if ((contentScaleFactor instanceof Float) && (((Float) contentScaleFactor).floatValue() != 1.0F)) {
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }

            // Fill background with white
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, TEXT_TEXTURE_SIZE, TEXT_TEXTURE_SIZE);

            if ((lines != null) && (lines.length > 0)) {
                // Set color to black for text rendering
                graphics.setColor(Color.BLACK);
                final int checkFontSize;

                // Scale font size with image size multiplier
                if (STANDARD_IMAGE_SIZE_MULTIPLIER != 1) {
                    final Font initualFont = graphics.getFont();
                    final Font newFont = initualFont.deriveFont(initualFont.getSize2D() * STANDARD_IMAGE_SIZE_MULTIPLIER);
                    graphics.setFont(newFont);
                    checkFontSize = newFont.getSize();
                } else {
                    checkFontSize = graphics.getFont().getSize();
                }

                // Prevent infinite loops
                final int fontSize = checkFontSize < 1 ? 1 : checkFontSize;
                int yPos = TEXT_STARTING_YPOS;
                int stringsDrawn = 0;

                while (yPos < TEXT_TEXTURE_SIZE) {
                    final String currentLine = lines[stringsDrawn % lines.length];
                    stringsDrawn++;
                    graphics.drawString(currentLine, 1, yPos);
                    yPos += fontSize;

                    if ((stringsDrawn % lines.length) == 0) {
                        if (!repeats) {
                            break;
                        }

                        yPos += TEXT_STARTING_YPOS / 2;
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

    public String getGeneratorName() {
        return "Missing_Textures";
    }

    /**
     * Gets the generated "missing texture" texture groups.
     *
     * @return the generated "missing texture" texture groups
     */
    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] {
                   missingTextureText("Java_b1_4_to_13w01b", false, new String[] { "missingtex" }),
                   missingTextureText("Java_13w02a_to_13w17a", true, new String[] { "missing", "texture"}),
                   missingTextureCheckerboard("Java_13w18a_to_1_12_2", 0x000000, 0xF800F8),
                   missingTextureCheckerboard("Java_17w43a_to_current", 0xF800F8, 0x000000),
                   missingTextureCheckerboard2x("Java_13w38a_to_14w21b", 0x000000, 0xF800F8)
               };
    }

}
