package com.github.nerdthened.mctexturegen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Random;

import com.github.nerdthened.mctexturegen.data.TextureGroup;

/**
 * This class is used as the base of texture generators which generate liquid textures (i.e lava and water).
 * All current subclasses are non-deterministic generators.
 *
 * @todo clean up, refactor
 */
public abstract class AbstractLiquidGenerator extends AbstractTextureGenerator {

    /** The name of this liquid texture generator. */
    private final String generatorName;

    /**
     * The Random instance used when generating a liquid texture.
     *
     * @todo refactor
     */
    Random rand;

    /**
     * Creates a new liquid texture generator with the specified name.
     * Used for subclasses to define what their name is.
     *
     * @param generatorName the texture generator name
     */
    protected AbstractLiquidGenerator(String generatorName) {
        this.generatorName = generatorName;
    }

    /**
     * Clamps the passed float to a value between 0.0F and 1.0F.
     * Used in a fairly janky way by {@link Classic22aLavaGenerator#clampCurrentPixelIntensity(float)} to modify the value before clamping.
     *
     * @todo   refactor
     * @param  toClamp the float to clamp
     * @return a float clamped between 0.0F and 1.0F
     */
    float clampCurrentPixelIntensity(float toClamp) {
        if (toClamp > 1.0F) {
            toClamp = 1.0F;
        }

        if (toClamp < 0.0F) {
            toClamp = 0.0F;
        }

        return toClamp;
    }

    /**
     * Generates a liquid texture with the provided parameters.
     *
     * @todo  better Javadoc
     * @param liquidImagePrevious the previous liquid image
     * @param liquidImageCurrent the current liquid image
     * @param liquidIntensity the liquid intensity map
     * @param liquidIntensityIntensity the liquid intensity intensity map
     */
    public abstract void generateLiquidTexture(final float[] liquidImagePrevious, final float[] liquidImageCurrent, final float[] liquidIntensity, final float[] liquidIntensityIntensity);

    /**
     * Returns the set liquid generator name from {@link #AbstractLiquidGenerator(String)}.
     */
    public final String getGeneratorName() {
        return generatorName;
    }

    /**
     * Gets the generated liquid textures.
     *
     * @return the generated liquid texture group
     */
    public final TextureGroup[] getTextureGroups() {
        return new TextureGroup[] { liquidTextures() };
    }

    /**
     * Generates the liquid textures for this liquid texture generator, and returns them as a texture group.
     *
     * @return the generated liquid texture group
     */
    private final TextureGroup liquidTextures() {
        rand = getRandom();
        float[] liquidImagePrevious = new float[STANDARD_IMAGE_SIZE * STANDARD_IMAGE_SIZE];
        float[] liquidImageCurrent = new float[STANDARD_IMAGE_SIZE * STANDARD_IMAGE_SIZE];
        final float[] liquidIntensity = new float[STANDARD_IMAGE_SIZE * STANDARD_IMAGE_SIZE];
        final float[] liquidIntensityIntensity = new float[STANDARD_IMAGE_SIZE * STANDARD_IMAGE_SIZE];
        final BufferedImage[] liquidImages = new BufferedImage[nonDeterministicFrames];

        for (int currentFrame = 0; currentFrame < nonDeterministicFrames; currentFrame++) {
            generateLiquidTexture(liquidImagePrevious, liquidImageCurrent, liquidIntensity, liquidIntensityIntensity);
            final float[] liquidImageCurrentTemp = liquidImageCurrent;
            liquidImageCurrent = liquidImagePrevious;
            liquidImagePrevious = liquidImageCurrentTemp;
            final BufferedImage currentLiquidImage = new BufferedImage(STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
            final byte[] imageByteData = ((DataBufferByte) currentLiquidImage.getRaster().getDataBuffer()).getData();

            for (int currentPixel = 0; currentPixel < (STANDARD_IMAGE_SIZE * STANDARD_IMAGE_SIZE); ++currentPixel) {
                final float currentPixelIntensity = clampCurrentPixelIntensity(liquidImagePrevious[currentPixel]);
                final int imageOffset = currentPixel * 4;
                setABGR(imageByteData, currentPixelIntensity, imageOffset);
            }

            liquidImages[currentFrame] = currentLiquidImage;
        }

        return new TextureGroup(generatorName + "_Textures", liquidImages);
    }

    /**
     * Sets the ABGR values at a specified location for a liquid texture from the current pixel intensity.
     *
     * @todo  refactor
     * @param imageByteData the image byte data to set
     * @param currentPixelIntensity the current pixel intensity
     * @param imageOffset the image offset to write to
     */
    public abstract void setABGR(final byte[] imageByteData, final float currentPixelIntensity, final int imageOffset);

}
