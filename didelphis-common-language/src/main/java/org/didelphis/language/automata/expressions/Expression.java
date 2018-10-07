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

package org.didelphis.language.automata.expressions;

import lombok.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Interface {@code Expression}
 *
 * @author Samantha Fiona McCabe
 * @date 2013-09-01
 *
 * 	Expression creates and stores a compact representation of a regular
 * 	expression string and is used as a preprocessor for the creation of
 * 	state-automata for regex matching
 */
public interface Expression {

	boolean hasChildren();

	boolean isNegative();
	
	boolean isParallel();

	boolean isCapturing();
	
	boolean isTerminal();
	
	@NonNull String getId();
	
	@NonNull String getTerminal();

	@NonNull List<Expression> getChildren();

	@NonNull String getQuantifier();

	@NonNull Expression reverse();

	@NonNull Expression withId(String id);
	
	@NonNull Expression withNegative(boolean isNegative);
	
	@NonNull Expression withQuantifier(String newQuantifier);
	
	@NonNull default Expression withTerminal(String newTerminal) {
		throw new UnsupportedOperationException(
				"Cannot add terminal " + newTerminal +
						" to a non-terminal node");
	}

	@NonNull
	static String randomId(Object... objects) {
		int hash = Objects.hash(objects);
		long rand = Double.doubleToLongBits(Math.random());
		return Long.toHexString(hash ^ rand);
	}

	/**
	 * Traverses the provided expression and rewrites the node ids such that:
	 * <ul>
	 * <li>The root node has ID {@code 0}</li>
	 * <li>The ID of any child is it's parent, followed by {@code "."} and its
	 * index, from zero (i.e. the first child is {@code 0}, the second is {@code
	 * 1}, etc.</li>
	 * <li>Any child of a "parallel" node is prefixed {@code P}</li>
	 * </ul>
	 *
	 * @param expression any {@code Expression} whose ID and whose children's
	 * 		IDs are to be rewritten
	 * @param root the ID that will be assigned to {@param expression}
	 *
	 * @return the new rewritten expression hierarchy
	 */
	@NonNull
	static Expression rewriteIds(
			@NonNull Expression expression,
			@NonNull String root
	) {
		Expression expression1 = expression.withId(root);
		if (expression1.hasChildren()) {
			List<Expression> children = expression1.getChildren();
			for (int i = 0; i < children.size(); i++) {
				Expression expression2 = children.get(i);
				if (expression1.isParallel()) {
					children.set(i, rewriteIds(expression2, root + ".P" + i));
				} else {
					children.set(i, rewriteIds(expression2, root + '.' + i));
				}
			}
		}
		return expression1;
	}
}
