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

/**
 * Class {@code TerminalNode}
 *
 * 10/12/17
 */
@Value
public class TerminalNode implements Expression {

	String id;
	String terminal;
	String quantifier;
	boolean negative;

	public TerminalNode(
			String id,
			String terminal,
			String quantifier,
			boolean negative
	) {
		this.id = id;
		this.terminal = terminal;
		this.quantifier = quantifier;
		this.negative = negative;
	}

	public TerminalNode(String terminal, String quantifier, boolean negative) {
		this.terminal = terminal;
		this.quantifier = quantifier;
		this.negative = negative;

		id = Expression.randomId(terminal, quantifier, negative);
	}

	public TerminalNode(String terminal, String quantifier) {
		this(terminal, quantifier, false);
	}

	public TerminalNode(String terminal) {
		this(terminal, "", false);
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public boolean isCapturing() {
		return false;
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@NonNull
	@Override
	public String getTerminal() {
		return terminal;
	}

	@NonNull
	@Override
	public List<Expression> getChildren() {
		return Collections.emptyList();
	}

	@NonNull
	@Override
	public String getQuantifier() {
		return quantifier;
	}

	@NonNull
	@Override
	public Expression reverse() {
		return this;
	}

	@NonNull
	@Override
	public Expression withId(String id) {
		return new TerminalNode(id, terminal, quantifier, negative);
	}

	@NonNull
	@Override
	public Expression withNegative(boolean isNegative) {
		return new TerminalNode(id, terminal, quantifier, isNegative);
	}

	@NonNull
	@Override
	public Expression withQuantifier(String newQuantifier) {
		return new TerminalNode(id, terminal, newQuantifier, negative);
	}

	@NonNull
	@Override
	public Expression withTerminal(String newTerminal) {
		return new TerminalNode(id, newTerminal, quantifier, negative);
	}

	@Override
	public String toString() {
		return (negative ? "!" : "") + terminal + quantifier;
	}
}
