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

import org.didelphis.language.matching.Quantifier;

import java.util.List;

/**
 * Interface {@code Expression}
 * 
 * @author Samantha Fiona McCabe
 * @date 2013-09-01

 * Expression creates and stores a compact representation of a regular 
 * expression string and is used as a preprocessor for the creation of 
 * state-automata for regex matching
 */
public interface Expression {

	boolean hasChildren();
	
	String getTerminal();
	
	List<Expression> getChildren();
	
	Quantifier getQuantifier();

	enum DefaultQuantifier implements Quantifier {
		NONE("", false),
		STAR("*", true),
		PLUS("+", true),
		HOOK("?", true),
		STAR_RELUCTANT("*?", false),
		PLUS_RELUCTANT("+?", false);

		private final String symbol;
		private final boolean isGreedy;

		DefaultQuantifier(String symbol, boolean isGreedy) {
			this.symbol = symbol;
			this.isGreedy = isGreedy;
		}
		
		@Override
		public String getSymbol() {
			return symbol;
		}
		
		@Override
		public boolean isGreedy() {
			return isGreedy;
		}
		
		@Override
		public String toString() {
			return symbol;
		}
	}
}
	
