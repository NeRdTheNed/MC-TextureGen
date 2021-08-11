package mcTextureGen;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import mcTextureGen.data.TextureGroup;
import mcTextureGen.generators.AbstractTextureGenerator;
import mcTextureGen.generators.GearRotationFramesGenerator;
import mcTextureGen.generators.MC4k1Generator;
import mcTextureGen.generators.MC4k2Generator;
import mcTextureGen.generators.NetherPortalGenerator;

public final class MCTextureGenerator {

    // private static boolean hasDebugInfo = true;

    public static AbstractTextureGenerator[] getTextureGenerators() {
        return new AbstractTextureGenerator[] { new MC4k1Generator(), new MC4k2Generator(), new GearRotationFramesGenerator(), new NetherPortalGenerator() };
    }

    public static void main(final String[] args) {
        // TODO: Clean up
        if (args.length > 0) {
            if ((args.length % 2) == 0) {
                for (int i = 0; i < args.length; i += 2) {
                    try {
                        if ("-nonDeterministicFrames".equals(args[i])) {
                            AbstractTextureGenerator.nonDeterministicFrames = Integer.parseInt(args[i + 1]);
                        } else if ("-randomSeed".equals(args[i])) {
                            AbstractTextureGenerator.randomSeed = Long.parseLong(args[i + 1]);
                        } else {
                            System.out.println("Error: Invalid command line parameter " + args[i] + " provided");
                            System.exit(1);
                        }
                    } catch (final Exception e) {
                        System.out.println("Error: The command line parameter for " + args[i] + " was not able to be parsed");
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            } else {
                System.out.println("Error: An incorrect amount of command line parameters was provided");
                System.exit(1);
            }
        }

        final String currentDir = System.getProperty("user.dir");
        final String fileSeperator = System.getProperty("file.separator");
        final String lineSeperator = System.getProperty("line.separator");
        System.out.println(lineSeperator + "MCTextureGenerator" + lineSeperator + "Generates and saves runtime-generated textures from various Minecraft versions." + lineSeperator);
        final String baseTextureOutputPath = currentDir + fileSeperator + "GeneratedTextures";

        for (final AbstractTextureGenerator generator : getTextureGenerators()) {
            System.out.println("Generating all texture groups for the texture generator " + generator.getGeneratorName() + lineSeperator);
            final String textureGeneratorOutputPath = baseTextureOutputPath + fileSeperator + generator.getGeneratorName();

            for (final TextureGroup group : generator.getTextureGroups()) {
                if (group.textureImages.length == 0) {
                    System.out.println("Warning: Group " + group.textureGroupName + " did not contain any textures, skipping" + lineSeperator);
                    continue;
                }

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
