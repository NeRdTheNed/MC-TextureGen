package mcTextureGen.data;

import java.awt.image.RenderedImage;

public class TextureGroup {

    public final String textureGroupName;
    public final RenderedImage[] textureImages;

    public TextureGroup(String textureName, RenderedImage textureImage) {
        textureGroupName = textureName;
        textureImages = new RenderedImage[] {textureImage};
    }
    public TextureGroup(String textureName, RenderedImage[] textureImages) {
        textureGroupName = textureName;
        this.textureImages = textureImages;
    }


}
