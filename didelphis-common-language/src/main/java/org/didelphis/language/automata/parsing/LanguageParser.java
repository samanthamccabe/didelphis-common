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

import lombok.NonNull;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface {@code Language Parser}
 * @param <T>
 *     
 * @since 0.2.0
 * @date 2017-10-25
 */
public interface LanguageParser<T> {

	@NonNull 
	Map<String, String> supportedDelimiters();

	@NonNull
	Set<String> supportedQuantifiers();
	
	@NonNull
	T getWordStart();

	@NonNull
	T getWordEnd();

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
	T transform(String expression);

	/**
	 * Parse an expression string to a list of sub-expressions
	 * @param exp
	 * @return
	 */
	@NonNull
	Expression parseExpression(String exp, ParseDirection direction);

	/**
	 * Provides a uniform value for epsilon transitions 
	 * @return a uniform value for epsilon transitions 
	 */
	@Nullable
	T epsilon();

	/**
	 * Provides a collection of supported special symbols and their
	 * corresponding literal values
	 * @return a collection of supported special symbols and their
	 * corresponding literal values
	 */
	@NonNull
	MultiMap<String, T> getSpecials();

	/**
	 * Provides a uniform value for "dot" transitions, which accept any value,
	 * corresponding to "." in traditional regular expression languages
	 * @return a uniform value for "dot" transitions, which accept any value
	 */
	@NonNull
	T getDot();

	/**
	 * Determines the length of the provided element, where applicable. In some
	 * implementations, this may simply be 1 in all cases.
	 * @param t the data element whose length is to be determined
	 * @return the length of the provided element
	 */
	int lengthOf(@NonNull T t);

	@NonNull
	List<String> split(String substring);

	default Expression parseExpression(String exp) {
		return parseExpression(exp, ParseDirection.FORWARD);
	}
}
