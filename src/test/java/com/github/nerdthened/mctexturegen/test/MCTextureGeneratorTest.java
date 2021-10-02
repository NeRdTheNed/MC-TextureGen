package com.github.nerdthened.mctexturegen.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.nerdthened.mctexturegen.MCTextureGenerator;
import com.github.nerdthened.mctexturegen.data.TextureGroup;
import com.github.nerdthened.mctexturegen.generators.AbstractTextureGenerator;

/**
 * Unit tests for texture generators.
 *
 * @todo refactor
 */
final class MCTextureGeneratorTest {

    /** This regex matches if the whole string only contains alpha-numeric characters and / or underscores. */
    private final static Pattern checkUnsafeCharacterRegex = Pattern.compile("^\\w+$");

    /**
     * This Matcher matches against a String if it only contains safe characters to use in file names.
     * It is initially given a dummy value, as it is reused each loop.
     * If it somehow fails to get reset before it matches, this ASCII table flip should cause the test to fail.
     * P.S applicants are welcome to submit a better ASCII-only table flip (I really tried).
     * Using non-ASCII characters causes Eclipse to space lines weirdly.
     */
    private final static Matcher checkUnsafeCharacters = checkUnsafeCharacterRegex.matcher("(/@_@/) `` _|__|_");

    /** Constant for end of error messages. */
    private final static String unsafeCharacterEnd = "\" contained a character which might be potentially unsafe to use in a file name";

    /** Constant for middle of error messages. */
    private final static String unsafeCharacterQuotesStart = " \"";

    /** Constant for start of error messages. */
    private final static String unsafeCharacterStart = "The name of the ";

    static {
        // Test non-deterministic and platform-dependent generators.
        AbstractTextureGenerator.setNonDeterministicFrames(1024);
        AbstractTextureGenerator.setShouldGeneratePlatformDependentTextures(true);
    }

    /**
     * Checks if the provided String contains only safe characters to use in file names.
     *
     * @param  toCheck the String to check
     * @return true, if the String only contains safe characters to use in file names
     */
    private static boolean isSafeName(String toCheck) {
        return checkUnsafeCharacters.reset(toCheck).matches();
    }

    /**
     * Provides a Stream of all texture generators.
     *
     * @todo   this is bad
     * @return a Stream of all texture generators
     */
    private static Stream<AbstractTextureGenerator> textureGeneratorProvider() {
        return Stream.of(MCTextureGenerator.getTextureGenerators());
    }

    /**
     * Provides a Stream of all texture groups from all texture generators.
     *
     * @todo   this is bad
     * @return a Stream of all texture groups from all texture generators
     */
    private static Stream<TextureGroup> textureGroupProvider() {
        return Stream.of(MCTextureGenerator.getTextureGenerators()).map(AbstractTextureGenerator::getTextureGroups).flatMap(Stream::of);
    }

    /**
     * Tests if any AbstractTextureGenerator reports generation issues via hasGenerationIssue().
     *
     * @todo  refactor
     * @param generator the generator
     */
    @ParameterizedTest
    @MethodSource("textureGeneratorProvider")
    @DisplayName("Test if any AbstractTextureGenerator reports generation errors.")
    void testGenerationIssues(AbstractTextureGenerator generator) {
        generator.getTextureGroups();
        assertFalse(generator.hasGenerationIssue(), () -> ("The " + AbstractTextureGenerator.class.getSimpleName() + " \"" + generator.getGeneratorName() + "\" has an unspecified texture generation issue."));
    }

    /**
     * Ensure all names of AbstractTextureGenerators only contain characters which are safe to be used in file names.
     *
     * @param generator the provided texture generator
     */
    @ParameterizedTest
    @MethodSource("textureGeneratorProvider")
    @DisplayName("Ensure all names of AbstractTextureGenerators only contain characters which are safe to be used in file names")
    void testSafeCharactersInTextureGeneratorNames(AbstractTextureGenerator generator) {
        assertTrue(isSafeName(generator.getGeneratorName()), () -> (unsafeCharacterStart + AbstractTextureGenerator.class.getSimpleName() + unsafeCharacterQuotesStart + generator.getGeneratorName() + unsafeCharacterEnd));
    }

    /**
     * Ensure all names of TextureGroups only contain characters which are safe to be used in file names.
     *
     * @param group the provided texture group
     */
    @ParameterizedTest
    @MethodSource("textureGroupProvider")
    @DisplayName("Ensure all names of TextureGroups only contain characters which are safe to be used in file names")
    void testSafeCharactersInTextureGroupNames(TextureGroup group) {
        assertTrue(isSafeName(group.textureGroupName), () -> (unsafeCharacterStart + TextureGroup.class.getSimpleName() + unsafeCharacterQuotesStart  + group.textureGroupName + unsafeCharacterEnd));
    }

}
