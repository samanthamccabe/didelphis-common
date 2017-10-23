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

import java.util.ArrayList;
import java.util.List;

/**
 * Class {@code ExpressionNode}
 *
 * @author Samantha Fiona McCabe
 * @date 10/13/17
 */
@ToString
public class ExpressionNode implements Expression {

	private final List<Expression> children;
	private final Quantifier quantifier;
	
	public ExpressionNode(Quantifier quantifier) {
		children = new ArrayList<>();
		this.quantifier = quantifier;
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public String getTerminal() {
		return "";
	}

	@Override
	public List<Expression> getChildren() {
		return children;
	}

	@Override
	public Quantifier getQuantifier() {
		return quantifier;
	}
}
