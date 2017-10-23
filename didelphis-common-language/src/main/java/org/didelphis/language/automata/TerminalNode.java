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

package org.didelphis.language.automata;

import lombok.ToString;
import org.didelphis.language.matching.Quantifier;

import java.util.Collections;
import java.util.List;

/**
 * Class {@code TerminalNode}
 *
 * @author Samantha Fiona McCabe
 * @date 10/12/17
 */
@ToString
public class TerminalNode implements Expression {

	private final String terminal;

	public TerminalNode(String terminal) {
		this.terminal = terminal;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public String getTerminal() {
		return terminal;
	}

	@Override
	public List<Expression> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public Quantifier getQuantifier() {
		return null;
	}
}
