package mcTextureGen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import javax.imageio.ImageIO;

import mcTextureGen.data.TextureGroup;

/**
 * This class generates the frames of the gear rotation animation.
 */
public final class GearRotationFramesGenerator extends AbstractTextureGenerator {

    /**
     * This variable controls how many distinct angles (frames) the gear animation has.
     * Minecraft had 64 distinct angles.
     * The gear rotation animation would advance by one frame on every tick.
     */
    private static final int GEAR_ROTATION_STEPS = 64;

    /**
     * This variable can be changed to create higher resolution output rotated textures.
     * This is just for convenience.
     */
    private static final int ROTATED_TEXTURE_SIZE_MULTIPLIER = 1;

    /** gearmiddle.png has a resolution of 16 by 16. */
    private static final int MIDDLE_GEAR_TEXTURE_SIZE = 16;

    /** gear.png has a resolution of 32 by 32. */
    private static final int ORIGINAL_GEAR_TEXTURE_SIZE = 32;

    /** This variable controls the resolution of the output rotated gear textures. */
    private static final int ROTATED_TEXTURE_SIZE = MIDDLE_GEAR_TEXTURE_SIZE * ROTATED_TEXTURE_SIZE_MULTIPLIER;

    /** An integer array that contains the ARGB values of the "middle gear" image. */
    private static final int[] GEAR_MIDDLE_ARGB_VALUES = new int[MIDDLE_GEAR_TEXTURE_SIZE * MIDDLE_GEAR_TEXTURE_SIZE];

    /** An integer array that contains the ARGB values of the "gear" image. */
    private static final int[] GEAR_ARGB_VALUES = new int[ORIGINAL_GEAR_TEXTURE_SIZE * ORIGINAL_GEAR_TEXTURE_SIZE];

    /** Only used during testing. */
    private static boolean generationIssueFlag = false;

    static {
        // TODO this is janky
        try {
            ImageIO.read(ClassLoader.getSystemResource("gear.png")).getRGB(0, 0, ORIGINAL_GEAR_TEXTURE_SIZE, ORIGINAL_GEAR_TEXTURE_SIZE, GEAR_ARGB_VALUES, 0, ORIGINAL_GEAR_TEXTURE_SIZE);
            ImageIO.read(ClassLoader.getSystemResource("gearmiddle.png")).getRGB(0, 0, MIDDLE_GEAR_TEXTURE_SIZE, MIDDLE_GEAR_TEXTURE_SIZE, GEAR_MIDDLE_ARGB_VALUES, 0, MIDDLE_GEAR_TEXTURE_SIZE);
        } catch (final IOException e) {
            // Should never happen when running, as the tests must pass to build the application, and the tests don't pass if this happens.
            generationIssueFlag = true;
        }
    }

    /**
     * Generates the gear rotation textures, for each angle of the gear animation.
     *
     * @return the generated gear rotation texture group
     */
    private static TextureGroup gearRotationTextures() {
        final BufferedImage[] gearTextures = new BufferedImage[GEAR_ROTATION_STEPS];

        // For each angle of the gear animation, generate a texture
        for (int i = 0; i < GEAR_ROTATION_STEPS; i++) {
            gearTextures[i] = generateGearTextureForRotation(i);
        }

        // Only one TextureGroup is returned, as the clockwise and counter-clockwise animations
        // are comprised of identical frames, played in the opposite order.
        return new TextureGroup("Gear_Rotations", gearTextures);
    }

    /**
     * Generates a gear texture for a rotation step.
     *
     * @param rotationStep the rotation step
     * @return the generated gear rotation image
     */
    // TODO better documentation, variable names are way to verbose, check if gear rotation animation was consistent across all versions of Minecraft
    private static BufferedImage generateGearTextureForRotation(int rotationStep) {
        final BufferedImage rotatedImage = new BufferedImage(ROTATED_TEXTURE_SIZE, ROTATED_TEXTURE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        final byte[] imageByteData = ((DataBufferByte) rotatedImage.getRaster().getDataBuffer()).getData();
        // Convert the current rotation step into an angle in radians,
        // and use the lookup table to get the current sine and cosine of the angle.
        final float sinRotationAngle = lookupSin((rotationStep / (float) GEAR_ROTATION_STEPS) * (float) Math.PI * 2.0F);
        final float cosRotationAngle = lookupCos((rotationStep / (float) GEAR_ROTATION_STEPS) * (float) Math.PI * 2.0F);

        for (int rotatedImageX = 0; rotatedImageX < ROTATED_TEXTURE_SIZE; ++rotatedImageX) {
            for (int rotatedImageY = 0; rotatedImageY < ROTATED_TEXTURE_SIZE; ++rotatedImageY) {
                // TODO I'm not sure why this is done this way
                final float gearImageX = ((rotatedImageX / (ROTATED_TEXTURE_SIZE - 1.0F)) - 0.5F) * (ORIGINAL_GEAR_TEXTURE_SIZE - 1.0F);
                final float gearImageY = ((rotatedImageY / (ROTATED_TEXTURE_SIZE - 1.0F)) - 0.5F) * (ORIGINAL_GEAR_TEXTURE_SIZE - 1.0F);
                // Rotate the coordinates TODO document more
                final float rotatedOffsetGearImageX = (cosRotationAngle * gearImageX) - (sinRotationAngle * gearImageY);
                final float rotatedOffsetGearImageY = (cosRotationAngle * gearImageY) + (sinRotationAngle * gearImageX);
                // Fix the offset after rotating
                final int rotatedGearImageX = (int) (rotatedOffsetGearImageX + (ORIGINAL_GEAR_TEXTURE_SIZE / 2));
                final int rotatedGearImageY = (int) (rotatedOffsetGearImageY + (ORIGINAL_GEAR_TEXTURE_SIZE / 2));
                final int ARGB;

                if ((rotatedGearImageX >= 0) && (rotatedGearImageY >= 0) && (rotatedGearImageX < ORIGINAL_GEAR_TEXTURE_SIZE) && (rotatedGearImageY < ORIGINAL_GEAR_TEXTURE_SIZE)) {
                    final int gearDiv = ROTATED_TEXTURE_SIZE / MIDDLE_GEAR_TEXTURE_SIZE;
                    final int gearMiddleARGB = GEAR_MIDDLE_ARGB_VALUES[(rotatedImageX / gearDiv) + ((rotatedImageY / gearDiv) * MIDDLE_GEAR_TEXTURE_SIZE)];
                    // Is the alpha component of the RGBA value for the middle piece of the gear greater than 128?
                    // (i.e. in the context of the gear images, is there a non-transparent pixel at that position?
                    // If so, use the RGBA value for the middle of the gear as the RGBA value
                    // TODO refactor, this is dumb)
                    ARGB = (gearMiddleARGB >>> 24) > 128 ? gearMiddleARGB : GEAR_ARGB_VALUES[rotatedGearImageX + (rotatedGearImageY * ORIGINAL_GEAR_TEXTURE_SIZE)];
                } else {
                    ARGB = 0;
                }

                final int imageOffset = (rotatedImageX + (rotatedImageY * ROTATED_TEXTURE_SIZE)) * 4;
                // Set ABGR values
                imageByteData[imageOffset + 0] = (byte) ((ARGB >>> 24) > 128 ? 255 : 0);
                imageByteData[imageOffset + 1] = (byte) ((ARGB >> 16) & 0xFF);
                imageByteData[imageOffset + 2] = (byte) ((ARGB >> 8) & 0xFF);
                imageByteData[imageOffset + 3] = (byte) (ARGB & 0xFF);
            }
        }

        return rotatedImage;
    }

    public String getGeneratorName() {
        return "Gear_Rotation";
    }

    /**
     * Gets the generated gear rotation textures.
     *
     * @return the generated gear rotation texture group
     */
    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] { gearRotationTextures() };
    }

    public boolean hasGenerationIssue() {
        return generationIssueFlag;
    }

}
