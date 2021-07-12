package mcTextureGen.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mcTextureGen.MCTextureGenerator;
import mcTextureGen.data.TextureGroup;
import mcTextureGen.generators.TextureGenerator;

// TODO refactor tests to use MethodSource
public class MCTextureGeneratorTest {

	@Test
	@DisplayName("Ensure all names of TextureGenerators and TextureGroups only contain characters which are safe to be used in file names")
	void testSafeCharactersInNames() {
		// This regex matches if the whole string only contains alpha-numeric characters and / or underscores.
		final Pattern checkUnsafeCharactersRegex = Pattern.compile("^\\w+$");
		// The Matcher is initially given a dummy value, as it is reused each loop.
		// If it somehow fails to get reset, this ASCII table flip should cause the test to fail.
		// P.S applicants welcome to submit a better ASCII-only table flip (I really tried).
		// Using non-ASCII characters causes Eclipse to space lines weirdly.
		final Matcher checkUnsafeCharacters = checkUnsafeCharactersRegex.matcher("(/@_@/) `` _|__|_");

		final String unsafeCharacterStart = "The name of the ";
		final String unsafeCharacterQuotesStart = " \"";
		final String unsafeCharacterEnd = "\" contained a character which might be potentially unsafe to use in a file name";
		for (final TextureGenerator generator : MCTextureGenerator.getTextureGenerators()) {
			// Re-use Matcher instead of creating a new one for each String
			checkUnsafeCharacters.reset(generator.getGeneratorName());
			// Lambda used for lazy evaluation of error message
			assertTrue(checkUnsafeCharacters.matches(), () -> (unsafeCharacterStart + TextureGenerator.class.getSimpleName() + unsafeCharacterQuotesStart + generator.getGeneratorName() + unsafeCharacterEnd));
			for (final TextureGroup group : generator.getTextureGroups()) {
				// Re-use Matcher instead of creating a new one for each String
				checkUnsafeCharacters.reset(group.textureGroupName);
				// Lambda used for lazy evaluation of error message
				assertTrue(checkUnsafeCharacters.matches(), () -> (unsafeCharacterStart + TextureGroup.class.getSimpleName() + unsafeCharacterQuotesStart  + group.textureGroupName + unsafeCharacterEnd));
			}
		}
	}

}
