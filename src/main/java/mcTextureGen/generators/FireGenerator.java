package mcTextureGen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Random;

import mcTextureGen.data.TextureGroup;

public final class FireGenerator extends AbstractTextureGenerator {

    private static final int fireMulti = 1;

    // Set up fire texture size
    private static final int fireTextureWidth = 16 * fireMulti;
    private static final int fireTextureHeight = 20 * fireMulti;

    // Pixels to sample from.
    private static final int sampleXLeft = 1;
    private static final int sampleXRight = 1;
    private static final int sampleYDown = 0;
    private static final int sampleYUp = 1;

    // This was originally a counter that was incremented inside the code that sampled neighboring pixels,
    // but the counter would always end up at the same value (24, purely deterministic at build time).
    private static final int selfSamples = 1;
    private static final int sampleCounterStart = 18 * fireMulti;
    private static final int sampleCounterIterations = (sampleXLeft + selfSamples + sampleXRight) * (sampleYDown + selfSamples + sampleYUp);
    private static final int sampleCounterEnd = sampleCounterStart + sampleCounterIterations;

    // wtFloat is a very strange constant. I felt the name was appropriate, given how hard it was to pin down.
    // These sorts of constants keep me awake at night.

    // Earlier versions of Minecraft.
    //private static final float wtFloat = 1.06F;
    // Later versions.
    //private static final float wtFloat = 1.0600001F;

    // This is the current method I've settled on to determine the scaled value of wtFloat.
    // If fireMulti is 1, it results in 1.06F.
    private static final float wtFloat = 1.0F + (selfSamples * 0.01F) + (((sampleCounterIterations - selfSamples) * 0.01F) / fireMulti);

    // TODO It's rumored that some versions of pocket edition have this as 25.2?
    // If that's true, removing " + (selfSamples * 0.01F)" from wtFloat might simulate this.
    private static final float divPixelIntensity = sampleCounterEnd * wtFloat;

    @Override
    public String getGeneratorName() {
        return "Fire";
    }

    @Override
    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] { fireTextures() };
    }

    private static TextureGroup fireTextures() {
        float[] fireImagePrevious = new float[fireTextureWidth * fireTextureHeight];
        float[] fireImageCurrent = new float[fireTextureWidth * fireTextureHeight];
        final Random rand = getRandom();
        final BufferedImage[] fireImages = new BufferedImage[nonDeterministicFrames];

        for (int currentFrame = 0; currentFrame < nonDeterministicFrames; currentFrame++) {
            for (int fireX = 0; fireX < fireTextureWidth; ++fireX) {
                // Loop over every row except the bottom row
                for (int fireY = 0; fireY < (fireTextureHeight - 1); ++fireY) {
                    // Start by sampling the pixel above
                    float pixelIntensity = fireImagePrevious[fireX + ((fireY + 1) * fireTextureWidth)] * sampleCounterStart;

                    // Sample from one pixel left to one pixel right, on this row and the above row
                    for (int localFireX = fireX - sampleXLeft; localFireX <= (fireX + sampleXRight); ++localFireX) {
                        for (int localFireY = fireY - sampleYDown; localFireY <= (fireY + sampleYUp); ++localFireY) {
                            // Check to make sure the pixel we're sampling is in bounds
                            if ((localFireX >= 0) && (localFireY >= 0) && (localFireX < fireTextureWidth) && (localFireY < fireTextureHeight)) {
                                pixelIntensity += fireImagePrevious[localFireX + (localFireY * fireTextureWidth)];
                            }
                        }
                    }

                    // Set pixel value, compensating for the additional sampling
                    fireImageCurrent[fireX + (fireY * fireTextureWidth)] = pixelIntensity / divPixelIntensity;
                }

                // Randomize bottom row of pixels
                fireImageCurrent[fireX + ((fireTextureHeight - 1) * fireTextureWidth)] = (float)((rand.nextDouble() * rand.nextDouble() * rand.nextDouble() * (3 + fireMulti)) + (rand.nextDouble() * 0.1F) + 0.2F);
            }

            final float[] fireImageCurrentTemp = fireImageCurrent;
            fireImageCurrent = fireImagePrevious;
            fireImagePrevious = fireImageCurrentTemp;
            final BufferedImage currentFireImage = new BufferedImage(fireTextureWidth, fireTextureHeight, BufferedImage.TYPE_4BYTE_ABGR);
            final byte[] imageByteData = ((DataBufferByte) currentFireImage.getRaster().getDataBuffer()).getData();

            for (int currentPixel = 0; currentPixel < (fireTextureWidth * fireTextureHeight); ++currentPixel) {
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

}
