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
import lombok.extern.slf4j.Slf4j;
import org.didelphis.io.FileHandler;
import org.didelphis.io.NullFileHandler;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.structures.Suppliers;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import static org.didelphis.utilities.Splitter.lines;

/**
 * Class {@code FeatureModelLoader}
 *
 * @author Samantha Fiona McCabe
 * @date 2017-02-18
 * @since 0.1.0
 */
@ToString
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class FeatureModelLoader<T> {

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
	static final Pattern FEATURES_PATTERN = compile("(?<name>\\w+)(\\s+(?<code>\\w*))?", CASE_INSENSITIVE);
	static final Pattern TRANSFORM        = compile("\\s*>\\s*");
	static final Pattern BRACKETS         = compile("[\\[\\]]");
	static final Pattern EQUALS           = compile("\\s*=\\s*");
	static final Pattern IMPORT           = compile("import\\s+['\"]([^'\"]+)['\"]", CASE_INSENSITIVE);
	static final Pattern COMMENT_PATTERN  = compile("\\s*%.*");
	static final Pattern SYMBOL_PATTERN   = compile("([^\\t]+)\\t(.*)");
	static final Pattern TAB_PATTERN      = compile("\\t");
	static final Pattern DOTTED_CIRCLE    = compile("◌", LITERAL);
	
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
		this(featureType,
				fileHandler,
				lines(fileHandler.readOrThrow(path,
						NullPointerException.class
				))
		);
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
	public Constraint<T> parseConstraint(@NonNull CharSequence entry) {
		String[] split = TRANSFORM.split(entry, 2);
		String source = split[0];
		String target = split[1];
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

		for (CharSequence string : zoneData.get(ParseZone.ALIASES)) {
			String[] split = EQUALS.split(string, 2);
			String alias = BRACKETS.matcher(split[0]).replaceAll("");
			String value = split[1];
			aliases.put(alias, featureModel.parseFeatureString(value));
		}

		// populate constraints
		zoneData.get(ParseZone.CONSTRAINTS)
				.stream()
				.map(this::parseConstraint)
				.forEach(constraints::add);

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
		for (CharSequence string : lines) {
			String line = COMMENT_PATTERN.matcher(string).replaceAll("").trim();
			if (line.isEmpty()) {
				continue;
			}
			if (line.toLowerCase().startsWith("import")) {
				Matcher matcher = IMPORT.matcher(line);
				if (matcher.find()) {
					String filePath = matcher.group(1);
					CharSequence fileData = fileHandler.read(filePath);
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
				throw ParseException.builder()
						.add("Unrecognized 'FEATURE' command.")
						.data(entry)
						.build();
			}
			i++;
		}
	}

	@NonNull
	private Map<String, FeatureArray<T>> populateModifiers() {
		Map<String, FeatureArray<T>> diacritics = new LinkedHashMap<>();
		Iterable<String> lines = zoneData.get(ParseZone.MODIFIERS);
		for (CharSequence entry : lines) {
			Matcher matcher = SYMBOL_PATTERN.matcher(entry);
			if (matcher.matches()) {
				CharSequence symbol = matcher.group(1);
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
				String norm = Normalizer.normalize(diacritic, Normalizer.Form.NFD);
				diacritics.put(norm, array);
			} else {
				log.error("Unrecognized diacritic definition {}", entry);
			}
		}
		return diacritics;
	}

	@NonNull
	private Map<String, FeatureArray<T>> populateSymbols() {
		Map<String, FeatureArray<T>> featureMap = new LinkedHashMap<>();
		Iterable<String> lines = zoneData.get(ParseZone.SYMBOLS);
		for (CharSequence entry : lines) {
			Matcher matcher = SYMBOL_PATTERN.matcher(entry);
			if (matcher.matches()) {
				String symbol = matcher.group(1);
				String[] values = TAB_PATTERN.split(matcher.group(2), -1);
				int size = featureModel.getSpecification().size();
				FeatureArray<T> features
						= new SparseFeatureArray<>(featureModel);
				for (int i = 0; i < size; i++) {
					String value = values[i];
					features.set(i, featureType.parseValue(value));
				}
				checkFeatureCollisions(symbol, featureMap, features);
				String norm = Normalizer.normalize(symbol, Normalizer.Form.NFD);
				featureMap.put(norm, features);
			} else {
				log.error("Unrecognized symbol definition {}", entry);
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
		return Arrays.stream(ParseZone.values())
				.filter(zone -> zone.matches(string))
				.findFirst()
				.orElse(null);
	}

	private static <T> void checkFeatureCollisions(
			@NonNull String symbol,
			@NonNull Map<String, FeatureArray<T>> featureMap,
			@NonNull FeatureArray<T> features
	) {
		if (featureMap.containsValue(features)) {
			for (Entry<String, FeatureArray<T>> e : featureMap.entrySet()) {
				if (features.equals(e.getValue())) {
					log.warn("Collision between features {} and {} --- both "
							+ "have value {}", symbol, e.getKey(), features);
				}
			}
		}
	}
}
