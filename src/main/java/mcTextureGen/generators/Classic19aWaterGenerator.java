package mcTextureGen.generators;

/**
 * This class generates water textures from Minecraft Classic 0.0.19a to snapshot 13w01b.
 * This is a non-deterministic generator.
 */
public final class Classic19aWaterGenerator extends AbstractLiquidGenerator {

    /**
     * Creates a new Classic 0.0.19a water texture generator.
     */
    public Classic19aWaterGenerator() {
        super("Classic_19a_Water");
    }

    /**
     * Generates the water textures.
     * TODO Better Javadoc.
     */
    public void generateLiquidTexture(final float[] liquidImagePrevious, final float[] liquidImageCurrent, final float[] liquidIntensity, final float[] liquidIntensityIntensity) {
        // Generate the image pixel values
        for (int currentWaterX = 0; currentWaterX < LIQUID_IMAGE_SIZE; ++currentWaterX) {
            for (int currentWaterY = 0; currentWaterY < LIQUID_IMAGE_SIZE; ++currentWaterY) {
                float localPixelIntensity = 0.0F;

                // Iterates through (x - 1, y) to (x + 1, y), and accumulates the previous pixel value at each location into localPixelIntensity.
                // Only iterating over the x values causes the water to generate horizontal "stripes", as the y value does not change.
                for (int localWaterX = currentWaterX - 1; localWaterX <= (currentWaterX + 1); ++localWaterX) {
                    // Restrict the adjusted x coordinate to be in range of the maximum valid coordinate (liquidImageSize).
                    // If the x coordinate is out of range, it wraps to be in range.
                    localPixelIntensity += liquidImagePrevious[(localWaterX & LIQUID_IMAGE_MASK) + (currentWaterY * LIQUID_IMAGE_SIZE)];
                }

                final int currentWaterOffset = currentWaterX + (currentWaterY * LIQUID_IMAGE_SIZE);
                // localPixelIntensity is divided by 3.3F, because it samples from 3 x points.
                liquidImageCurrent[currentWaterOffset] = (localPixelIntensity / 3.3F) + (liquidIntensity[currentWaterOffset] * 0.8F);
            }
        }

        // Update liquidIntensity and liquidIntensityIntensity
        for (int currentWaterX = 0; currentWaterX < LIQUID_IMAGE_SIZE; ++currentWaterX) {
            for (int currentWaterY = 0; currentWaterY < LIQUID_IMAGE_SIZE; ++currentWaterY) {
                final int currentWaterOffset = currentWaterX + (currentWaterY * LIQUID_IMAGE_SIZE);
                liquidIntensity[currentWaterOffset] += liquidIntensityIntensity[currentWaterOffset] * 0.05F;

                if (liquidIntensity[currentWaterOffset] < 0.0F) {
                    liquidIntensity[currentWaterOffset] = 0.0F;
                }

                liquidIntensityIntensity[currentWaterOffset] = rand.nextDouble() < 0.05 ? 0.5F : (liquidIntensityIntensity[currentWaterOffset] - 0.1F);
            }
        }
    }

    public void setABGR(final byte[] imageByteData, final float currentPixelIntensity, final int imageOffset) {
        final float currentPixelIntensityPow = currentPixelIntensity * currentPixelIntensity;
        imageByteData[imageOffset + 0] = (byte) (146.0F + (currentPixelIntensityPow * 50.0F));
        imageByteData[imageOffset + 1] = (byte) 0xFF;
        imageByteData[imageOffset + 2] = (byte) (50.0F + (currentPixelIntensityPow * 64.0F));
        imageByteData[imageOffset + 3] = (byte) (32.0F + (currentPixelIntensityPow * 32.0F));
    }

}
