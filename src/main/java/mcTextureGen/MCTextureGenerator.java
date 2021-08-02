package mcTextureGen;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import mcTextureGen.data.TextureGroup;
import mcTextureGen.generators.GearRotationFramesGenerator;
import mcTextureGen.generators.MC4k1Generator;
import mcTextureGen.generators.MC4k2Generator;
import mcTextureGen.generators.TextureGenerator;

public final class MCTextureGenerator {

    // private static boolean hasDebugInfo = true;

    public static TextureGenerator[] getTextureGenerators() {
        return new TextureGenerator[] { new MC4k1Generator(), new MC4k2Generator(), new GearRotationFramesGenerator() };
    }

    public static void main(final String[] args) {
        // TODO: Clean up
        final String currentDir = System.getProperty("user.dir");
        final String fileSeperator = System.getProperty("file.separator");
        final String lineSeperator = System.getProperty("line.separator");
        System.out.println(lineSeperator + "MCTextureGenerator" + lineSeperator + "Generates and saves runtime-generated textures from various Minecraft versions." + lineSeperator);
        final String baseTextureOutputPath = currentDir + fileSeperator + "GeneratedTextures";

        for (final TextureGenerator generator : getTextureGenerators()) {
            System.out.println("Generating all texture groups for the texture generator " + generator.getGeneratorName() + lineSeperator);
            final String textureGeneratorOutputPath = baseTextureOutputPath + fileSeperator + generator.getGeneratorName();

            for (final TextureGroup group : generator.getTextureGroups()) {
                final String textureGroupOutputPath = textureGeneratorOutputPath + fileSeperator + group.textureGroupName;
                final File textureGroupDirectory = new File(textureGroupOutputPath);

                if (!textureGroupDirectory.exists() && !textureGroupDirectory.mkdirs()) {
                    System.out.println("Error: Could not create directory for texture group " + group.textureGroupName + ", skipping" + lineSeperator);
                    continue;
                }

                System.out.println("Generating all texures for the texture group " + group.textureGroupName);

                for (int i = 0; i < group.textureImages.length; i++) {
                    final RenderedImage textureImage = group.textureImages[i];
                    final String outFileName = textureGroupOutputPath + fileSeperator + group.textureGroupName + "_" + (i + 1) + ".png";
                    final File textureFile = new File(outFileName);

                    try {
                        ImageIO.write(textureImage, "png", textureFile);
                    } catch (final IOException e) {
                        System.out.println("Error: Failed to save " + group.textureGroupName);
                        e.printStackTrace();
                    }
                }

                System.out.println("Finished generating all texures for the texture group " + group.textureGroupName + lineSeperator);
            }

            System.out.println("Finished generating all texture groups for the texture generator " + generator.getGeneratorName() + lineSeperator);
        }

        System.out.println("All images have been generated and saved!");
    }

}
