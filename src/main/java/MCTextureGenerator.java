import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import javax.imageio.ImageIO;

import mcTextureGen.data.TextureGroup;
import mcTextureGen.generators.MC4k1Generator;
import mcTextureGen.generators.MC4k2Generator;
import mcTextureGen.generators.TextureGenerator;

public final class MCTextureGenerator {

    // TODO Find a better way to do this with ProGuard
    private static boolean hasDebugInfo = true;

    private static TextureGenerator[] getTextureGenerators() {
        return new TextureGenerator[] {new MC4k1Generator(), new MC4k2Generator()};
    }

    public static void main(final String[] args) {
        // TODO: Clean up
        System.out.println(System.lineSeparator() + "MCTextureGenerator" + System.lineSeparator() + "Generates and saves runtime-generated textures from various Minecraft versions." + System.lineSeparator());
        final String currentDir = System.getProperty("user.dir");
        final FileSystem fileSystem = FileSystems.getDefault();
        final String baseTextureOutputPath = currentDir + fileSystem.getSeparator() + "GeneratedTextures";

        for (final TextureGenerator generator : getTextureGenerators()) {
            // TODO Proper tests
            if (hasDebugInfo) {
                assert (generator.getGeneratorName().equals(generator.getGeneratorName().replaceAll("[^a-zA-Z0-9-_\\.]", ""))) : "Invalid generator name";
            }

            System.out.println("Generating all texture groups for the texture generator " + generator.getGeneratorName() + "..." + System.lineSeparator());
            final String textureGeneratorOutputPath = baseTextureOutputPath + fileSystem.getSeparator() + generator.getGeneratorName();

            for (final TextureGroup group : generator.getTextureGroups()) {
                // TODO Proper tests
                if (hasDebugInfo) {
                    assert (group.textureGroupName.equals(group.textureGroupName.replace(' ', '_').replaceAll("[^a-zA-Z0-9-_\\.]", ""))) : "Invalid generator name";
                }

                final String textureGroupOutputPath = textureGeneratorOutputPath + fileSystem.getSeparator() + group.textureGroupName;
                System.out.println("Generating all texures in the texture group " + group.textureGroupName + "...");
                verifyDirectory(textureGroupOutputPath);

                for (int i = 1; i < (group.textureImages.length + 1); i++) {
                    final RenderedImage textureImage = group.textureImages[i - 1];
                    final String outFileName = textureGroupOutputPath + fileSystem.getSeparator() + group.textureGroupName + "_" + i + ".png";
                    final File textureFile = new File(outFileName);

                    try {
                        ImageIO.write(textureImage, "png", textureFile);
                    } catch (final IOException e) {
                        System.out.println("Failed to save " + group.textureGroupName + ":");
                        e.printStackTrace();
                    }
                }

                System.out.println("Finished generating all texures in the texture group " + group.textureGroupName + "." + System.lineSeparator());
            }

            System.out.println("Finished generating all texture groups for the texture generator " + generator.getGeneratorName() + "." + System.lineSeparator());
        }

        System.out.println("All images have been generated and saved!");
    }

    private static void verifyDirectory(final String path) {
        final File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }
    }

}
