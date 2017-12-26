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

import java.util.Collections;
import java.util.List;

/**
 * Class {@code TerminalNode}
 *
 * @author Samantha Fiona McCabe
 * @date 10/12/17
 */
@Value
public class TerminalNode implements Expression {

	String terminal;
	String quantifier;
	boolean negative;
	
	public TerminalNode(String terminal, String quantifier, boolean negative) {
		this.terminal = terminal;
		this.quantifier = quantifier;
		this.negative = negative;
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
	public Expression withNegative(boolean isNegative) {
		return new TerminalNode(terminal, quantifier, isNegative);
	}

	@NonNull
	@Override
	public Expression withQuantifier(String newQuantifier) {
		return new TerminalNode(terminal, newQuantifier, negative);
	}

	@Override
	public String toString() {
		return (negative ? "!" : "") + terminal + quantifier;
	}
}
