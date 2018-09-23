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

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Delegate;
import org.didelphis.language.automata.matching.Match;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class {@code Regex}
 * <p>
 * A {@link Automaton} wrapper for the standard {@link Pattern} class.
 *
 * @author Samantha Fiona McCabe
 */
public class Regex implements Automaton<String> {

	private final Pattern pattern;

	public Regex(@Language ("RegExp") @NonNull String pattern) {
		this(pattern, 0);
	}

	public Regex(@Language ("RegExp") @NonNull String pattern, int flags) {
		this.pattern = Pattern.compile(pattern, flags);
	}

	@NonNull
	@Override
	public Match<String> match(@NonNull String input, int start) {
		Matcher matcher = pattern.matcher(input);
		if (matcher.find(start) && matcher.start() == start) {
			return new PatternMatch(matcher.toMatchResult());
		}
		return new EmptyMatch();
	}

	@NonNull
	@Override
	public List<String> split(@NonNull String input, int limit) {
		return Arrays.asList(pattern.split(input, limit));
	}

	@NonNull
	@Override
	public String replace(
			@NonNull String input, @NonNull String replacement
	) {
		return pattern.matcher(input).replaceAll(replacement);
	}

	@Override
	public String toString() {
		return pattern.pattern();
	}

	@Value
	private static final class PatternMatch implements Match<String> {
		@Delegate MatchResult matchResult;
	}

	private static final class EmptyMatch implements Match<String> {

		@Override
		public int start() {
			return -1;
		}

		@Override
		public int start(int group) {
			return -1;
		}

		@Override
		public int end() {
			return -1;
		}

		@Override
		public int end(int group) {
			return -1;
		}

		@Override
		public @Nullable String group(int group) {
			return null;
		}

		@Override
		public int groupCount() {
			return 0;
		}
	}
}
