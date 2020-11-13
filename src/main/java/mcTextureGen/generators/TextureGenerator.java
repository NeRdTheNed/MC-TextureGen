package mcTextureGen.generators;

import mcTextureGen.data.TextureGroup;

public interface TextureGenerator {

    String getGeneratorName();
    TextureGroup[] getTextureGroups();

}
