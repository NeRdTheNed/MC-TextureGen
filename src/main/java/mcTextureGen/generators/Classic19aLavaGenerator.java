package mcTextureGen.generators;

public final class Classic19aLavaGenerator extends AbstractLavaGenerator {

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
