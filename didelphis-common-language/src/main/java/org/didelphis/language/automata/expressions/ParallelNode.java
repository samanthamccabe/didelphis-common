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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class {@code ParallelNode}
 *
 */
@Value
public class ParallelNode implements Expression {

	String id;
	List<Expression> children;
	String quantifier;
	boolean negative;

	public ParallelNode(List<Expression> children) {
		this(children, "", false);
	}
	
	public ParallelNode(
			String id,
			List<Expression> children,
			String quantifier,
			boolean negative
	) {
		this.id = id;
		this.children = children;
		this.quantifier = quantifier;
		this.negative = negative;
	}

	public ParallelNode(
			List<Expression> children, String quantifier, boolean negative
	) {
		this.children = children;
		this.quantifier = quantifier;
		this.negative = negative;

		id = Expression.randomId(children, quantifier, negative);
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public boolean isParallel() {
		return true;
	}

	@Override
	public boolean isCapturing() {
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
		return new ParallelNode(id, revChildren, quantifier, negative);
	}

	@NonNull
	@Override
	public Expression withId(String id) {
		return new ParallelNode(id, children, quantifier, negative);
	}

	@NonNull
	@Override
	public Expression withNegative(boolean isNegative) {
		return new ParallelNode(id, children, quantifier, isNegative);
	}

	@NonNull
	@Override
	public Expression withQuantifier(String newQuantifier) {
		return new ParallelNode(id, children, newQuantifier, negative);
	}

	@Override
	public String toString() {
		return (negative ? "!" : "") + children.stream()
				.map(Expression::toString)
				.collect(Collectors.joining(" ", "{", "}")) + quantifier;
	}
}
