package mcTextureGen.generators;

/**
 * This class generates lava textures from Minecraft Classic 0.0.22a to snapshot 13w01b.
 * This is a non-deterministic generator.
 */
public final class Classic22aLavaGenerator extends AbstractLavaGenerator {

    /**
     * Creates a new Classic 0.0.22a lava texture generator.
     */
    public Classic22aLavaGenerator() {
        super("Classic_22a_Lava");
    }

    /**
     * Multiplies the passed float by two, then clamps to a value between 0.0F and 1.0F.
     *
     * @todo refactor
     */
    float clampCurrentPixelIntensity(float toClamp) {
        toClamp *= 2.0F;
        return super.clampCurrentPixelIntensity(toClamp);
    }

    public void setABGR(final byte[] imageByteData, final float currentPixelIntensity, final int imageOffset) {
        imageByteData[imageOffset + 0] = (byte) 0xFF;
        imageByteData[imageOffset + 1] = (byte) (currentPixelIntensity * currentPixelIntensity * currentPixelIntensity * currentPixelIntensity * 128.0F);
        imageByteData[imageOffset + 2] = (byte) (currentPixelIntensity * currentPixelIntensity * 255.0F);
        imageByteData[imageOffset + 3] = (byte) ((currentPixelIntensity * 100.0F) + 155.0F);
    }

}
