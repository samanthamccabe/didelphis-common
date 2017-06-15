/*=============================================================================
 = Copyright (c) 2017. Samantha Fiona McCabe (Didelphis)
 =
 = Licensed under the Apache License, Version 2.0 (the "License");
 = you may not use this file except in compliance with the License.
 = You may obtain a copy of the License at
 =     http://www.apache.org/licenses/LICENSE-2.0
 = Unless required by applicable law or agreed to in writing, software
 = distributed under the License is distributed on an "AS IS" BASIS,
 = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 = See the License for the specific language governing permissions and
 = limitations under the License.
 =============================================================================*/

package org.didelphis.language.machines.interfaces;

import org.didelphis.language.machines.Expression;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by samantha on 2/23/17.
 */
public interface MachineParser<T> {

	/**
	 * Transform an expression string into a corresponding state machine
	 * @param expression
	 * @return
	 */
	T transform(String expression);

	/**
	 * Parse an expression to a list of sub-expressions
	 * @param expression
	 * @return
	 */
	List<Expression> parseExpression(String expression);

	/**
	 * Provides a uniform value for epsilon transitions 
	 * @return a uniform value for epsilon transitions 
	 */
	T epsilon();

	/**
	 * Provides a {@code collection} of supported special symbols and their
	 * corresponding literal values
	 * @return a {@code collection} of supported special symbols and their
	 * corresponding literal values
	 */
	Map<String, Collection<T>> getSpecials();

	/**
	 * Provides a uniform value for "dot" transitions, which accept any value,
	 * corresponding to "." in traditional regular expression languages
	 * @return a uniform value for "dot" transitions, which accept any value
	 */
	T getDot();

	/**
	 * Determines the length of the provided element, where applicable. In some
	 * implementations, this may simply be 1 in all cases.
	 * @param t the data element whose length is to be determined
	 * @return the length of the provided element
	 */
	int lengthOf(T t);
	
}
