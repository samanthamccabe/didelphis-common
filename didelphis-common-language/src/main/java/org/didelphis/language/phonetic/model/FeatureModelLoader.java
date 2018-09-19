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

package org.didelphis.language.phonetic.model;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.didelphis.io.FileHandler;
import org.didelphis.io.NullFileHandler;
import org.didelphis.language.automata.Automaton;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.utils.Regex;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.structures.Suppliers;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Logger;
import org.didelphis.utilities.Templates;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.LITERAL;
import static org.didelphis.utilities.Splitter.lines;

/**
 * Class {@code FeatureModelLoader}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 */
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class FeatureModelLoader<T> {

	private static final Logger LOG = Logger.create(FeatureModelLoader.class);
	
	/**
	 * Enum {@code ParseZone}
	 */
	private enum ParseZone {
		FEATURES, ALIASES, CONSTRAINTS, SYMBOLS, MODIFIERS, NONE;

		boolean matches(@NonNull String string) {
			return name().equals(string.toUpperCase());
		}
	}

	/*-----------------------------------------------------------------------<*/
	static final Automaton<String> FEATURES_PATTERN = Regex.create("(\\w+)(\\s+(\\w*))?", CASE_INSENSITIVE);
	static final Automaton<String> TRANSFORM        = Regex.create("\\s*>\\s*");
	static final Automaton<String> BRACKETS         = Regex.create("[\\[\\]]");
	static final Automaton<String> EQUALS           = Regex.create("\\s*=\\s*");
	static final Automaton<String> IMPORT           = Regex.create("import\\s+['\"]([^'\"]+)['\"]", CASE_INSENSITIVE);
	static final Automaton<String> COMMENT_PATTERN  = Regex.create("\\s*%.*");
	static final Automaton<String> SYMBOL_PATTERN   = Regex.create("([^\\t]+)\\t(.*)");
	static final Automaton<String> TAB_PATTERN      = Regex.create("\\t");
	static final Automaton<String> DOTTED_CIRCLE    = Regex.create("◌", LITERAL);
	
	final MultiMap<ParseZone, String> zoneData;
	
	final List<String>         featureNames;
	final Map<String, Integer> featureIndices;
	final FeatureType<T>       featureType;
	final FileHandler          fileHandler;

	FeatureSpecification specification;
	FeatureModel<T>      featureModel;
	FeatureMapping<T>    featureMapping;
	/*>-----------------------------------------------------------------------*/

	public FeatureModelLoader(FeatureType<T> featureType) {
		this.featureType = featureType;
		fileHandler = NullFileHandler.INSTANCE;
		featureNames = new ArrayList<>();
		featureIndices = new HashMap<>();
		zoneData = new GeneralMultiMap<>();

		specification = new DefaultFeatureSpecification();
		featureModel = new GeneralFeatureModel<>(featureType,
				specification,
				Collections.emptyList(),
				Collections.emptyMap()
		);
		featureMapping = new GeneralFeatureMapping<>(featureModel,
				Collections.emptyMap(),
				Collections.emptyMap()
		);
	}

	public FeatureModelLoader(
			@NonNull FeatureType<T> featureType,
			@NonNull FileHandler fileHandler,
			@NonNull String path
	) {
		this(featureType, fileHandler, lines(fileHandler.read(path)));
	}

	public FeatureModelLoader(
			@NonNull FeatureType<T> featureType,
			@NonNull FileHandler fileHandler,
			@NonNull Iterable<String> lines
	) {
		this.featureType = featureType;
		this.fileHandler = fileHandler;

		featureNames = new ArrayList<>();
		featureIndices = new HashMap<>();

		Map<ParseZone, Collection<String>> map = new EnumMap<>(ParseZone.class);
		for (ParseZone zone : ParseZone.values()) {
			map.put(zone, new ArrayList<>());
		}
		zoneData = new GeneralMultiMap<>(map, Suppliers.ofList());

		parse(lines);
		populate();
	}

	@NonNull
	public Constraint<T> parseConstraint(@NonNull String entry) {
		List<String> split = TRANSFORM.split(entry, 2);
		String source = split.get(0);
		String target = split.get(1);
		FeatureArray<T> sMap = featureModel.parseFeatureString(source);
		FeatureArray<T> tMap = featureModel.parseFeatureString(target);
		return new Constraint<>(sMap, tMap);
	}

	@NonNull
	public FeatureSpecification getSpecification() {
		return specification;
	}

	@NonNull
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}

	@NonNull
	public FeatureMapping<T> getFeatureMapping() {
		return featureMapping;
	}

	private void populate() {
		specification = parseSpecification();

		Map<String, FeatureArray<T>> aliases = new HashMap<>();
		List<Constraint<T>> constraints = new ArrayList<>();

		featureModel = new GeneralFeatureModel<>(featureType,
				specification,
				constraints,
				aliases
		);

		for (String string : zoneData.get(ParseZone.ALIASES)) {
			List<String> split = EQUALS.split(string, 2);
			String alias = BRACKETS.replace(split.get(0),"");
			String value = split.get(1);
			aliases.put(alias, featureModel.parseFeatureString(value));
		}

		// populate constraints
		for (String s : zoneData.get(ParseZone.CONSTRAINTS)) {
			Constraint<T> constraint = parseConstraint(s);
			constraints.add(constraint);
		}

		featureMapping = new GeneralFeatureMapping<>(featureModel,
				populateSymbols(),
				populateModifiers()
		);
	}

	private FeatureSpecification parseSpecification() {
		// 1. populate the fields, builder raw representations
		// 2. instantiate the featureModel object w size
		// 3. builder correct internal objects with instance
		// 4. add objects back to instance

		populateFeatures();
		// Once the main feature definitions are parsed, it's possible to builder
		// the featureModel instance
		return new DefaultFeatureSpecification(featureNames, featureIndices);
	}

	/**
	 * Traverses the provided data and sorts commands into the appropriate bins
	 */
	private void parse(@NonNull Iterable<String> lines) {
		ParseZone currentZone = ParseZone.NONE;
		for (String string : lines) {
			String line = COMMENT_PATTERN.replace(string,"").trim();
			if (line.isEmpty()) {
				continue;
			}
			if (line.toLowerCase().startsWith("import")) {
				Match<String> matcher = IMPORT.match(line);
				if (matcher.end() >= 0) {
					String filePath = matcher.group(1);
					String fileData = fileHandler.read(filePath);
					Iterable<String> list = lines(fileData);
					parse(list);
					continue;
				}
			}

			ParseZone zone = determineZone(line);
			if (zone == null) {
				zoneData.add(currentZone, line);
			} else {
				currentZone = zone;
			}
		}
	}

	private void populateFeatures() {
		int i = 0;
		for (String entry : zoneData.get(ParseZone.FEATURES)) {
			Match<String> matcher = FEATURES_PATTERN.match(entry);
			if (matcher.end() >= 0) {
				String name = matcher.group(1);
				String code = matcher.group(3);
				featureNames.add(name);
				featureIndices.put(name, i);
				if (!code.isEmpty()) {
					featureIndices.put(code, i);
				}
			} else {
				String message = Templates.create()
						.add("Unrecognized 'FEATURE' command.")
						.data(entry)
						.build();
				throw new ParseException(message);
			}
			i++;
		}
	}

	@NonNull
	private Map<String, FeatureArray<T>> populateModifiers() {
		Map<String, FeatureArray<T>> diacritics = new LinkedHashMap<>();
		Iterable<String> lines = zoneData.get(ParseZone.MODIFIERS);
		for (String entry : lines) {
			Match<String> matcher = SYMBOL_PATTERN.match(entry);
			if (matcher.end() >= 0) {
				String symbol = matcher.group(1);
				List<String> values = TAB_PATTERN.split(matcher.group(2), -1);
				FeatureArray<T> array = new SparseFeatureArray<>(featureModel);
				int i = 0;
				for (String value : values) {
					if (!value.isEmpty()) {
						array.set(i, featureType.parseValue(value));
					}
					i++;
				}
				String diacritic = DOTTED_CIRCLE.replace(symbol, "");
				String norm = Normalizer.normalize(diacritic, Normalizer.Form.NFD);
				diacritics.put(norm, array);
			} else {
				LOG.error("Unrecognized diacritic definition {}", entry);
			}
		}
		return diacritics;
	}

	@NonNull
	private Map<String, FeatureArray<T>> populateSymbols() {
		Map<String, FeatureArray<T>> featureMap = new LinkedHashMap<>();
		Iterable<String> lines = zoneData.get(ParseZone.SYMBOLS);
		for (String entry : lines) {
			Match<String> match = SYMBOL_PATTERN.match(entry);
			if (match.end() >= 0) {
				String symbol = match.group(1);
				List<String> values = TAB_PATTERN.split(match.group(2), -1);
				int size = featureModel.getSpecification().size();
				FeatureArray<T> features
						= new SparseFeatureArray<>(featureModel);
				for (int i = 0; i < size; i++) {
					String value = values.get(i);
					features.set(i, featureType.parseValue(value));
				}
				checkFeatureCollisions(symbol, featureMap, features);
				String norm = Normalizer.normalize(symbol, Normalizer.Form.NFD);
				featureMap.put(norm, features);
			} else {
				LOG.error("Unrecognized symbol definition {}", entry);
			}
		}
		return featureMap;
	}

	/**
	 * @param string
	 *
	 * @return
	 */
	private static @Nullable ParseZone determineZone(@NonNull String string) {
		for (ParseZone zone : ParseZone.values()) {
			if (zone.matches(string)) {
				return zone;
			}
		}
		return null;
	}

	private static <T> void checkFeatureCollisions(
			@NonNull String symbol,
			@NonNull Map<String, FeatureArray<T>> featureMap,
			@NonNull FeatureArray<T> features
	) {
		if (featureMap.containsValue(features)) {
			for (Entry<String, FeatureArray<T>> e : featureMap.entrySet()) {
				if (features.equals(e.getValue())) {
					LOG.warn("Collision between features {} and {} --- both "
							+ "have value {}", symbol, e.getKey(), features);
				}
			}
		}
	}
}
