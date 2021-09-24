package mcTextureGen.generators;

/* TODO clean up, refactor */
public abstract class AbstractLavaGenerator extends AbstractLiquidGenerator {

    protected AbstractLavaGenerator(String generatorName) {
        super(generatorName);
    }

    public final void generateLiquidTexture(final float[] liquidImagePrevious, final float[] liquidImageCurrent, final float[] liquidIntensity, final float[] liquidIntensityIntensity) {
        for (int currentLavaX = 0; currentLavaX < liquidImageSize; ++currentLavaX) {
            for (int currentLavaY = 0; currentLavaY < liquidImageSize; ++currentLavaY) {
                float localPreviousIntensity = 0.0F;
                // Side note: 1.2F is the value originally used, despite it being "incorrect" to use a float here
                // (would usually be a double, as 1.2F gets promoted to double at compile time, due to Math.sin returning a double).
                // This was likely due to programmer oversight when writing this code.
                // TL;DR: I meant to type 1.2F, Notch probably didn't.
                final int ySin = (int) (Math.sin((currentLavaY * Math.PI * 2.0D) / liquidImageSize) * 1.2F);
                final int xCos = (int) (Math.sin((currentLavaX * Math.PI * 2.0D) / liquidImageSize) * 1.2F);

                // Iterates through (x - 1, y - 1) to (x + 1, y + 1).
                // For each x and y value, it accumulates the previous pixel value at (x + ySin, y + xCos) into localPixelIntensity.
                for (int localLavaX = currentLavaX - 1; localLavaX <= (currentLavaX + 1); ++localLavaX) {
                    for (int localLavaY = currentLavaY - 1; localLavaY <= (currentLavaY + 1); ++localLavaY) {
                        // Restrict the adjusted coordinates to be in range of the maximum valid coordinate (liquidImageSize).
                        // If a coordinate is out of range, it wraps to be in range.
                        final int localLavaXWrapped = (localLavaX + ySin) & liquidImageMask;
                        final int localLavaYWrapped = (localLavaY + xCos) & liquidImageMask;
                        localPreviousIntensity += liquidImagePrevious[localLavaXWrapped + (localLavaYWrapped * liquidImageSize)];
                    }
                }

                final int currentLavaOffset = currentLavaX + (currentLavaY * liquidImageSize);
                // A manually unrolled loop iterating from (y, x) to (y + 1, x + 1). Wraps coordinates in the same way as above.
                // For each x and y value, it accumulates the additional pixel intensity into localLiquidIntensity.
                // I don't want to change floating point semantics, so I'm not re-rolling this for now.
                // This was originally inlined as part of the next line of code. I un-inlined it for readability.
                // I'm pretty sure this shouldn't change anything, but I should probably figure out how floats work in Java to verify that.
                final float localLiquidIntensity = liquidIntensity[(currentLavaX & liquidImageMask) + ((currentLavaY & liquidImageMask) * liquidImageSize)]
                                                   + liquidIntensity[(currentLavaX & liquidImageMask) + (((currentLavaY + 1) & liquidImageMask) * liquidImageSize)]
                                                   + liquidIntensity[((currentLavaX + 1) & liquidImageMask) + ((currentLavaY & liquidImageMask) * liquidImageSize)]
                                                   + liquidIntensity[((currentLavaX + 1) & liquidImageMask) + (((currentLavaY + 1) & liquidImageMask) * liquidImageSize)];
                // localLiquidIntensity is divided by 4.0F, because it samples from 4 points.
                // localPreviousIntensity is divided by 10.0F, because the whims of other programmers are inscrutable.
                // You'd expect it to be 9.0F based on the previous bit of logic. Maybe someone made an off-by-one error? Or rounded up from 9.9? TODO investigate
                liquidImageCurrent[currentLavaOffset] = (localPreviousIntensity / 10.0F) + ((localLiquidIntensity / 4.0F) * 0.8F);
                // Update liquidIntensity and liquidIntensityIntensity
                liquidIntensity[currentLavaOffset] += liquidIntensityIntensity[currentLavaOffset] * 0.01F;

                if (liquidIntensity[currentLavaOffset] < 0.0F) {
                    liquidIntensity[currentLavaOffset] = 0.0F;
                }

                liquidIntensityIntensity[currentLavaOffset] = rand.nextDouble() < 0.005 ? 1.5F : (liquidIntensityIntensity[currentLavaOffset] - 0.06F);
            }
        }
    }

}
