package mcTextureGen.generators;

import java.util.Random;

import mcTextureGen.data.TextureGroup;

public abstract class AbstractTextureGenerator {

    protected static int nonDeterministicFrames = 0;
    private static Long randomSeed = null;
    protected static boolean shouldGeneratePlatformDependantTextures = false;

    private static final Random cachedRand = new Random();

    public static void setNonDeterministicFrames(int nonDeterministicFrames) {
        AbstractTextureGenerator.nonDeterministicFrames = nonDeterministicFrames;
    }

    public static void setRandomSeed(Long randomSeed) {
        AbstractTextureGenerator.randomSeed = randomSeed;
    }

    public static void setShouldGeneratePlatformDependantTextures(boolean shouldGeneratePlatformDependantTextures) {
        AbstractTextureGenerator.shouldGeneratePlatformDependantTextures = shouldGeneratePlatformDependantTextures;
    }

    public abstract String getGeneratorName();

    public abstract TextureGroup[] getTextureGroups();

    // Only used during tests, signifies that the generator would not be able to generate a texture correctly.
    // TODO probably refactor
    public boolean hasGenerationIssue() {
        return false;
    }

    /** Returns an instance of Random with a set seed from the command line arguments, or a cached Random if a seed value was not passed. */
    static final Random getRandom() {
        return randomSeed != null ? new Random(randomSeed.longValue()) : cachedRand;
    }

    // Math utilities

    /*
     * Lookup tables for sin and cos, mainly adapted from
     * https://jvm-gaming.org/t/fast-math-sin-cos-lookup-tables/36660
     */
    private static final int SIN_BITS = 16;
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_COUNT = SIN_MASK + 1;

    private static final float RADIANS_TO_INDEX = SIN_COUNT / (float) (Math.PI * 2.0);

    private static final float[] SINE_TABLE = new float[SIN_COUNT];

    static final float lookupCos(float radians) {
        return SINE_TABLE[(int) ((radians * RADIANS_TO_INDEX) + (SIN_COUNT / 4)) & SIN_MASK];
    }

    static final float lookupSin(float radians) {
        return SINE_TABLE[(int) (radians * RADIANS_TO_INDEX) & SIN_MASK];
    }

    static {
        for (int i = 0; i < SIN_COUNT; ++i) {
            SINE_TABLE[i] = (float) Math.sin((i * Math.PI * 2.0) / SIN_COUNT);
        }
    }

}
