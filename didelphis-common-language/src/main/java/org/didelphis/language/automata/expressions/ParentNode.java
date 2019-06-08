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
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class {@code ExpressionNode}
 *
 */
@Value
public class ParentNode implements Expression {

	String id;
	List<Expression> children;
	String quantifier;
	boolean negative;
	boolean capturing;

	public ParentNode(List<Expression> children) {
		this(children, "", false);
	}

	public ParentNode(List<Expression> children, String quantifier) {
		this(children, quantifier, false);
	}

	public ParentNode(
			List<Expression> children, String quantifier, boolean negative
	) {
		this(children, quantifier, negative, false);
	}

	public ParentNode(
			String id,
			List<Expression> children,
			String quantifier,
			boolean negative,
			boolean capturing
	) {
		this.id = id;
		this.children = children;
		this.quantifier = quantifier;
		this.negative = negative;
		this.capturing = capturing;
	}

	@SuppressWarnings ("BooleanParameter")
	public ParentNode(
			List<Expression> children,
			String quantifier,
			boolean negative,
			boolean capturing
	) {
		this.children = children;
		this.quantifier = quantifier;
		this.negative = negative;
		this.capturing = capturing;

		id = Expression.randomId(children, quantifier, negative, capturing);
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public boolean isTerminal() {
		return false;
	}

	@NonNull
	@Override
	public String getTerminal() {
		return "";
	}

	@NonNull
	@Override
	public Expression reverse() {
		List<Expression> revChildren = children.stream()
				.map(Expression::reverse)
				.collect(Collectors.toList());
		Collections.reverse(revChildren);
		return new ParentNode(id, revChildren, quantifier, negative, capturing);
	}

	@NonNull
	@Override
	public Expression withId(String id) {
		return new ParentNode(id, children, quantifier, negative, capturing);
	}

	@NonNull
	@Override
	public Expression withNegative(boolean isNegative) {
		return new ParentNode(id, children, quantifier, isNegative, capturing);
	}

	@NonNull
	@Override
	public Expression withQuantifier(String newQuantifier) {
		return new ParentNode(id, children, newQuantifier, negative, capturing);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (Expression child : children) {
			String toString = child.toString();
			sb.append(toString);
		}
		sb.append(')');
		return (negative ? "!" : "") + sb + quantifier;
	}
}
