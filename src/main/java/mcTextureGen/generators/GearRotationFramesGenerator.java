package mcTextureGen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import javax.imageio.ImageIO;

import mcTextureGen.data.TextureGroup;

public final class GearRotationFramesGenerator extends TextureGenerator {

    /**
     * This variable controls how many distinct angles (frames) the gear animation has.
     * Minecraft had 64 distinct angles.
     * The gear rotation animation would advance by one frame on every tick.
     */
    private static final int gearRotationSteps = 64;

    /**
     * This variable can be changed to create higher resolution output rotated textures.
     * This is just for convenience.
     */
    private static final int rotatedTextureSizeMultiplier = 1;

    /**
     * gearmiddle.png has a resolution of 16 by 16.
     */
    private static final int middleGearTextureSize = 16;

    /**
     * gear.png has a resolution of 32 by 32.
     */
    private static final int originalGearTextureSize = 32;

    /**
     * This variable controls the resolution of the output rotated gear textures.
     */
    private static final int rotatedTextureSize = middleGearTextureSize * rotatedTextureSizeMultiplier;

    // Integer arrays to store the ARGB values of the original gear images
    private static final int[] gearMiddleARGBValues = new int[middleGearTextureSize * middleGearTextureSize];
    private static final int[] gearARGBValues = new int[originalGearTextureSize * originalGearTextureSize];

    // Only used during testing.
    private static boolean generationIssueFlag = false;

    static {
        // TODO this is janky
        try {
            ImageIO.read(ClassLoader.getSystemResource("gear.png")).getRGB(0, 0, originalGearTextureSize, originalGearTextureSize, gearARGBValues, 0, originalGearTextureSize);
            ImageIO.read(ClassLoader.getSystemResource("gearmiddle.png")).getRGB(0, 0, middleGearTextureSize, middleGearTextureSize, gearMiddleARGBValues, 0, middleGearTextureSize);
        } catch (final IOException e) {
            // Should never happen when running, as the tests must pass to build the application, and the tests don't pass if this happens.
            generationIssueFlag = true;
        }
    }

    private TextureGroup gearRotationTextures() {
        final BufferedImage[] gearTextures = new BufferedImage[gearRotationSteps];

        // For each angle of the gear animation, generate a texture
        for (int i = 0; i < gearRotationSteps; i++) {
            gearTextures[i] = generateGearTextureForRotation(i);
        }

        // Only one TextureGroup is returned, as the clockwise and counter-clockwise animations
        // are comprised of identical frames, played in the opposite order.
        return new TextureGroup("Gear_Rotations", gearTextures);
    }

    // TODO better documentation, variable names are way to verbose, check if gear rotation animation was consistent across all versions of Minecraft
    private BufferedImage generateGearTextureForRotation(int rotationStep) {
        final BufferedImage rotatedImage = new BufferedImage(rotatedTextureSize, rotatedTextureSize, BufferedImage.TYPE_4BYTE_ABGR);
        final byte[] imageByteData = ((DataBufferByte) rotatedImage.getRaster().getDataBuffer()).getData();
        // Convert the current rotation step into an angle in radians,
        // and use the lookup table to get the current sine and cosine of the angle.
        final float sinRotationAngle = lookupSin((rotationStep / (float) gearRotationSteps) * (float) Math.PI * 2.0F);
        final float cosRotationAngle = lookupCos((rotationStep / (float) gearRotationSteps) * (float) Math.PI * 2.0F);

        for (int rotatedImageX = 0; rotatedImageX < rotatedTextureSize; ++rotatedImageX) {
            for (int rotatedImageY = 0; rotatedImageY < rotatedTextureSize; ++rotatedImageY) {
                // TODO I'm not sure why this is done this way
                final float gearImageX = ((rotatedImageX / (rotatedTextureSize - 1.0F)) - 0.5F) * (originalGearTextureSize - 1.0F);
                final float gearImageY = ((rotatedImageY / (rotatedTextureSize - 1.0F)) - 0.5F) * (originalGearTextureSize - 1.0F);
                // Rotate the coordinates TODO document more
                final float rotatedOffsetGearImageX = (cosRotationAngle * gearImageX) - (sinRotationAngle * gearImageY);
                final float rotatedOffsetGearImageY = (cosRotationAngle * gearImageY) + (sinRotationAngle * gearImageX);
                // Fix the offset after rotating
                final int rotatedGearImageX = (int) (rotatedOffsetGearImageX + (originalGearTextureSize / 2));
                final int rotatedGearImageY = (int) (rotatedOffsetGearImageY + (originalGearTextureSize / 2));
                final int ARGB;

                if ((rotatedGearImageX >= 0) && (rotatedGearImageY >= 0) && (rotatedGearImageX < originalGearTextureSize) && (rotatedGearImageY < originalGearTextureSize)) {
                    final int gearDiv = rotatedTextureSize / middleGearTextureSize;
                    final int gearMiddleARGB = gearMiddleARGBValues[(rotatedImageX / gearDiv) + ((rotatedImageY / gearDiv) * middleGearTextureSize)];

                    // Is the alpha component of the RGBA value for the middle piece of the gear greater than 128?
                    // (i.e. in the context of the gear images, is there a non-transparent pixel at that position?
                    // TODO refactor, this is dumb)
                    if ((gearMiddleARGB >>> 24) > 128) {
                        // If so, use the RGBA value for the middle of the gear as the RGBA value
                        ARGB = gearMiddleARGB;
                    } else {
                        ARGB = gearARGBValues[rotatedGearImageX + (rotatedGearImageY * originalGearTextureSize)];
                    }
                } else {
                    ARGB = 0;
                }

                final int imageOffset = (rotatedImageX + (rotatedImageY * rotatedTextureSize)) * 4;
                // Set ABGR values
                imageByteData[imageOffset + 0] = (byte) ((ARGB >>> 24) > 128 ? 255 : 0);
                imageByteData[imageOffset + 1] = (byte) ((ARGB >> 16) & 0xFF);
                imageByteData[imageOffset + 2] = (byte) ((ARGB >> 8) & 0xFF);
                imageByteData[imageOffset + 3] = (byte) (ARGB & 0xFF);
            }
        }

        return rotatedImage;
    }

    @Override
    public String getGeneratorName() {
        return "Gear_Rotation";
    }

    @Override
    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] { gearRotationTextures() };
    }

    @Override
    public boolean hasGenerationIssue() {
        return generationIssueFlag;
    }

}
