/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.language.automata.parsing;

import lombok.NonNull;
import lombok.ToString;

import org.didelphis.language.automata.matching.Match;
import org.didelphis.structures.graph.Arc;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Splitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class {@code StringParser}
 *
 * A {@link String}-only companion to {@link SequenceParser} which uses the same
 * linguistics-oriented regular expression syntax.
 *
 * @since 0.3.0
 */
@ToString
public class StringParser extends AbstractDidelphisParser<String> {

	private static final Arc<String> DOT_ARC        = new DotArc();
	private static final Arc<String> EPSILON_ARC    = new EpsilonArc();
	private static final Arc<String> WORD_START_ARC = new WordStartArc();
	private static final Arc<String> WORD_END_ARC   = new WordEndArc();

	private static final Map<String, String> DELIMITERS = new LinkedHashMap<>();
	static {
		DELIMITERS.put("(?:", ")");
		DELIMITERS.put("(", ")");
		DELIMITERS.put("{", "}");
	}

	private final MultiMap<String, String> specials;

	public StringParser() {
		this(new GeneralMultiMap<>());
	}

	public StringParser(@NonNull MultiMap<String, String> specials) {
		this.specials = specials;
	}

	@NonNull
	@Override
	public Map<String, String> supportedDelimiters() {
		return Collections.unmodifiableMap(DELIMITERS);
	}

	@NonNull
	@Override
	public String transform(String expression) {
		return expression;
	}

	@NonNull
	@Override
	public Arc<String> getArc(String arc) {
		if (arc.equals("#[")) return WORD_START_ARC;
		if (arc.equals("]#")) return WORD_END_ARC;
		if (arc.equals(".")) return DOT_ARC;
		if (specials.containsKey(arc)) {
			return new SetArc(specials.get(arc));
		}
		return new LiteralArc(arc);
	}

	@NonNull
	@Override
	public Arc<String> epsilon() {
		return EPSILON_ARC;
	}

	@NonNull
	@Override
	public MultiMap<String, String> getSpecialsMap() {
		return specials;
	}

	@NonNull
	@Override
	public Arc<String> getDot() {
		return DOT_ARC;
	}

	@Override
	public int lengthOf(@NonNull String segments) {
		return segments.length();
	}

	@Override
	@NonNull
	protected List<String> split(String string) {
		return Splitter.toList(string, DELIMITERS, specials.keys());
	}

	@NonNull
	@Override
	public String subSequence(@NonNull String sequence, int start, int end) {
		return sequence.substring(start, end);
	}

	@NonNull
	@Override
	public String concatenate(
			@NonNull String sequence1,
			@NonNull String sequence2
	) {
		return sequence1 + sequence2;
	}

	@NonNull
	@Override
	public String replaceGroups(
			@NonNull String input,
			@NonNull Match<String> match
	) {
		StringBuilder sb = new StringBuilder();
		StringBuilder number = new StringBuilder();

		boolean inGroup = false;

		int cursor = 0;
		int i = 0;
		while (i < input.length()) {
			char c = input.charAt(i);
			// ASCII digits 0-9
			if (0x30 <= c && c < 0x3A && inGroup) {
				number.append(c);
				i++;
				cursor = i;
			} else {
				// parse and append group data
				if (number.length() > 0) {
					int groupNumber = Integer.parseInt(number.toString());
					String group = match.group(groupNumber);
					if (group != null) {
						sb.append(group);
					}
					// clear buffer
					number = new StringBuilder();
				}

				if (c == '$') {
					inGroup = true;
					if (cursor != i) {
						sb.append(input, cursor, i);
					}
					i++;
					cursor = i;
				} else {
					inGroup = false;
					i++;
				}
			}
		}

		sb.append(input.substring(cursor));

		// parse and append group data
		if (number.length() > 0) {
			int groupNumber = Integer.parseInt(number.toString());
			String group = match.group(groupNumber);
			if (group != null) {
				sb.append(group);
			}
		}
		return sb.toString();
	}

	private static final class LiteralArc implements Arc<String> {

		private final String literal;

		private LiteralArc(String literal) {
			this.literal = literal;
		}

		@Override
		public int match(String sequence, int index) {
			if (sequence.startsWith(literal, index)) {
				return index + literal.length();
			}
			return -1;
		}

		@Override
		public String toString() {
			return literal;
		}
	}

	private static final class SetArc implements Arc<String> {

		private final List<String> strings;

		private SetArc(Collection<String> strings) {
			this.strings = new ArrayList<>(strings);
		}

		@Override
		public int match(String sequence, int index) {
			for (String string : strings) {
				if (sequence.startsWith(string, index)) {
					return index + string.length();
				}
			}
			return -1;
		}

		@Override
		public String toString() {
			return strings.toString();
		}
	}

	private static class DotArc implements Arc<String> {
		@Override
		public int match(String sequence, int index) {
			int length = sequence.length();
			return length > 0 && index < length ? index + 1 : -1;
		}

		@Override
		public String toString() {
			return ".";
		}
	}

	private static class EpsilonArc implements Arc<String> {
		@Override
		public int match(String sequence, int index) {
			return index;
		}

		@Override
		public String toString() {
			return "";
		}
	}

	private static class WordStartArc implements Arc<String> {
		@Override
		public int match(String sequence, int index) {
			return index == 0 ? 0 : -1;
		}

		@Override
		public String toString() {
			return "^";
		}
	}

	private static class WordEndArc implements Arc<String> {
		@Override
		public int match(String sequence, int index) {
			int length = sequence.length();
			return index == length ? length : -1;
		}

		@Override
		public String toString() {
			return "$";
		}
	}
}
