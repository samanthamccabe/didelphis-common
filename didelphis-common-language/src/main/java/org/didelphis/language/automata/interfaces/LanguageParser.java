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

package org.didelphis.language.automata.interfaces;

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.structures.maps.interfaces.MultiMap;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

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

	Map<String, String> supportedDelimiters();

	Set<String> supportedQuantifiers();
	
	T getWordStart();

	T getWordEnd();

	/**
	 * Transform an expression string into a corresponding terminal symbol, the
	 * same as is consumed from an input while searching for a match.
	 * @param expression
	 * @return
	 */
	@Nullable
	T transform(String expression);

	/**
	 * Parse an expression string to a list of sub-expressions
	 * @param rawExpression
	 * @return
	 */
	@NonNull
	Expression parseExpression(String rawExpression);

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
	
}
