package mcTextureGen.generators;

/**
 * This class generates lava textures from Minecraft Classic 0.0.19a to Classic 0.0.21a_01.
 * This is a non-deterministic generator.
 */
public final class Classic19aLavaGenerator extends AbstractLavaGenerator {

    /**
     * Creates a new Classic 0.0.19a lava texture generator.
     */
    public Classic19aLavaGenerator() {
        super("Classic_19a_Lava");
    }

    public void setABGR(final byte[] imageByteData, final float currentPixelIntensity, final int imageOffset) {
        imageByteData[imageOffset + 0] = (byte) 0xFF;
        imageByteData[imageOffset + 1] = (byte) (currentPixelIntensity * currentPixelIntensity * currentPixelIntensity * currentPixelIntensity * 128.0F);
        imageByteData[imageOffset + 2] = (byte) (currentPixelIntensity * currentPixelIntensity * 255.0F);
        imageByteData[imageOffset + 3] = (byte) ((currentPixelIntensity * 200.0F) + 55.0F);
    }

}
