package com.github.nerdthened.mctexturegen;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.github.nerdthened.mctexturegen.generators.AbstractTextureGenerator;
import com.github.nerdthened.mctexturegen.generators.Classic19aLavaGenerator;
import com.github.nerdthened.mctexturegen.generators.Classic19aWaterGenerator;
import com.github.nerdthened.mctexturegen.generators.Classic22aLavaGenerator;
import com.github.nerdthened.mctexturegen.generators.FireGenerator;
import com.github.nerdthened.mctexturegen.generators.GearRotationFramesGenerator;
import com.github.nerdthened.mctexturegen.generators.MC4k1Generator;
import com.github.nerdthened.mctexturegen.generators.MC4k2Generator;
import com.github.nerdthened.mctexturegen.generators.MissingTextureGenerator;
import com.github.nerdthened.mctexturegen.generators.NetherPortalGenerator;

/**
 * The main program class. Responsible for generating and saving the textures for each implemented generator.
 */
public final class MCTextureGenerator {

    //private static boolean hasDebugInfo = true;

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

    /**
     * The main method. Responsible for generating and saving the textures for each implemented generator.
     *
     * @todo  clean up, make less complex
     * @param args The command line arguments. Recognized options include:
     * <ul>
     * <li>-nonDeterministicFrames (integer n): Generates n frames of each non-deterministic texture. Otherwise, no non-deterministic textures are generated.
     * <li>-randomSeed (long n): Sets the starting seed for each random number generator used by textures generated from non-deterministic generators. Otherwise, a random starting seed is used.
     * <li>-platformTextures (boolean b): If b is true, platform dependant textures are generated. Otherwise, no platform dependant textures are generated. "Platform dependant" can mean that the textures vary depending on the version of Java, the operating system, and / or global settings for the current platform.
     * <li>-multiThreaded (boolean b): If b is false, texture generation will be done consecutively on a single thread. Otherwise, each texture generator will be run on a seperate thread. This should not cause any differences in the generated textures.
     * </ul>
     */
    public static void main(final String[] args) {
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
                    // TODO I have no idea why these threads would ever be interrupted, and I'm not sure how to handle it if they are.
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
