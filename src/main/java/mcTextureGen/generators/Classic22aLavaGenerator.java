package mcTextureGen.generators;

public final class Classic22aLavaGenerator extends AbstractLavaGenerator {

    public Classic22aLavaGenerator() {
        super("Classic_22a_Lava");
    }

    public void setABGR(final byte[] imageByteData, final float currentPixelIntensity, final int imageOffset) {
        imageByteData[imageOffset + 0] = (byte) 0xFF;
        imageByteData[imageOffset + 1] = (byte) (currentPixelIntensity * currentPixelIntensity * currentPixelIntensity * currentPixelIntensity * 128.0F);
        imageByteData[imageOffset + 2] = (byte) (currentPixelIntensity * currentPixelIntensity * 255.0F);
        imageByteData[imageOffset + 3] = (byte) ((currentPixelIntensity * 100.0F) + 155.0F);
    }

    float clampCurrentPixelIntensity(float toClamp) {
        toClamp *= 2.0F;
        return super.clampCurrentPixelIntensity(toClamp);
    }

}
