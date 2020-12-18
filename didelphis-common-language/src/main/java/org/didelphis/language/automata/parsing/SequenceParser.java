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

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.UndefinedSegment;
import org.didelphis.language.phonetic.sequences.ImmutableSequence;
import org.didelphis.language.phonetic.sequences.PhoneticSequence;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.graph.Arc;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Class {@code SequenceParser}
 *
 * @since 0.1.0
 */
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SequenceParser extends AbstractDidelphisParser<Sequence> {

	private static final Pattern DIGIT = Pattern.compile("\\d+");

	Arc<Sequence> dotArc       = new DotArc<>();
	Arc<Sequence> epsilonArc   = new EpsilonArc<>();
	Arc<Sequence> wordStartArc = new WordStartArc<>();
	Arc<Sequence> wordEndArc   = new WordEndArc<>();

	private static final Map<String, String> DELIMITERS = new LinkedHashMap<>();
	static {
		DELIMITERS.put("(?:", ")");
		DELIMITERS.put("(", ")");
		DELIMITERS.put("{", "}");
		DELIMITERS.put("[", "]");
	}

	SequenceFactory factory;
	MultiMap<String, Sequence> specials;

	Sequence wordStart;
	Sequence wordEnd;

	public SequenceParser(@NonNull SequenceFactory factory) {
		this(factory, new GeneralMultiMap<>());
	}

	public SequenceParser(
			@NonNull SequenceFactory factory,
			@NonNull MultiMap<String, Sequence> specials
	) {
		this.factory = factory;
		this.specials = specials;

		// Generate epsilon / lambda symbol
		FeatureModel model = factory.getFeatureMapping().getFeatureModel();
		wordStart = immutable("#[", model);
		wordEnd   = immutable("]#", model);
	}

	@NonNull
	@Override
	public Map<String, String> supportedDelimiters() {
		return Collections.unmodifiableMap(DELIMITERS);
	}

	@NonNull
	@Override
	public Sequence transform(String expression) {
		return factory.toSequence(expression);
	}

	@NonNull
	@Override
	public Arc<Sequence> getArc(String arc) {
		if (arc.equals("#[")) return wordStartArc;
		if (arc.equals("]#")) return wordEndArc;
		if (arc.equals(".")) return dotArc;
		if (specials.containsKey(arc)) {
			return new SetArc<>(specials.get(arc));
		}
		return new LiteralArc<>(factory.toSequence(arc));
	}

	@NonNull
	@Override
	public Arc<Sequence> epsilon() {
		return epsilonArc;
	}

	@NonNull
	@Override
	public MultiMap<String, Sequence> getSpecialsMap() {
		return specials;
	}

	@NonNull
	@Override
	public Arc<Sequence> getDot() {
		return dotArc;
	}

	@Override
	public int lengthOf(@NonNull Sequence segments) {
		return segments.size();
	}

	@Override
	@NonNull
	protected List<String> split(String string) {
		FormatterMode formatter = factory.getFormatterMode();
		Set<String> set = new HashSet<>();
		set.addAll(factory.getSpecialStrings());
		set.addAll(specials.keys());
		return formatter.split(string, set, DELIMITERS);
	}

	@NonNull
	@Override
	public Sequence subSequence(@NonNull Sequence sequence, int start, int end) {
		return sequence.subsequence(start, end);
	}

	@NonNull
	@Override
	public Sequence concatenate(
			@NonNull Sequence sequence1,
			@NonNull Sequence sequence2
	) {
		sequence1.add(sequence2);
		return sequence1;
	}

	@NonNull
	@Override
	public Sequence replaceGroups(
			@NonNull Sequence input,
			@NonNull Match<Sequence> match
	) {

		FeatureModel featureModel = factory.getFeatureMapping().getFeatureModel();
		Sequence sequence = new PhoneticSequence(featureModel);
		StringBuilder number = new StringBuilder();

		int i = 0;
		int cursor = 0;
		boolean inGroup = false;
		while (i < input.size()) {
			Segment segment = input.get(i);
			String symbol = segment.getSymbol();
			// ASCII digits 0-9
			if (DIGIT.matcher(symbol).matches() && inGroup) {
				number.append(segment);
				i++;
				cursor = i;
			} else {
				// parse and append group data
				if (number.length() > 0) {
					int groupNumber = Integer.parseInt(number.toString());
					Sequence group = match.group(groupNumber);
					if (group != null) {
						sequence.add(group);
					}
					// clear buffer
					number = new StringBuilder();
				}

				if (segment.getSymbol().startsWith("$")) {
					inGroup = true;
					if (cursor != i) {
						sequence.add(input.subsequence(cursor, i));
					}
					i++;
					cursor = i;
				} else {
					inGroup = false;
					i++;
				}
			}
		}

		sequence.add(input.subsequence(cursor));

		// parse and append group data
		if (number.length() > 0) {
			int groupNumber = Integer.parseInt(number.toString());
			Sequence group = match.group(groupNumber);
			if (group != null) {
				sequence.add(group);
			}
		}

		return sequence;
	}

	@NonNull
	private static <T> Sequence immutable(
			@NonNull String symbol,
			@NonNull FeatureModel model
	) {
		// Undefined segments can only match when the symbol matches
		return new ImmutableSequence(new UndefinedSegment(symbol, model));
	}

	private static final class LiteralArc<T> implements Arc<Sequence> {

		private final Sequence literal;

		private LiteralArc(Sequence literal) {
			this.literal = literal;
		}

		@Override
		public int match(Sequence sequence, int index) {
			if (sequence.subsequence(index).startsWith(literal)) {
				return index + literal.size();
			}
			return -1;
		}

		@Override
		public String toString() {
			return literal.toString();
		}
	}

	private static final class SetArc<T> implements Arc<Sequence> {

		private final List<Sequence> strings;

		private SetArc(Collection<Sequence> strings) {
			this.strings = new ArrayList<>(strings);
		}

		@Override
		public int match(Sequence sequence, int index) {
			for (Sequence string : strings) {
				if (sequence.subsequence(index).startsWith(string)) {
					return index + string.size();
				}
			}
			return -1;
		}

		@Override
		public String toString() {
			return strings.toString();
		}
	}

	private static class EpsilonArc<T> implements Arc<Sequence> {
		@Override
		public int match(Sequence sequence, int index) {
			return index;
		}

		@Override
		public String toString() {
			return "";
		}
	}

	private static class WordStartArc<T> implements Arc<Sequence> {
		@Override
		public int match(Sequence sequence, int index) {
			return index == 0 ? 0 : -1;
		}

		@Override
		public String toString() {
			return "^";
		}
	}

	private static class DotArc<T> implements Arc<Sequence> {
		@Override
		public int match(Sequence sequence, int index) {
			int length = sequence.size();
			return (length > 0 && index < length) ? index + 1 : -1;
		}

		@Override
		public String toString() {
			return ".";
		}
	}

	private static class WordEndArc<T> implements Arc<Sequence> {
		@Override
		public int match(Sequence sequence, int index) {
			int length = sequence.size();
			return index == length ? length : -1;
		}

		@Override
		public String toString() {
			return "$";
		}
	}
}
