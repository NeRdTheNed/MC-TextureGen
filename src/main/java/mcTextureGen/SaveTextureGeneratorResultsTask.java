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

/**
 * This class is used to generate and save the textures for a texture generator.
 */
public final class SaveTextureGeneratorResultsTask implements Runnable {

    /** The system value for the file separator string. */
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /** The base texture output path. */
    private final String baseTextureOutputPath;

    /** The texture generator to generate textures for. */
    private final AbstractTextureGenerator generator;

    /** The Logger to log to. */
    private final Logger log;

    /**
     * Creates a new SaveTextureGeneratorResultsTask with the specified output path, texture generator, and logger.
     *
     * @param baseTextureOutputPath the base texture output path (the generated textures are saved to subdirectories)
     * @param generator             the texture generator to generate textures for
     * @param log                   the Logger to log to
     */
    public SaveTextureGeneratorResultsTask(String baseTextureOutputPath, AbstractTextureGenerator generator, Logger log) {
        this.baseTextureOutputPath = baseTextureOutputPath;
        this.generator = generator;
        this.log = log;
    }

    /**
     * Generates all textures for a texture generator, then saves them to the provided directory.
     */
    public void run() {
        log.log(Level.INFO, "Generating all texture groups for the texture generator {0}", generator.getGeneratorName());
        final String textureGeneratorOutputPath = baseTextureOutputPath + FILE_SEPARATOR + generator.getGeneratorName();
        final TextureGroup[] textureGroups = generator.getTextureGroups();

        for (int i = 0; i < textureGroups.length; i++) {
            final TextureGroup group = textureGroups[i];

            if (group.textureImages.length == 0) {
                log.log(Level.WARNING, "Group {0} did not contain any textures, skipping", group.textureGroupName);
                continue;
            }

            final String textureGroupOutputPath = textureGeneratorOutputPath + FILE_SEPARATOR + group.textureGroupName;
            final File textureGroupDirectory = new File(textureGroupOutputPath);

            if (!textureGroupDirectory.exists() && !textureGroupDirectory.mkdirs()) {
                log.log(Level.WARNING, "Could not create directory for texture group {0}, skipping", group.textureGroupName);
                continue;
            }

            log.log(Level.INFO, "Generating all texures for the texture group {0}", group.textureGroupName);

            for (int j = 0; j < group.textureImages.length; j++) {
                final RenderedImage textureImage = group.textureImages[j];
                final String outFileName;

                if (group.textureImages.length > 1) {
                    outFileName = textureGroupOutputPath + FILE_SEPARATOR + group.textureGroupName + "_" + (j + 1) + ".png";
                } else {
                    outFileName = textureGroupOutputPath + FILE_SEPARATOR + group.textureGroupName + ".png";
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

}
