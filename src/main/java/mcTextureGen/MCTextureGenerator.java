package mcTextureGen;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import mcTextureGen.data.TextureGroup;
import mcTextureGen.generators.AbstractTextureGenerator;
import mcTextureGen.generators.Classic19aLavaGenerator;
import mcTextureGen.generators.Classic19aWaterGenerator;
import mcTextureGen.generators.Classic22aLavaGenerator;
import mcTextureGen.generators.FireGenerator;
import mcTextureGen.generators.GearRotationFramesGenerator;
import mcTextureGen.generators.MC4k1Generator;
import mcTextureGen.generators.MC4k2Generator;
import mcTextureGen.generators.MissingTextureGenerator;
import mcTextureGen.generators.NetherPortalGenerator;

public final class MCTextureGenerator {

    // private static boolean hasDebugInfo = true;

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

    public static void main(final String[] args) {
        // TODO: Clean up
        final Logger log = Logger.getLogger("MCTextureGenerator");
        log.log(Level.INFO, "MCTextureGenerator: Generates and saves runtime-generated textures from various Minecraft versions.");
        log.log(Level.FINE, "Setting up GraphicsEnvironment");

        if (args.length > 0) {
            if ((args.length % 2) == 0) {
                for (int i = 0; i < args.length; i += 2) {
                    try {
                        if ("-nonDeterministicFrames".equals(args[i])) {
                            AbstractTextureGenerator.setNonDeterministicFrames(Integer.parseInt(args[i + 1]));
                        } else if ("-randomSeed".equals(args[i])) {
                            AbstractTextureGenerator.setRandomSeed(Long.valueOf(args[i + 1]));
                        } else {
                            log.log(Level.SEVERE, "Invalid command line parameter {0} provided", args[i]);
                            System.exit(1);
                        }
                    } catch (final NumberFormatException e) {
                        final LogRecord logRecord = new LogRecord(Level.SEVERE, "The command line parameter for {0} was not able to be parsed");
                        logRecord.setParameters(new Object[] { args[i] });
                        logRecord.setThrown(e);
                        log.log(logRecord);
                        System.exit(1);
                    }
                }
            } else {
                log.log(Level.SEVERE, "An incorrect amount of command line parameters was provided");
                System.exit(1);
            }
        }

        final String currentDir = System.getProperty("user.dir");
        final String fileSeperator = System.getProperty("file.separator");
        final String baseTextureOutputPath = currentDir + fileSeperator + "GeneratedTextures";

        for (int i = 0; i < getTextureGenerators().length; i++) {
            final AbstractTextureGenerator generator = getTextureGenerators()[i];
            log.log(Level.INFO, "Generating all texture groups for the texture generator {0}", generator.getGeneratorName());
            final String textureGeneratorOutputPath = baseTextureOutputPath + fileSeperator + generator.getGeneratorName();

            for (int j = 0; j < generator.getTextureGroups().length; j++) {
                final TextureGroup group = generator.getTextureGroups()[j];

                if (group.textureImages.length == 0) {
                    log.log(Level.WARNING, "Group {0} did not contain any textures, skipping", group.textureGroupName);
                    continue;
                }

                final String textureGroupOutputPath = textureGeneratorOutputPath + fileSeperator + group.textureGroupName;
                final File textureGroupDirectory = new File(textureGroupOutputPath);

                if (!textureGroupDirectory.exists() && !textureGroupDirectory.mkdirs()) {
                    log.log(Level.WARNING, "Could not create directory for texture group {0}, skipping", group.textureGroupName);
                    continue;
                }

                log.log(Level.INFO, "Generating all texures for the texture group {0}", group.textureGroupName);

                for (int k = 0; k < group.textureImages.length; k++) {
                    final RenderedImage textureImage = group.textureImages[k];
                    final String outFileName;

                    if (group.textureImages.length > 1) {
                        outFileName = textureGroupOutputPath + fileSeperator + group.textureGroupName + "_" + (k + 1) + ".png";
                    } else {
                        outFileName = textureGroupOutputPath + fileSeperator + group.textureGroupName + ".png";
                    }

                    final File textureFile = new File(outFileName);

                    try {
                        ImageIO.write(textureImage, "png", textureFile);
                    } catch (final IOException e) {
                        final LogRecord logRecord = new LogRecord(Level.WARNING, "Failed to save {0}");
                        logRecord.setParameters(new Object[] { group.textureGroupName });
                        logRecord.setThrown(e);
                        log.log(logRecord);
                    }
                }

                log.log(Level.INFO, "Finished generating all texures for the texture group {0}", group.textureGroupName);
            }

            log.log(Level.INFO, "Finished generating all texture groups for the texture generator {0}", generator.getGeneratorName());
        }

        log.log(Level.INFO, "All images have been generated and saved!");
    }

}
