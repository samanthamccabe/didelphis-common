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

package org.didelphis.language.machines;

import lombok.Data;
import lombok.NonNull;

/**
 * 
 * @author Samantha Fiona McCabe
 * @date 2013-09-01

 * Expression creates and stores a compact representation of a regular 
 * expression string and is used as a preprocessor for the creation of 
 * state-machines for regex matching
 */
@Data
public class Expression {
	private String  expression;
	private String  metacharacter;
	private boolean negative;
	
	public Expression() {
		this("","",false);
	}
	
	public Expression(String expression) {
		this(expression, "", false);
	}
	
	public Expression(String expression, String metacharacter, boolean negative) {
		this.expression = expression;
		this.metacharacter = metacharacter;
		this.negative = negative;
	}


	@NonNull
	@Override
	public String toString() {
		return (negative ? "!" : "") + expression + metacharacter;
	}
	
	public boolean isEmpty() {
		return expression.isEmpty();
	}

	public boolean isNegative() {
		return negative;
	}

}
