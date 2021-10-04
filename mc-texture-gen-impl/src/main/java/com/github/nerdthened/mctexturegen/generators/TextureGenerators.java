package com.github.nerdthened.mctexturegen.generators;

/**
 * A utility class to get all implemented texture generators.
 */
public class TextureGenerators {

    /**
     * Private constructor, to hide the default visible one.
     * There should be no reason to ever create an instance of this class.
     */
    private TextureGenerators() {}

    /**
     * Creates an array of all implemented texture generators.
     *
     * @return an array of all implemented texture generators
     */
    public static AbstractTextureGenerator[] getTextureGenerators() {
        return new AbstractTextureGenerator[] {
                   new MissingTextureGenerator(),
                   new MC4k1Generator(),
                   new MC4k2Generator(),
                   new GearRotationFramesGenerator(),
                   new NetherPortalGenerator(),
                   new Classic19aWaterGenerator(),
                   new Classic19aLavaGenerator(),
                   new Classic22aLavaGenerator(),
                   new FireGenerator()
               };
    }

}
