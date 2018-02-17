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
import lombok.Value;
import lombok.experimental.Delegate;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class {@code JavaPatternAutomaton}
 *
 * A {@link Automaton} wrapper for the normal {@link Pattern} class.
 * 
 * @author Samantha Fiona McCabe
 * @date 10/17/17
 */
@ToString
public class JavaPatternAutomaton implements Automaton<String> {

	private final Pattern pattern;

	public JavaPatternAutomaton(String pattern) {
		this(pattern, 0);
	}
	
	public JavaPatternAutomaton(String pattern, int flags) {
		this.pattern = Pattern.compile(pattern, flags);
	}
	
	@Override
	public Match<String> match(String input, int start) {
		Matcher matcher = pattern.matcher(input);
		if (matcher.find(start)) {
			return new PatternMatch(matcher.toMatchResult());
		}
		return null;
	}

	@Value
	private static final class PatternMatch implements Match<String> {
		@Delegate
		MatchResult matchResult;
	}
}
