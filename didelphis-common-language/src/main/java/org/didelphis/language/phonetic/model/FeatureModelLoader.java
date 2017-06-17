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

package org.didelphis.language.phonetic.model;

import org.didelphis.io.FileHandler;
import org.didelphis.language.exceptions.ParseException;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Split;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.LITERAL;
import static java.util.regex.Pattern.compile;

/**
 * Created by samantha on 2/18/17.
 */
public final class FeatureModelLoader<T> {

	private static final Logger LOG = LoggerFactory.getLogger(FeatureModelLoader.class);

	private static final Pattern FEATURES_PATTERN = compile(
			"(?<name>\\w+)(\\s+(?<code>\\w*))?",
			CASE_INSENSITIVE);

	private static final Pattern TRANSFORM = compile("\\s*>\\s*");
	private static final Pattern BRACKETS = compile("[\\[\\]]");
	private static final Pattern EQUALS = compile("\\s*=\\s*");

	private static final Pattern IMPORT_PATTERN = compile("import\\s+['\"]([^'\"]+)['\"]", CASE_INSENSITIVE);
	private static final Pattern COMMENT_PATTERN = compile("\\s*%.*");
	private static final Pattern SYMBOL_PATTERN = compile("([^\\t]+)\\t(.*)");
	private static final Pattern TAB_PATTERN = compile("\\t");
	private static final Pattern DOTTED_CIRCLE = compile("◌", LITERAL);

	private final List<String> featureNames;
	private final Map<String, Integer> featureIndices;

	private final MultiMap<ParseZone, String> zoneData;

	private final FeatureType<T> featureType;
	private final FileHandler fileHandler;

	private FeatureSpecification specification;
	private FeatureModel<T> featureModel;
	private FeatureMapping<T> featureMapping;

	public FeatureModelLoader(FeatureType<T> featureType, FileHandler fileHandler, String path) {
		this(featureType, fileHandler, Split.splitLines(fileHandler.read(path)));
	}

	public FeatureModelLoader(FeatureType<T> featureType, FileHandler fileHandler, Iterable<String> lines) {

		this.featureType = featureType;
		this.fileHandler = fileHandler;

		featureNames = new ArrayList<>();
		featureIndices = new HashMap<>();

		Map<ParseZone, Collection<String>> map = new EnumMap<>(ParseZone.class);
		for (ParseZone zone : ParseZone.values()) {
			map.put(zone, new ArrayList<>());
		}
		zoneData = new GeneralMultiMap<>(map, ArrayList.class);

		parse(lines);
		populate();
	}

	/**
	 * @param string
	 * @return
	 */
	private static ParseZone determineZone(String string) {
		return Arrays.stream(ParseZone.values())
				.filter(zone -> zone.matches(string))
				.findFirst().orElse(null);
	}

	private void populate() {
		specification = parseSpecification();

		Map<String, FeatureArray<T>> aliases = new HashMap<>();
		List<Constraint<T>> constraints = new ArrayList<>();

		featureModel = new GeneralFeatureModel<>(featureType, specification, constraints, aliases);

		for (CharSequence string : zoneData.get(ParseZone.ALIASES)) {
			String[] split = EQUALS.split(string, 2);
			String alias = BRACKETS.matcher(split[0]).replaceAll("");
			String value = split[1];
			aliases.put(alias, featureModel.parseFeatureString(value));
		}

		for (String entry : zoneData.get(ParseZone.CONSTRAINTS)) {
			constraints.add(parseConstraint(entry));
		}

		featureMapping = new GeneralFeatureMapping<>(featureModel, populateSymbols(), populateModifiers());
	}

	@NotNull
	public Constraint<T> parseConstraint(String entry) {
		String[] split = TRANSFORM.split(entry, 2);
		String source = split[0];
		String target = split[1];
		FeatureArray<T> sMap = featureModel.parseFeatureString(source);
		FeatureArray<T> tMap = featureModel.parseFeatureString(target);
		return new Constraint<>(entry, sMap, tMap, featureModel);
	}

	@NotNull
	public FeatureSpecification getSpecification() {
		return specification;
	}

	@NotNull
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}

	@NotNull
	public FeatureMapping<T> getFeatureMapping() {
		return featureMapping;
	}

	private FeatureSpecification parseSpecification() {
		// 1. populate the fields, create raw representations
		// 2. instantiate the featureModel object w size
		// 3. create correct internal objects with instance
		// 4. add objects back to instance

		populateFeatures();
		// Once the main feature definitions are parsed, it's possible to create
		// the featureModel instance
		return new DefaultFeatureSpecification(featureNames, featureIndices);
	}

	/**
	 * Traverses the provided data and sorts commands into the appropriate bins
	 */
	private void parse(Iterable<String> lines) {
		ParseZone currentZone = ParseZone.NONE;
		for (String string : lines) {
			String line = COMMENT_PATTERN.matcher(string).replaceAll("").trim();
			if (line.isEmpty()) {
				continue;
			}

			if (line.toLowerCase().startsWith("import")) {
				Matcher matcher = IMPORT_PATTERN.matcher(line);
				if (matcher.find()) {
					String filePath = matcher.group(1);
					CharSequence fileData = fileHandler.read(filePath);
					List<String> list = Split.splitLines(fileData);
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
			Matcher matcher = FEATURES_PATTERN.matcher(entry);
			if (matcher.find()) {
				String name = matcher.group("name");
				String code = matcher.group("code");
				featureNames.add(name);
				featureIndices.put(name, i);
				if (!code.isEmpty()) {
					featureIndices.put(code, i);
				}
			} else {
				throw new ParseException("Unrecognized FEATURE command", entry);
			}
			i++;
		}
	}

	private Map<String, FeatureArray<T>> populateModifiers() {
		Map<String, FeatureArray<T>> diacritics = new LinkedHashMap<>();
		Collection<String> lines = zoneData.get(ParseZone.MODIFIERS);
		for (String entry : lines) {
				Matcher matcher = SYMBOL_PATTERN.matcher(entry);
				if (matcher.matches()) {
					String symbol = matcher.group(1);
					String[] values = TAB_PATTERN.split(matcher.group(2), -1);
					FeatureArray<T> array = new SparseFeatureArray<>(featureModel);
					int i = 0;
					for (String value : values) {
						if (!value.isEmpty()) {
							array.set(i, featureType.parseValue(value));
						}
						i++;
					}
					String diacritic = DOTTED_CIRCLE.matcher(symbol).replaceAll("");
					diacritics.put(diacritic, array);
				} else {
					LOG.error("Unrecognized diacritic definition {}", entry);
				}
			}
		return diacritics;
	}

	private Map<String, FeatureArray<T>> populateSymbols() {
		Map<String, FeatureArray<T>> featureMap = new LinkedHashMap<>();
		Collection<String> lines = zoneData.get(ParseZone.SYMBOLS);
		for (String entry : lines) {
			Matcher matcher = SYMBOL_PATTERN.matcher(entry);
			if (matcher.matches()) {
				String symbol = matcher.group(1);
				String[] values = TAB_PATTERN.split(matcher.group(2), -1);
				int size = featureModel.getSpecification().size();
				FeatureArray<T> features = new SparseFeatureArray<>(featureModel);
				for (int i = 0; i < size; i++) {
					String value = values[i];
					features.set(i, featureType.parseValue(value));
				}
				checkFeatureCollisions(featureMap, symbol, features);
				featureMap.put(symbol, features);
			} else {
				LOG.error("Unrecognized symbol definition {}", entry);
			}
		}
		return featureMap;
	}

	private static <T> void checkFeatureCollisions(
			Map<String, FeatureArray<T>> featureMap,
			String symbol, FeatureArray<T> features) {
		if (featureMap.containsValue(features)) {
			for (Entry<String, FeatureArray<T>> e : featureMap.entrySet()) {
				if (features.equals(e.getValue())) {
					LOG.warn("Collision between features {} and {} --- both "
							+ "have value {}", symbol, e.getKey(), features);
				}
			}
		}
	}

	/**
	 * Enum {@code ParseZone}
	 */
	private enum ParseZone {
		FEATURES, ALIASES, CONSTRAINTS, SYMBOLS, MODIFIERS, NONE;

		boolean matches(String string) {
			return name().equals(string.toUpperCase());
		}
	}
}
