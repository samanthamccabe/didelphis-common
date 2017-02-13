/*******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.didelphis.common.language.machines;

import org.didelphis.common.language.enums.FormatterMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Samantha Fiona Morrigan McCabe
 * Date: 9/1/13
 * Time: 9:34 PM
 * Expression creates and stores a compact representation of a regular expression string
 * and is used as a preprocessor for the creation of state-machines for regex matching
 */
public class Expression {
	private String  expression    = "";
	private String  metacharacter = "";
	private boolean negative;


	public static List<Expression> getExpressions(String string, Collection<String> keys, FormatterMode formatterMode) {

		List<String> strings = formatterMode.split(string, keys);

		List<Expression> list = new ArrayList<>();
		if (!strings.isEmpty()) {

			Expression buffer = new Expression();
			for (String symbol : strings) {
				if (symbol.equals("*") || symbol.equals("?") || symbol.equals("+")) {
					buffer.setMetacharacter(symbol);
					buffer = updateBuffer(list, buffer);
				} else if (symbol.equals("!")) {
					// first in an expression
					buffer = updateBuffer(list, buffer);
					buffer.setNegative(true);
				} else {
					buffer = updateBuffer(list, buffer);
					buffer.setExpression(symbol);
				}
			}
			if (!buffer.getExpression().isEmpty()) {
				list.add(buffer);
			}
		}
		return list;
	}

	private static Expression updateBuffer(Collection<Expression> list, Expression buffer) {
		// Add the contents of buffer if not empty
		if (!buffer.isEmpty()) {
			list.add(buffer);
			return new Expression();
		} else {
			return buffer;
		}
	}



	@Override
	public String toString() {
		return (negative ? "!" : "") + expression + metacharacter;
	}

	public String getExpression() {
		return expression;
	}
	
	public boolean isEmpty() {
		return expression.isEmpty();
	}

	public void setExpression(String expParam) {
		expression = expParam;
	}

	public String getMetacharacter() {
		return metacharacter;
	}

	public void setMetacharacter(String metaParam) {
		metacharacter = metaParam;
	}

	public boolean isNegative() {
		return negative;
	}

	public void setNegative(boolean negParam) {
		negative = negParam;
	}
}
