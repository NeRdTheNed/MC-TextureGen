package mcTextureGen.generators;

import mcTextureGen.data.TextureGroup;

public abstract class TextureGenerator {

    public abstract String getGeneratorName();

    public abstract TextureGroup[] getTextureGroups();

}
