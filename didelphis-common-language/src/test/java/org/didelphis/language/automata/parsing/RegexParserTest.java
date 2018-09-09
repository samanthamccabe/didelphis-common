package org.didelphis.language.automata.parsing;

import org.didelphis.language.automata.expressions.Expression;
import org.junit.jupiter.api.Test;

/**
 * Class {@code RegexParserTest}
 *
 * @author Samantha Fiona McCabe
 * @since 0.3.0
 */
public class RegexParserTest {

	private RegexParser regexParser = new RegexParser();
	
	
	@Test
	void testParse() {
		
		String string = "abc";

		Expression expression = regexParser.parseExpression(string);
		
		
	}

	@Test
	void testParseParallel() {

		String string = "a|b|c";

		Expression expression = regexParser.parseExpression(string);


	}

	@Test
	void testParseSquareBrackets() {

		String string = "[abc]";

		Expression expression = regexParser.parseExpression(string);


	}
}
