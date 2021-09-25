package mcTextureGen.generators;

import java.util.Random;

import mcTextureGen.data.TextureGroup;

/**
 * The base texture generator class. All texture generators extend from this.
 * This is an abstract class (instead of an interface) due to it having helper methods.
 */
public abstract class AbstractTextureGenerator {

    // Global settings

    /** The amount of non-deterministic frames to generate. */
    protected static int nonDeterministicFrames = 0;

    /** The random seed used by {@link #getRandom()}. */
    private static Long randomSeed = null;

    /** True if platform dependent texture generators should generate platform dependent textures. */
    protected static boolean shouldGeneratePlatformDependentTextures = false;

    /*
     * Constants to determine the size of the sin and cos lookup tables.
     * Adapted from https://jvm-gaming.org/t/fast-math-sin-cos-lookup-tables/36660.
     */

    /** The size of the sine table in bits. */
    private static final int SIN_BITS = 16;

    /** A bit mask of the amount of bits in the sine table. */
    private static final int SIN_MASK = ~(-1 << SIN_BITS);

    /** The size of the sine table. */
    private static final int SIN_COUNT = SIN_MASK + 1;

    /** A value to convert from an angle in radians to an index in the sine table. */
    private static final float RADIANS_TO_INDEX = SIN_COUNT / (float) (Math.PI * 2.0);

    /** A lookup table for finding the sine of an angle. */
    private static final float[] SINE_TABLE = new float[SIN_COUNT];

    static {
        for (int i = 0; i < SIN_COUNT; ++i) {
            SINE_TABLE[i] = (float) Math.sin((i * Math.PI * 2.0) / SIN_COUNT);
        }
    }

    /**
     * This should be used in non-deterministic generators
     * when using random number generation which doesn't use a set seed.
     *
     * @return an instance of Random with a set seed from the command line arguments,
     *         or a new Random if a seed value was not passed
     */
    static final Random getRandom() {
        return randomSeed != null ? new Random(randomSeed.longValue()) : new Random();
    }

    /**
     * This method uses a lookup table to find the cosine of the provided angle in radians.
     * Adapted from https://jvm-gaming.org/t/fast-math-sin-cos-lookup-tables/36660.
     *
     * @param  radians the angle in radians to lookup
     * @return the cosine of the angle
     */
    static final float lookupCos(float radians) {
        return SINE_TABLE[(int) ((radians * RADIANS_TO_INDEX) + (SIN_COUNT / 4)) & SIN_MASK];
    }

    /**
     * This method uses a lookup table to find the sine of the provided angle in radians.
     * Adapted from https://jvm-gaming.org/t/fast-math-sin-cos-lookup-tables/36660.
     *
     * @param  radians the angle in radians to lookup
     * @return the sine of the angle
     */
    static final float lookupSin(float radians) {
        return SINE_TABLE[(int) (radians * RADIANS_TO_INDEX) & SIN_MASK];
    }

    // Setters for global settings

    /**
     * Sets the amount of generated non-deterministic frames.
     *
     * @param nonDeterministicFrames the new amount of non-deterministic frames
     */
    public static void setNonDeterministicFrames(int nonDeterministicFrames) {
        AbstractTextureGenerator.nonDeterministicFrames = nonDeterministicFrames;
    }

    /**
     * Sets the seed used by getRandom().
     *
     * @param randomSeed the new random seed
     */
    public static void setRandomSeed(Long randomSeed) {
        AbstractTextureGenerator.randomSeed = randomSeed;
    }

    /**
     * Sets if platform dependent generators should generate textures.
     *
     * @param shouldGeneratePlatformDependentTextures whether platform dependent generators should generate textures
     */
    public static void setShouldGeneratePlatformDependentTextures(boolean shouldGeneratePlatformDependentTextures) {
        AbstractTextureGenerator.shouldGeneratePlatformDependentTextures = shouldGeneratePlatformDependentTextures;
    }

    // Interface methods

    /**
     * Gets the name of this texture generator.
     *
     * @return the generator name
     */
    public abstract String getGeneratorName();

    /**
     * Gets the generated texture groups of this texture generator.
     *
     * @return the generated texture groups
     */
    public abstract TextureGroup[] getTextureGroups();

    /**
     * Only used during tests, signifies that the generator would not be able to generate textures correctly.
     *
     * @todo   probably refactor
     * @return true, if there were no generation issues during testing
     */
    public boolean hasGenerationIssue() {
        return false;
    }

}
