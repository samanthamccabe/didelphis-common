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
import org.didelphis.language.phonetic.sequences.BasicSequence;
import org.didelphis.language.phonetic.sequences.ImmutableSequence;
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
 * @param <T> the feature type used by language objects supported by a given
 * instance of this parser.
 *
 * @since 0.1.0
 */
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SequenceParser<T> extends AbstractDidelphisParser<Sequence<T>> {

	private static final Pattern DIGIT = Pattern.compile("\\d+");

	Arc<Sequence<T>> dotArc       = new DotArc<>();
	Arc<Sequence<T>> epsilonArc   = new EpsilonArc<>();
	Arc<Sequence<T>> wordStartArc = new WordStartArc<>();
	Arc<Sequence<T>> wordEndArc   = new WordEndArc<>();

	private static final Map<String, String> DELIMITERS = new LinkedHashMap<>();
	static {
		DELIMITERS.put("(?:", ")");
		DELIMITERS.put("(", ")");
		DELIMITERS.put("{", "}");
		DELIMITERS.put("[", "]");
	}

	SequenceFactory<T> factory;
	MultiMap<String, Sequence<T>> specials;

	Sequence<T> wordStart;
	Sequence<T> wordEnd;

	public SequenceParser(@NonNull SequenceFactory<T> factory) {
		this(factory, new GeneralMultiMap<>());
	}

	public SequenceParser(
			@NonNull SequenceFactory<T> factory,
			@NonNull MultiMap<String, Sequence<T>> specials
	) {
		this.factory = factory;
		this.specials = specials;

		// Generate epsilon / lambda symbol
		FeatureModel<T> model = factory.getFeatureMapping().getFeatureModel();
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
	public Sequence<T> transform(String expression) {
		return factory.toSequence(expression);
	}

	@NonNull
	@Override
	public Arc<Sequence<T>> getArc(String arc) {
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
	public Arc<Sequence<T>> epsilon() {
		return epsilonArc;
	}

	@NonNull
	@Override
	public MultiMap<String, Sequence<T>> getSpecialsMap() {
		return specials;
	}

	@NonNull
	@Override
	public Arc<Sequence<T>> getDot() {
		return dotArc;
	}

	@Override
	public int lengthOf(@NonNull Sequence<T> segments) {
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
	public Sequence<T> subSequence(@NonNull Sequence<T> sequence, int start, int end) {
		return sequence.subsequence(start, end);
	}

	@NonNull
	@Override
	public Sequence<T> concatenate(
			@NonNull Sequence<T> sequence1,
			@NonNull Sequence<T> sequence2
	) {
		sequence1.add(sequence2);
		return sequence1;
	}

	@NonNull
	@Override
	public Sequence<T> replaceGroups(
			@NonNull Sequence<T> input,
			@NonNull Match<Sequence<T>> match
	) {

		FeatureModel<T> featureModel = factory.getFeatureMapping().getFeatureModel();
		Sequence<T> sequence = new BasicSequence<>(featureModel);
		StringBuilder number = new StringBuilder();

		int i = 0;
		int cursor = 0;
		boolean inGroup = false;
		while (i < input.size()) {
			Segment<T> segment = input.get(i);
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
					Sequence<T> group = match.group(groupNumber);
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
			Sequence<T> group = match.group(groupNumber);
			if (group != null) {
				sequence.add(group);
			}
		}

		return sequence;
	}

	@NonNull
	private static <T> Sequence<T> immutable(
			@NonNull String symbol,
			@NonNull FeatureModel<T> model
	) {
		// Undefined segments can only match when the symbol matches
		return new ImmutableSequence<>(new UndefinedSegment<>(symbol, model));
	}

	private static final class LiteralArc<T> implements Arc<Sequence<T>> {

		private final Sequence<T> literal;

		private LiteralArc(Sequence<T> literal) {
			this.literal = literal;
		}

		@Override
		public int match(Sequence<T> sequence, int index) {
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

	private static final class SetArc<T> implements Arc<Sequence<T>> {

		private final List<Sequence<T>> strings;

		private SetArc(Collection<Sequence<T>> strings) {
			this.strings = new ArrayList<>(strings);
		}

		@Override
		public int match(Sequence<T> sequence, int index) {
			for (Sequence<T> string : strings) {
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

	private static class EpsilonArc<T> implements Arc<Sequence<T>> {
		@Override
		public int match(Sequence<T> sequence, int index) {
			return index;
		}

		@Override
		public String toString() {
			return "";
		}
	}

	private static class WordStartArc<T> implements Arc<Sequence<T>> {
		@Override
		public int match(Sequence<T> sequence, int index) {
			return index == 0 ? 0 : -1;
		}

		@Override
		public String toString() {
			return "^";
		}
	}

	private static class DotArc<T> implements Arc<Sequence<T>> {
		@Override
		public int match(Sequence<T> sequence, int index) {
			int length = sequence.size();
			return (length > 0 && index < length) ? index + 1 : -1;
		}

		@Override
		public String toString() {
			return ".";
		}
	}

	private static class WordEndArc<T> implements Arc<Sequence<T>> {
		@Override
		public int match(Sequence<T> sequence, int index) {
			int length = sequence.size();
			return index == length ? length : -1;
		}

		@Override
		public String toString() {
			return "$";
		}
	}
}
