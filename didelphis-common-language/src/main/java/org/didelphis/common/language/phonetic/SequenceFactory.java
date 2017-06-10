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

package org.didelphis.common.language.phonetic;

import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.segments.Segment;
import org.didelphis.common.language.phonetic.segments.StandardSegment;
import org.didelphis.common.language.phonetic.sequences.BasicSequence;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Author: Samantha Fiona Morrigan McCabe
 * Created: 11/23/2014
 */
public class SequenceFactory<N> {

	private static final Logger LOG = LoggerFactory.getLogger(SequenceFactory.class);
	
	private static final Pattern BACKREFERENCE_PATTERN = Pattern.compile("(\\$[^$]*\\d+)");

	private final FeatureMapping<N> featureMapping;
	private final VariableStore variableStore;
	private final FormatterMode formatterMode;
	private final Set<String>   reservedStrings;

	private final Segment<N> dotSegment;
	private final Segment<N> borderSegment;

	private final Sequence<N> dotSequence;
	private final Sequence<N> borderSequence;
	
	public SequenceFactory(FeatureMapping<N> mapping, FormatterMode mode) {
		this(mapping, new VariableStore(mode), new HashSet<>(), mode);
	}

	public SequenceFactory(FeatureMapping<N> mapping, VariableStore store, Set<String> reserved, FormatterMode mode) {
		featureMapping = mapping;
		variableStore   = store;
		reservedStrings = reserved;
		formatterMode   = mode;

		FeatureModel<N> model = featureMapping.getFeatureModel();
		FeatureArray<N> sparseArray = new SparseFeatureArray<>(model);

		//TODO: make these immutable
		dotSegment = new StandardSegment<>(".", sparseArray, model);
		borderSegment = new StandardSegment<>("#", sparseArray, model);

		dotSequence    = new BasicSequence<>(dotSegment);
		borderSequence = new BasicSequence<>(borderSegment);
	}

	public FormatterMode getFormatterMode() {
		return formatterMode;
	}

	public void reserve(String string) {
		reservedStrings.add(string);
	}

	public Segment<N> getDotSegment() {
		return dotSegment;
	}

	public Segment<N> getBorderSegment() {
		return borderSegment;
	}

	public Sequence<N> getDotSequence() {
		return dotSequence;
	}

	public Sequence<N> getBorderSequence() {
		return borderSequence;
	}

	public Segment<N> getSegment(String string) {
		if (!featureMapping.containsKey("#") && string.equals("#")) {
			return borderSegment;
		} else if (string.equals(".")) {
			return dotSegment;
		} else {
			return featureMapping.parseSegment(string);
		}
	}

	public Lexicon<N> getLexiconFromSingleColumn(Iterable<String> list) {
		Lexicon<N> lexicon = new Lexicon<>();
		for (String entry : list) {
			Sequence<N> sequence = getSequence(entry);
			lexicon.add(sequence);
		}
		return lexicon;
	}

	public Lexicon<N> getLexiconFromSingleColumn(String... list) {
		Lexicon<N> lexicon = new Lexicon<>();
		for (String entry : list) {
			Sequence<N> sequence = getSequence(entry);
			lexicon.add(sequence);
		}
		return lexicon;
	}

	public Lexicon<N> getLexicon(Iterable<List<String>> lists) {
		Lexicon<N> lexicon = new Lexicon<>();

		for (Iterable<String> row : lists) {
			List<Sequence<N>> lexRow = new ArrayList<>();
			for (String entry : row) {
				Sequence<N> sequence = getSequence(entry);
				lexRow.add(sequence);
			}
			lexicon.add(lexRow);
		}
		return lexicon;
	}

	public Sequence<N> getNewSequence() {
		return getSequence("");
	}

	public Sequence<N> getSequence(String word) {
		if (word.equals("#")) {
			return borderSequence;
		} else if (word.equals(".")) {
			return dotSequence;
		} else {
			Collection<String> keys = new ArrayList<>();
			keys.addAll(variableStore.getKeys());
			keys.addAll(reservedStrings);
			
			return SegmenterUtil.getSequence(word, featureMapping, keys, formatterMode);
		}
	}

	public boolean hasVariable(String label) {
		return variableStore.contains(label);
	}

	public List<Sequence<N>> getVariableValues(String label) {
		return variableStore.get(label).stream()
				.map(this::getSequence)
				.collect(Collectors.toList());
	}

	public List<String> getSegmentedString(String string) {
		return SegmenterUtil.getSegmentedString(string, getSpecialStrings(), formatterMode);
	}

	public FeatureMapping<N> getFeatureMapping() {
		return featureMapping;
	}

	public VariableStore getVariableStore() {
		return variableStore;
	}

	public String getBestMatch(String tail) {

		Iterable<String> keys = getSpecialStrings();
		String bestMatch = "";
		for (String key : keys) {
			if (tail.startsWith(key) && bestMatch.length() < key.length()) {
				bestMatch = key;
			}
		}
		Matcher backReferenceMatcher = BACKREFERENCE_PATTERN.matcher(tail);
		if (backReferenceMatcher.lookingAt()) {
			bestMatch = backReferenceMatcher.group();
		}
		return bestMatch;
	}

	@Override
	public String toString() {
		return "SequenceFactory{" +
		       ", featureMapping=" + featureMapping +
		       ", variableStore=" + variableStore +
		       ", formatterMode=" + formatterMode +
		       ", reservedStrings=" + reservedStrings +
		       '}';
	}

	public Collection<String> getSpecialStrings() {
		Collection<String> keys = new ArrayList<>();
		keys.addAll(variableStore.getKeys());
//		keys.addAll(featureMapping); // TODO: 
		keys.addAll(reservedStrings);
		return keys;
	}
}
