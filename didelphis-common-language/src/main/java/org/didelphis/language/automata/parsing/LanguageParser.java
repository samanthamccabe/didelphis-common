/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.language.automata.parsing;

import lombok.NonNull;

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.structures.graph.Arc;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface {@code Language Parser}
 *
 * @param <S>
 *
 * @since 0.2.0
 */
public interface LanguageParser<S> {

	@NonNull Map<String, String> supportedDelimiters();

	@NonNull Set<String> supportedQuantifiers();

	/**
	 * Transform an expression string into a corresponding terminal symbol, the
	 * same as is consumed from an input while searching for a match.
	 * <p>
	 * Used in the construction of state machines, specifically when parsing
	 * terminal symbols into objects of type {@code <T>} which form the state
	 * transitions .
	 *
	 * @param expression
	 *
	 * @return
	 */
	@NonNull S transform(String expression);

	@NonNull Arc<S> getArc(String arc);

	/**
	 * Parse an expression string to a list of sub-expressions
	 *
	 * @param expression a string representation of an expression to be parsed
	 *
	 * @return an expression object parsed from the original string
	 *
	 * @throws ParseException if any invalid inputs are found
	 */
	@NonNull Expression parseExpression(
			@NonNull String expression, @NonNull ParseDirection direction
	);

	/**
	 * Provides a uniform value for epsilon transitions
	 *
	 * @return a uniform value for epsilon transitions
	 */
	@NonNull Arc<S> epsilon();

	/**
	 * Provides a collection of supported special symbols and their
	 * corresponding literal values
	 *
	 * @return a collection of supported special symbols and their
	 * 		corresponding literal values
	 */
	@NonNull MultiMap<String, S> getSpecialsMap();

	/**
	 * Provides a uniform value for "dot" transitions, which accept any value,
	 * corresponding to "." in traditional regular expression languages
	 *
	 * @return a uniform value for "dot" transitions, which accept any value
	 */
	@NonNull Arc<S> getDot();

	/**
	 * Determines the length of the provided element, where applicable. In some
	 * implementations, this may simply be 1 in all cases.
	 *
	 * @param t the data element whose length is to be determined
	 *
	 * @return the length of the provided element
	 */
	int lengthOf(@NonNull S t);

	/**
	 * Allows type-agnostic retrieval of a subsequence
	 *
	 * @param sequence the sequence from which to take a subsequence
	 * @param start the start index
	 * @param end the end index
	 *
	 * @return a subsequence of the original sequence; not null
	 *
	 * @throws IndexOutOfBoundsException if either {@param start} or {@param
	 *        end} are negative, or greater than or equal to the length of the
	 * 		provided sequence
	 */
	@NonNull S subSequence(@NonNull S sequence, int start, int end);

	/**
	 * Concatenates two sequences into a third, new sequence
	 *
	 * @param sequence1 a sequence to be concatenated; not null
	 * @param sequence2 a sequence to be concatenated; not null
	 *
	 * @return a new object of type {@code <S>}; it should not be a mutated instance
	 * 		of either of the provided sequences
	 */
	@NonNull S concatenate(@NonNull S sequence1, @NonNull S sequence2);

	/**
	 * Replaces group markers like {@code $1} with the corresponding group from
	 * the match
	 *
	 * @param input a sequence including group markers
	 * @param match a match object
	 *
	 * @return a new sequence where the group markers are replaced with their
	 * 		corresponding matched content
	 */
	@NonNull S replaceGroups(@NonNull S input, @NonNull Match<S> match);

	/**
	 * Parse an expression string into the matching expression object
	 *
	 * @param exp a string representing an expression to be parsed; not-null
	 *
	 * @return an expression object described by the provided string; not-null
	 */
	@NonNull
	default Expression parseExpression(@NonNull String exp) {
		return parseExpression(exp, ParseDirection.FORWARD);
	}

	/**
	 * Attempts to retrieve the children from an expression; if no children are
	 * found, it will return a list containing only the expression itself
	 *
	 * @param exp an expression to be checked for children; not null
	 *
	 * @return a list of the expression's children or, if no children are
	 * 		present, a list containing the expression itself; not-null
	 */
	@NonNull
	static List<Expression> getChildrenOrExpression(@NonNull Expression exp) {
		if (exp.hasChildren() && !(exp.isNegative() || exp.isParallel()) &&
				exp.getQuantifier().isEmpty()) {
			return exp.getChildren();
		} else {
			List<Expression> list = new ArrayList<>();
			list.add(exp);
			return list;
		}
	}

	@NonNull
	static ParserBuffer update(
			@NonNull ParserBuffer buffer,
			@NonNull Collection<Expression> children
	) {
		if (buffer.isEmpty()) {
			return buffer;
		} else {
			Expression ex = buffer.toExpression();
			if (ex == null) {
				String message = Templates.create()
						.add("Unable to convert buffer to expression")
						.data(buffer)
						.build();
				throw new ParseException(message);
			}
			children.add(ex);
			return new ParserBuffer();
		}
	}
}
