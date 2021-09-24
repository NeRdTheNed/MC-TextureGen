package mcTextureGen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Random;

import mcTextureGen.data.TextureGroup;

/* TODO clean up, refactor */
public abstract class AbstractLiquidGenerator extends AbstractTextureGenerator {

    private static final int liquidImageBits = 4;
    static final int liquidImageMask = ~(-1 << liquidImageBits);
    static final int liquidImageSize = liquidImageMask + 1;

    private final String generatorName;

    // TODO refactor
    Random rand;

    protected AbstractLiquidGenerator(String generatorName) {
        this.generatorName = generatorName;
    }

    float clampCurrentPixelIntensity(float toClamp) {
        if (toClamp > 1.0F) {
            toClamp = 1.0F;
        }

        if (toClamp < 0.0F) {
            toClamp = 0.0F;
        }

        return toClamp;
    }

    public abstract void generateLiquidTexture(final float[] liquidImagePrevious, final float[] liquidImageCurrent, final float[] liquidIntensity, final float[] liquidIntensityIntensity);

    public final String getGeneratorName() {
        return generatorName;
    }

    public final TextureGroup[] getTextureGroups() {
        return new TextureGroup[] { liquidTextures() };
    }

    private final TextureGroup liquidTextures() {
        rand = getRandom();
        float[] liquidImagePrevious = new float[liquidImageSize * liquidImageSize];
        float[] liquidImageCurrent = new float[liquidImageSize * liquidImageSize];
        final float[] liquidIntensity = new float[liquidImageSize * liquidImageSize];
        final float[] liquidIntensityIntensity = new float[liquidImageSize * liquidImageSize];
        final BufferedImage[] liquidImages = new BufferedImage[nonDeterministicFrames];

        for (int currentFrame = 0; currentFrame < nonDeterministicFrames; currentFrame++) {
            generateLiquidTexture(liquidImagePrevious, liquidImageCurrent, liquidIntensity, liquidIntensityIntensity);
            final float[] liquidImageCurrentTemp = liquidImageCurrent;
            liquidImageCurrent = liquidImagePrevious;
            liquidImagePrevious = liquidImageCurrentTemp;
            final BufferedImage currentLiquidImage = new BufferedImage(liquidImageSize, liquidImageSize, BufferedImage.TYPE_4BYTE_ABGR);
            final byte[] imageByteData = ((DataBufferByte) currentLiquidImage.getRaster().getDataBuffer()).getData();

            for (int currentPixel = 0; currentPixel < (liquidImageSize * liquidImageSize); ++currentPixel) {
                final float currentPixelIntensity = clampCurrentPixelIntensity(liquidImagePrevious[currentPixel]);
                final int imageOffset = currentPixel * 4;
                setABGR(imageByteData, currentPixelIntensity, imageOffset);
            }

            liquidImages[currentFrame] = currentLiquidImage;
        }

        return new TextureGroup(generatorName + "_Textures", liquidImages);
    }

    public abstract void setABGR(final byte[] imageByteData, final float currentPixelIntensity, final int imageOffset);

}
