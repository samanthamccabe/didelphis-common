/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.language.automata.parsing;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.expressions.ParallelNode;
import org.didelphis.language.automata.expressions.ParentNode;
import org.didelphis.language.automata.expressions.TerminalNode;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.structures.graph.Arc;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Templates;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface {@code Language Parser}
 * @param <S>
 *     
 * @since 0.2.0
 */
public interface LanguageParser<S> {

	@NonNull 
	Map<String, String> supportedDelimiters();

	@NonNull
	Set<String> supportedQuantifiers();

	/**
	 * Transform an expression string into a corresponding terminal symbol, the
	 * same as is consumed from an input while searching for a match.
	 * 
	 * Used in the construction of state machines, specifically when parsing
	 * terminal symbols into objects of type {@code <T>} which form the state
	 * transitions .
	 * 
	 * @param expression
	 * @return
	 */
	@NonNull
	S transform(String expression);

	@NonNull
	Arc<S> getArc(String arc);
	
	/**
	 * Parse an expression string to a list of sub-expressions
	 * @param expression
	 * @return
	 */
	@NonNull
	Expression parseExpression(
			@NonNull String expression, 
			@NonNull ParseDirection direction);

	/**
	 * Provides a uniform value for epsilon transitions 
	 * @return a uniform value for epsilon transitions 
	 */
	@Nullable Arc<S> epsilon();

	/**
	 * Provides a collection of supported special symbols and their
	 * corresponding literal values
	 * @return a collection of supported special symbols and their
	 * corresponding literal values
	 */
	@NonNull
	MultiMap<String, S> getSpecialsMap();

	/**
	 * Provides a uniform value for "dot" transitions, which accept any value,
	 * corresponding to "." in traditional regular expression languages
	 * @return a uniform value for "dot" transitions, which accept any value
	 */
	@NonNull
	Arc<S> getDot();

	/**
	 * Determines the length of the provided element, where applicable. In some
	 * implementations, this may simply be 1 in all cases.
	 * @param t the data element whose length is to be determined
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
	 * 		end} are negative, or greater than or equal to the length of the
	 * 		provided sequence
	 */
	@NonNull
	S subSequence(@NonNull S sequence, int start, int end);

	/**
	 * It is critical that this function return a new object and to not mutate
	 * and return {@param sequence1}. 
	 * @param sequence1
	 * @param sequence2
	 * @return
	 */
	@NonNull
	S concatenate(@NonNull S sequence1, @NonNull S sequence2);
	
	@NonNull
	S replaceGroups(@NonNull S input, @NonNull Match<S> match);
	
	default Expression parseExpression(String exp) {
		return parseExpression(exp, ParseDirection.FORWARD);
	}

	@Data
	@FieldDefaults (level = AccessLevel.PRIVATE)
	final class Buffer {

		boolean negative;
		boolean parallel;
		boolean capturing;

		String quantifier = "";
		String terminal = "";

		List<Expression> nodes = new ArrayList<>();

		public boolean isEmpty() {
			return nodes.isEmpty() && terminal.isEmpty();
		}

		public @Nullable Expression toExpression() {
			if (nodes.isEmpty()) {
				return new TerminalNode(terminal, quantifier, negative);
			} else if (parallel) {
				return new ParallelNode(nodes, quantifier, negative);
			} else if (terminal == null || terminal.isEmpty()) {
				return new ParentNode(nodes, quantifier, negative, capturing);
			} else {
				return null;
			}
		}
	}

	@NonNull
	static List<Expression> getChildrenOrExpression(@NonNull Expression exp) {
		if (exp.hasChildren() && !(exp.isNegative() || exp.isParallel()) && exp.getQuantifier().isEmpty()) {
			return exp.getChildren();
		} else {
			List<Expression> list = new ArrayList<>();
			list.add(exp);
			return list;
		}
	}
	
	@NonNull
	static Buffer update(
			@NonNull Buffer buffer,
			@NonNull Collection<Expression> children
	) {
		if (!buffer.isEmpty()) {
			Expression ex = buffer.toExpression();
			if (ex == null) {
				String message = Templates.create()
						.add("Unable to convert buffer to expression")
						.data(buffer)
						.build();
				throw new ParseException(message);
			}
			children.add(ex);
			return new Buffer();
		} else {
			return buffer;
		}
	}
}
