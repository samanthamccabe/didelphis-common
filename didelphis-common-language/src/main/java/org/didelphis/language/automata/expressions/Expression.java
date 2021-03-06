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

package org.didelphis.language.automata.expressions;

import lombok.NonNull;

import java.util.List;

/**
 * Interface {@code Expression}
 * <p>
 * Expression creates and stores a compact representation of a regular
 * expression string and is used as a preprocessor for the creation of
 * state-automata for regex matching
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
		int hash = 1;
		for (Object element : objects) {
			hash = 31 * hash + (element == null ? 0 : element.hashCode());
		}
		long rand = System.nanoTime();
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
	 *      IDs are to be rewritten
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
