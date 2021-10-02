package com.github.nerdthened.mctexturegen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Random;

import com.github.nerdthened.mctexturegen.data.TextureGroup;

/**
 * This class generates all nether portal textures.
 */
public final class NetherPortalGenerator extends AbstractTextureGenerator {

    /** True if a constant offset should be added to the texture. */
    private static final boolean ADD_CONSTANT_OFFSET = true;

    /** True if random noise should be added to the texture. */
    private static final boolean ADD_RANDOM_NOISE = true;

    /** The amount of generated nether portal images. */
    private static final int PORTAL_IMAGE_AMOUNT = 32;

    /**
     * How many spirals the generated image has.
     *
     * @todo this doesn't really work for more than a value of 2
     * @see  #netherPortalFrames()
     */
    private static final int SPIRAL_AMOUNT = 2;

    /**
     * Generates all frames of the nether portal animation.
     *
     * @todo   refactor, document
     * @return the generated nether portal texture group
     */
    private static TextureGroup netherPortalFrames() {
        final BufferedImage[] portalImages = new BufferedImage[PORTAL_IMAGE_AMOUNT];
        final Random rand = new Random(100L);

        for (int currentPortalImage = 0; currentPortalImage < PORTAL_IMAGE_AMOUNT; currentPortalImage++) {
            final BufferedImage portalImage = new BufferedImage(STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
            final byte[] imageByteData = ((DataBufferByte) portalImage.getRaster().getDataBuffer()).getData();

            for (int portalImageX = 0; portalImageX < STANDARD_IMAGE_SIZE; portalImageX++) {
                for (int portalImageY = 0; portalImageY < STANDARD_IMAGE_SIZE; portalImageY++) {
                    float currentPixelIntensity = 0.0F;

                    for (int currentSpiral = 0; currentSpiral < SPIRAL_AMOUNT; currentSpiral++) {
                        final float currentSpiralOffset = currentSpiral * (STANDARD_IMAGE_SIZE / SPIRAL_AMOUNT);
                        float currentSpiralX = ((portalImageX - currentSpiralOffset) / STANDARD_IMAGE_SIZE) * 2.0F;
                        float currentSpiralY = ((portalImageY - currentSpiralOffset) / STANDARD_IMAGE_SIZE) * 2.0F;

                        if (ADD_CONSTANT_OFFSET) {
                            if (currentSpiralX < -1.0F) {
                                currentSpiralX += 2.0F;
                            }

                            if (currentSpiralX >= 1.0F) {
                                currentSpiralX -= 2.0F;
                            }

                            if (currentSpiralY < -1.0F) {
                                currentSpiralY += 2.0F;
                            }

                            if (currentSpiralY >= 1.0F) {
                                currentSpiralY -= 2.0F;
                            }
                        }

                        final float spiralPowerThingy = (currentSpiralX * currentSpiralX) + (currentSpiralY * currentSpiralY);
                        // TODO fix this to actually make more than two spirals work
                        float currentSpiralIntensity = (float)Math.atan2(currentSpiralY, currentSpiralX) + ((((((float)currentPortalImage / (float)PORTAL_IMAGE_AMOUNT) * (float) Math.PI * 2.0F) - (spiralPowerThingy * 10.0F)) + (currentSpiral * 2)) * ((currentSpiral * 2) - 1));
                        currentSpiralIntensity = (lookupSin(currentSpiralIntensity) + 1.0F) / 2.0F;
                        currentSpiralIntensity /= spiralPowerThingy + 1.0F;
                        currentPixelIntensity += currentSpiralIntensity * (1.0F / SPIRAL_AMOUNT);
                    }

                    if (ADD_RANDOM_NOISE) {
                        currentPixelIntensity += rand.nextFloat() * 0.1F;
                    }

                    // Construct the ABGR components of the image.
                    final int imageOffset = (portalImageX + (portalImageY * STANDARD_IMAGE_SIZE)) * 4;
                    /*
                     * Aplha / blue is very common, and has a fairly normal distribution.
                     * The alpha value is the same as the blue value.
                     * This means that the nether portal is the most transparent at the least blue pixels
                     * / the most opaque at the most blue pixels.
                     */
                    final byte blueAndAlpha = (byte) ((currentPixelIntensity * 100.0F) + 155.0F);
                    imageByteData[imageOffset + 0] = blueAndAlpha;
                    imageByteData[imageOffset + 1] = blueAndAlpha;
                    // Green is very unlikely, and has a large amount of variance.
                    imageByteData[imageOffset + 2] = (byte) (currentPixelIntensity * currentPixelIntensity * currentPixelIntensity * currentPixelIntensity * 255.0F);
                    // Red has a fairly large variance, but appears fairly commonly.
                    imageByteData[imageOffset + 3] = (byte) ((currentPixelIntensity * currentPixelIntensity * 200.0F) + 55.0F);
                }
            }

            portalImages[currentPortalImage] = portalImage;
        }

        return new TextureGroup("Nether_Portal_Animation", portalImages);
    }

    public String getGeneratorName() {
        return "Nether_Portal";
    }

    /**
     * Gets the generated nether portal textures.
     *
     * @return the generated nether portal texture group
     */
    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] { netherPortalFrames() };
    }

}
