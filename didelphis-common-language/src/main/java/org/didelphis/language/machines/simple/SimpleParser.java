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

package org.didelphis.language.machines.simple;

import lombok.NonNull;
import org.didelphis.language.machines.Expression;
import org.didelphis.language.machines.interfaces.MachineParser;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Split;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Enum {@code SimpleParser}
 *
 * A basic {@link String}-based parser instance 
 * 
 * @author Samantha Fiona McCabe
 * @date 2017-03-03
 * @since 0.1.0
 */
public enum SimpleParser implements MachineParser<String> {
	INSTANCE;

	@Override
	public String transform(String expression) {
		return expression;
	}

	@NonNull
	@Override
	public List<Expression> parseExpression(@NonNull String expression) {
		List<Expression> list = new ArrayList<>();
		if (!expression.isEmpty()) {
			Expression buffer = new Expression();
			for (String symbol : Split.splitToList(expression, null)) {
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
		}
		return list;
	}

	@Override
	public String getWordStart() {
		return "^";
	}

	@Override
	public String getWordEnd() {
		return "$";
	}
	
	@Override
	public String epsilon() {
		return "";
	}

	@NonNull
	@Override
	public MultiMap<String, String> getSpecials() {
		return GeneralMultiMap.emptyMultiMap();
	}

	@Override
	public @NotNull String getDot() {
		return ".";
	}

	@Override
	public int lengthOf(@NonNull String s) {
		return s.length();
	}

	@NonNull
	private static Expression updateExpressionBuffer(
			@NonNull Collection<Expression> list, @NonNull Expression buffer
	) {
		// Add the contents of buffer if not empty
		if (buffer.isEmpty()) {
			return buffer;
		} else {
			list.add(buffer);
			return new Expression();
		}
	}
}
