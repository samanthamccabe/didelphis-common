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
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class {@code ParallelNode}
 *
 * @author Samantha Fiona McCabe
 * @date 11/6/17
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
