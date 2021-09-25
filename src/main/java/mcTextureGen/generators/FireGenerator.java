package mcTextureGen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Random;

import mcTextureGen.data.TextureGroup;

/**
 * This class generates fire textures.
 * This is a non-deterministic generator.
 */
public final class FireGenerator extends AbstractTextureGenerator {

    /** A convenience variable to generate larger fire textures with. */
    private static final int FIRE_MULTI = 1;

    /** How wide the fire texture is. */
    private static final int FIRE_TEXTURE_WIDTH = 16 * FIRE_MULTI;

    /** How high the fire texture is. */
    private static final int FIRE_TEXTURE_HEIGHT = 20 * FIRE_MULTI;

    /** How many pixels left to sample from when generating the texture. */
    private static final int SAMPLE_X_LEFT = 1;

    /** How many pixels right to sample from when generating the texture. */
    private static final int SAMPLE_X_RIGHT = 1;

    /** How many pixels down to sample from when generating the texture. */
    private static final int SAMPLE_Y_DOWN = 0;

    /** How many pixels up to sample from when generating the texture. */
    private static final int SAMPLE_Y_UP = 1;

    /** How many times the center pixel is sampled from when generating the texture. */
    private static final int SAMPLE_SELF = 1;

    /*
     * There was originally a counter that was incremented inside the code that sampled neighboring pixels,
     * but the counter would always end up at the same value (24, purely deterministic at build time).
     * These constants are used in place of this counter.
     */

    /** The staring value for the "sample counter". */
    private static final int SAMPLE_COUNTER_START = 18 * FIRE_MULTI;

    /** How many times the "sample counter" would increment. */
    private static final int SAMPLE_COUNTER_ITERATIONS = (SAMPLE_X_LEFT + SAMPLE_SELF + SAMPLE_X_RIGHT) * (SAMPLE_Y_DOWN + SAMPLE_SELF + SAMPLE_Y_UP);

    /** The end value of the "sample counter". */
    private static final int SAMPLE_COUNTER_END = SAMPLE_COUNTER_START + SAMPLE_COUNTER_ITERATIONS;

    /**
     * WTFLOAT is a very strange constant. I felt the name was appropriate, given how hard it was to pin down.
     * Earlier versions of Minecraft define this as 1.06F. Later versions defined it as 1.0600001F.
     * This is the current method I've settled on to determine the scaled value of WTFLOAT.
     * If fireMulti is 1, it results in 1.06F.
     */
    private static final float WTFLOAT = 1.0F + (SAMPLE_SELF * 0.01F) + (((SAMPLE_COUNTER_ITERATIONS - SAMPLE_SELF) * 0.01F) / FIRE_MULTI);

    /**
     * Used during fire texture generation to compensate for sampling multiple pixels.
     * @todo It's rumored that some versions of pocket edition have this as 25.2?
     *       If that's true, removing " + (SAMPLE_SELF * 0.01F)" from WTFLOAT might simulate this.
     */
    private static final float DIV_PIXEL_INTENSITY = SAMPLE_COUNTER_END * WTFLOAT;

    /**
     * Generates the fire textures.
     *
     * @todo   better Javadoc
     * @return the generated fire texture group
     */
    private static TextureGroup fireTextures() {
        float[] fireImagePrevious = new float[FIRE_TEXTURE_WIDTH * FIRE_TEXTURE_HEIGHT];
        float[] fireImageCurrent = new float[FIRE_TEXTURE_WIDTH * FIRE_TEXTURE_HEIGHT];
        final Random rand = getRandom();
        final BufferedImage[] fireImages = new BufferedImage[nonDeterministicFrames];

        for (int currentFrame = 0; currentFrame < nonDeterministicFrames; currentFrame++) {
            for (int fireX = 0; fireX < FIRE_TEXTURE_WIDTH; ++fireX) {
                // Loop over every row except the bottom row
                for (int fireY = 0; fireY < (FIRE_TEXTURE_HEIGHT - 1); ++fireY) {
                    // Start by sampling the pixel above
                    float pixelIntensity = fireImagePrevious[fireX + ((fireY + 1) * FIRE_TEXTURE_WIDTH)] * SAMPLE_COUNTER_START;

                    // Sample from one pixel left to one pixel right, on this row and the above row
                    for (int localFireX = fireX - SAMPLE_X_LEFT; localFireX <= (fireX + SAMPLE_X_RIGHT); ++localFireX) {
                        for (int localFireY = fireY - SAMPLE_Y_DOWN; localFireY <= (fireY + SAMPLE_Y_UP); ++localFireY) {
                            // Check to make sure the pixel we're sampling is in bounds
                            if ((localFireX >= 0) && (localFireY >= 0) && (localFireX < FIRE_TEXTURE_WIDTH) && (localFireY < FIRE_TEXTURE_HEIGHT)) {
                                pixelIntensity += fireImagePrevious[localFireX + (localFireY * FIRE_TEXTURE_WIDTH)];
                            }
                        }
                    }

                    // Set pixel value, compensating for the additional sampling
                    fireImageCurrent[fireX + (fireY * FIRE_TEXTURE_WIDTH)] = pixelIntensity / DIV_PIXEL_INTENSITY;
                }

                // Randomize bottom row of pixels
                fireImageCurrent[fireX + ((FIRE_TEXTURE_HEIGHT - 1) * FIRE_TEXTURE_WIDTH)] = (float)((rand.nextDouble() * rand.nextDouble() * rand.nextDouble() * (3 + FIRE_MULTI)) + (rand.nextDouble() * 0.1F) + 0.2F);
            }

            final float[] fireImageCurrentTemp = fireImageCurrent;
            fireImageCurrent = fireImagePrevious;
            fireImagePrevious = fireImageCurrentTemp;
            final BufferedImage currentFireImage = new BufferedImage(FIRE_TEXTURE_WIDTH, FIRE_TEXTURE_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
            final byte[] imageByteData = ((DataBufferByte) currentFireImage.getRaster().getDataBuffer()).getData();

            for (int currentPixel = 0; currentPixel < (FIRE_TEXTURE_WIDTH * FIRE_TEXTURE_HEIGHT); ++currentPixel) {
                // Boost intensity
                float pixelIntensity = fireImagePrevious[currentPixel] * 1.8F;

                // Clamp pixel intensity
                if (pixelIntensity > 1.0F) {
                    pixelIntensity = 1.0F;
                }

                if (pixelIntensity < 0.0F) {
                    pixelIntensity = 0.0F;
                }

                final int imageOffset = currentPixel * 4;
                // If the pixel isn't intense enough, make it transparent
                imageByteData[imageOffset + 0] = (byte) (pixelIntensity < 0.5F ? 0 : 255);
                imageByteData[imageOffset + 1] = (byte) (pixelIntensity * pixelIntensity * pixelIntensity * pixelIntensity * pixelIntensity * pixelIntensity * pixelIntensity * pixelIntensity * pixelIntensity * pixelIntensity * 255.0F);
                imageByteData[imageOffset + 2] = (byte) (pixelIntensity * pixelIntensity * 255.0F);
                imageByteData[imageOffset + 3] = (byte) ((pixelIntensity * 155.0F) + 100.0F);
            }

            fireImages[currentFrame] = currentFireImage;
        }

        return new TextureGroup("Fire_Textures", fireImages);
    }

    public String getGeneratorName() {
        return "Fire";
    }

    /**
     * Gets the generated fire textures.
     *
     * @return the generated fire texture group
     */
    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] { fireTextures() };
    }

}
