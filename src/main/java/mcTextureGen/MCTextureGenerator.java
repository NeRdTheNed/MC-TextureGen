package mcTextureGen;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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

    public static void main(final String[] args) {
        // TODO: Clean up
        final Logger log = Logger.getLogger("MCTextureGenerator");
        log.log(Level.INFO, "MCTextureGenerator: Generates and saves runtime-generated textures from various Minecraft versions.");
        boolean isMultiThreaded = true;

        if (args.length > 0) {
            if ((args.length % 2) == 0) {
                for (int i = 0; i < args.length; i += 2) {
                    try {
                        if ("-nonDeterministicFrames".equals(args[i])) {
                            AbstractTextureGenerator.setNonDeterministicFrames(Integer.parseInt(args[i + 1]));
                        } else if ("-randomSeed".equals(args[i])) {
                            AbstractTextureGenerator.setRandomSeed(Long.valueOf(args[i + 1]));
                        } else if ("-platformTextures".equals(args[i])) {
                            AbstractTextureGenerator.setShouldGeneratePlatformDependentTextures(Boolean.valueOf(args[i + 1]).booleanValue());
                        } else if ("-multiThreaded".equals(args[i])) {
                            isMultiThreaded = Boolean.valueOf(args[i + 1]).booleanValue();
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

        if (isMultiThreaded) {
            log.log(Level.INFO, "Using multi-threaded texture generation");
        }

        final String baseTextureOutputPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "GeneratedTextures";
        final AbstractTextureGenerator[] textureGenerators = getTextureGenerators();
        final int textureGeneratorAmount = textureGenerators.length;
        final Thread[] generatorThreads = isMultiThreaded ? new Thread[textureGeneratorAmount] : null;

        for (int i = 0; i < textureGeneratorAmount; i++) {
            final AbstractTextureGenerator generator = textureGenerators[i];
            final SaveTextureGeneratorResultsTask task = new SaveTextureGeneratorResultsTask(baseTextureOutputPath, generator, log);

            if (isMultiThreaded) {
                final Thread taskThread = new Thread(task, generator.getGeneratorName() + " generator thread");
                taskThread.start();
                generatorThreads[i] = taskThread;
            } else {
                task.run();
            }
        }

        // When using multi-threaded texture generation, wait for all threads to finish generating before printing the end message.
        if (isMultiThreaded) {
            for (int i = 0; i < textureGeneratorAmount; i++) {
                final Thread taskThread = generatorThreads[i];

                try {
                    taskThread.join();
                } catch (final InterruptedException e) {
                    final LogRecord logRecord = new LogRecord(Level.WARNING, "An InterruptedException was thrown when trying to join {0}. Textures may not have been generated.");
                    logRecord.setParameters(new Object[] { taskThread.getName() });
                    logRecord.setThrown(e);
                    log.log(logRecord);
                }
            }
        }

        log.log(Level.INFO, "All images have been generated and saved!");
    }

}
