/*=============================================================================
 = Copyright (c) 2017. Samantha Fiona McCabe (Didelphis)
 =
 = Licensed under the Apache License, Version 2.0 (the "License");
 = you may not use this file except in compliance with the License.
 = You may obtain a copy of the License at
 =     http://www.apache.org/licenses/LICENSE-2.0
 = Unless required by applicable law or agreed to in writing, software
 = distributed under the License is distributed on an "AS IS" BASIS,
 = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 = See the License for the specific language governing permissions and
 = limitations under the License.
 =============================================================================*/

package org.didelphis.common.language.machines.simple;

import org.didelphis.common.language.machines.Expression;
import org.didelphis.common.language.machines.interfaces.MachineParser;
import org.didelphis.common.utilities.Split;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samantha on 3/3/17.
 */
public final class StringParser implements MachineParser<String> {
	
	private static final Map<String, Collection<String>> SPECIALS = specials();

	private static Map<String, Collection<String>> specials() {
		return new HashMap<>(); // TODO:
	}

	private static final StringParser INSTANCE = new StringParser();
	
	public static StringParser getInstance() {
		return INSTANCE;
	}

	private StringParser() {}
	
	@Override
	public String transform(String expression) {
		return null;
	}

	@Override
	public List<Expression> parseExpression(String expression) {
		List<Expression> list = new ArrayList<>();
		if (!expression.isEmpty()) {
			List<String> symbols = Split.splitToList(expression, SPECIALS.keySet());
			
			//TODO: repeated code below ?? =>>
			Expression buffer = new Expression();
			for (String symbol : symbols) {
				if ("*?+".contains(symbol)) {
					buffer.setMetacharacter(symbol);
					buffer = updateExpressionBuffer(list, buffer);
				} else if (symbol.equals("!")) {
					// first in an expression
					buffer = updateExpressionBuffer(list, buffer);
					buffer.setNegative(true);
				} else {
					buffer = updateExpressionBuffer(list, buffer);
					buffer.setExpression(symbol);
				}
			}
			if (!buffer.getExpression().isEmpty()) {
				list.add(buffer);
			}
			//TODO: <<= end repeated code ??
		}
		return list;
	}

	@Override
	public String epsilon() {
		return null;
	}

	@Override
	public Map<String, Collection<String>> getSpecials() {
		return null; // TODO: --------------------------------------------------
	}

	@Override
	public String getDot() {
		return ".";
	}

	@Override
	public int lengthOf(String s) {
		return s.length();
	}

	private static Expression updateExpressionBuffer(Collection<Expression> list, Expression buffer) {
		// Add the contents of buffer if not empty
		if (buffer.isEmpty()) {
			return buffer;
		} else {
			list.add(buffer);
			return new Expression();
		}
	}
}
