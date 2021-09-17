package mcTextureGen.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import mcTextureGen.MCTextureGenerator;
import mcTextureGen.data.TextureGroup;
import mcTextureGen.generators.AbstractTextureGenerator;

// TODO refactor
final class MCTextureGeneratorTest {

    static {
        MCTextureGenerator.setupGraphics();
        AbstractTextureGenerator.setNonDeterministicFrames(1024);
    }

    // TODO this is bad
    private static Stream<AbstractTextureGenerator> textureGeneratorProvider() {
        return Stream.of(MCTextureGenerator.getTextureGenerators());
    }

    // TODO this is bad
    private static Stream<TextureGroup> textureGroupProvider() {
        return Stream.of(MCTextureGenerator.getTextureGenerators()).map(AbstractTextureGenerator::getTextureGroups).flatMap(Stream::of);
    }

    // This regex matches if the whole string only contains alpha-numeric characters and / or underscores.
    private final static Pattern checkUnsafeCharactersRegex = Pattern.compile("^\\w+$");
    // The Matcher is initially given a dummy value, as it is reused each loop.
    // If it somehow fails to get reset, this ASCII table flip should cause the test to fail.
    // P.S applicants welcome to submit a better ASCII-only table flip (I really tried).
    // Using non-ASCII characters causes Eclipse to space lines weirdly.
    private final static Matcher checkUnsafeCharacters = checkUnsafeCharactersRegex.matcher("(/@_@/) `` _|__|_");

    private final static String unsafeCharacterStart = "The name of the ";
    private final static String unsafeCharacterQuotesStart = " \"";
    private final static String unsafeCharacterEnd = "\" contained a character which might be potentially unsafe to use in a file name";

    private static boolean isSafeName(String toCheck) {
        return checkUnsafeCharacters.reset(toCheck).matches();
    }

    @ParameterizedTest
    @MethodSource("textureGeneratorProvider")
    @DisplayName("Test if any TextureGenerator reports generation errors.")
    void testGenerationIssues(AbstractTextureGenerator generator) {
        assertFalse(generator.hasGenerationIssue(), () -> ("The " + AbstractTextureGenerator.class.getSimpleName() + " \"" + generator.getGeneratorName() + "\" has an unspecified texture generation issue."));
    }

    @ParameterizedTest
    @MethodSource("textureGeneratorProvider")
    @DisplayName("Ensure all names of TextureGenerators only contain characters which are safe to be used in file names")
    void testSafeCharactersInTextureGeneratorNames(AbstractTextureGenerator generator) {
        assertTrue(isSafeName(generator.getGeneratorName()), () -> (unsafeCharacterStart + AbstractTextureGenerator.class.getSimpleName() + unsafeCharacterQuotesStart + generator.getGeneratorName() + unsafeCharacterEnd));
    }

    @ParameterizedTest
    @MethodSource("textureGroupProvider")
    @DisplayName("Ensure all names of TextureGenerators only contain characters which are safe to be used in file names")
    void testSafeCharactersInTextureGroupNames(TextureGroup group) {
        assertTrue(isSafeName(group.textureGroupName), () -> (unsafeCharacterStart + TextureGroup.class.getSimpleName() + unsafeCharacterQuotesStart  + group.textureGroupName + unsafeCharacterEnd));
    }

}
