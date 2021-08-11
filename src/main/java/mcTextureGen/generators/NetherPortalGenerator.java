package mcTextureGen.generators;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Random;

import mcTextureGen.data.TextureGroup;

public final class NetherPortalGenerator extends AbstractTextureGenerator {

    private final static int portalImageAmount = 32;
    private final static int portalImageSize = 16;
    // TODO This doesn't really work for more than a value of 2, see "TODO fix this"
    private final static int spiralAmount = 2;

    private final static boolean addConstantOffset = true;
    private final static boolean addRandomNoise = true;

    @Override
    public String getGeneratorName() {
        return "Nether_Portal";
    }

    @Override
    public TextureGroup[] getTextureGroups() {
        return new TextureGroup[] { netherPortalFrames() };
    }

    /* TODO refactor, document */
    private TextureGroup netherPortalFrames() {
        final BufferedImage[] portalImages = new BufferedImage[portalImageAmount];
        final Random rand = new Random(100L);

        for (int currentPortalImage = 0; currentPortalImage < portalImageAmount; currentPortalImage++) {
            final BufferedImage portalImage = new BufferedImage(portalImageSize, portalImageSize, BufferedImage.TYPE_4BYTE_ABGR);
            final byte[] imageByteData = ((DataBufferByte) portalImage.getRaster().getDataBuffer()).getData();

            for (int portalImageX = 0; portalImageX < portalImageSize; portalImageX++) {
                for (int portalImageY = 0; portalImageY < portalImageSize; portalImageY++) {
                    float currentPixelIntensity = 0.0F;

                    for (int currentSpiral = 0; currentSpiral < spiralAmount; currentSpiral++) {
                        final float currentSpiralOffset = currentSpiral * (portalImageSize / spiralAmount);
                        float currentSpiralX = ((portalImageX - currentSpiralOffset) / portalImageSize) * 2.0f;
                        float currentSpiralY = ((portalImageY - currentSpiralOffset) / portalImageSize) * 2.0f;

                        if (addConstantOffset) {
                            if (currentSpiralX < -1.0F) {
                                currentSpiralX += 2.0F;
                            }

                            if (currentSpiralX >= 1.0F) {
                                currentSpiralX -= 2.0F;
                            }

                            if (currentSpiralY < -1.0F) {
                                currentSpiralY += 2.0F;
                            }

                            if (currentSpiralY >= 1.0F) {
                                currentSpiralY -= 2.0F;
                            }
                        }

                        final float spiralPowerThingy = (currentSpiralX * currentSpiralX) + (currentSpiralY * currentSpiralY);
                        // TODO fix this to actually make more than two spirals work, you can see my nonsensical attempt to do this before I got too confused to continue
                        float currentSpiralIntensity = (float)Math.atan2(currentSpiralY, currentSpiralX) + ((((((float)currentPortalImage / (float)portalImageAmount) * (float) Math.PI * 2.0F) - (spiralPowerThingy * 10.0F)) + (currentSpiral * 2)) * ((currentSpiral * 2) - 1));
                        // float currentSpiralIntensity = (float)Math.atan2(currentSpiralY, currentSpiralX) + ((((((float)currentPortalImage / (float)portalImageAmount) * (float) Math.PI * 2.0F) - (spiralPowerThingy * 10.0F)) + (currentSpiral * spiralAmount)) * ((((currentSpiral % 2) * 2) - 1) * ((currentSpiral + 2) / 2)));
                        currentSpiralIntensity = (lookupSin(currentSpiralIntensity) + 1.0f) / 2.0F;
                        currentSpiralIntensity /= spiralPowerThingy + 1.0F;
                        currentPixelIntensity += currentSpiralIntensity * (1.0F / spiralAmount);
                    }

                    if (addRandomNoise) {
                        currentPixelIntensity += rand.nextFloat() * 0.1F;
                    }

                    // Construct the ABGR components of the image.
                    final int imageOffset = (portalImageX + (portalImageY * portalImageSize)) * 4;
                    /*
                     * Aplha / blue is very common, and has a fairly normal distribution.
                     * The alpha value is the same as the blue value.
                     * This means that the nether portal is the most transparent at the least blue pixels
                     * / the most opaque at the most blue pixels.
                     */
                    final byte blueAndAlpha = (byte) ((currentPixelIntensity * 100.0F) + 155.0F);
                    imageByteData[imageOffset + 0] = blueAndAlpha;
                    imageByteData[imageOffset + 1] = blueAndAlpha;
                    // Green is very unlikely, and has a large amount of variance.
                    imageByteData[imageOffset + 2] = (byte) (currentPixelIntensity * currentPixelIntensity * currentPixelIntensity * currentPixelIntensity * 255.0F);
                    // Red has a fairly large variance, but appears fairly commonly.
                    imageByteData[imageOffset + 3] = (byte) ((currentPixelIntensity * currentPixelIntensity * 200.0F) + 55.0F);
                }
            }

            portalImages[currentPortalImage] = portalImage;
        }

        return new TextureGroup("Nether_Portal_Animation", portalImages);
    }

}
