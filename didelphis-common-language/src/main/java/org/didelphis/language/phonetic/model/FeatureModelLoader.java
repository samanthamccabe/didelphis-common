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

package org.didelphis.language.phonetic.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import org.didelphis.io.FileHandler;
import org.didelphis.io.NullFileHandler;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.SparseFeatureArray;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Templates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.text.Normalizer.*;
import static org.didelphis.language.phonetic.model.ModelConstants.*;
import static org.didelphis.utilities.Splitter.*;

/**
 * Class {@code FeatureModelLoader}
 *
 * @since 0.1.0
 */
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class FeatureModelLoader<T> {

	private static final Logger LOG = LogManager.getLogger(FeatureModelLoader.class);

	/**
	 * Enum {@code ParseZone}
	 */
	private enum ParseZone {
		FEATURES, ALIASES, CONSTRAINTS, SYMBOLS, MODIFIERS, NONE;

		boolean matches(@NonNull String string) {
			return name().equals(string.toUpperCase());
		}
	}

	final MultiMap<ParseZone, String> zoneData;

	final String               basePath;
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

		basePath = "";

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
		this.featureType = featureType;
		this.fileHandler = fileHandler;

		featureNames = new ArrayList<>();
		featureIndices = new HashMap<>();
		basePath = path;

		zoneData = new GeneralMultiMap<>(HashMap.class, ArrayList.class);
		for (ParseZone zone : ParseZone.values()) {
			zoneData.put(zone, new ArrayList<>());
		}

		try {
			String read = fileHandler.read(path);
			parse(lines(read));
			populate();
		} catch (IOException e) {
			LOG.error("Unexpected failure encountered: {}", e);
			throw new ParseException("Failed to read from path " + path, e);
		}
	}

	public FeatureModelLoader(
			@NonNull FeatureType<T> featureType,
			@NonNull FileHandler fileHandler,
			@NonNull Iterable<String> lines,
			@NonNull String basePath
	) {
		this.basePath = basePath;
		this.featureType = featureType;
		this.fileHandler = fileHandler;

		featureNames = new ArrayList<>();
		featureIndices = new HashMap<>();

		zoneData = new GeneralMultiMap<>(HashMap.class, ArrayList.class);
		for (ParseZone zone : ParseZone.values()) {
			zoneData.put(zone, new ArrayList<>());
		}

		parse(lines);
		populate();
	}

	@NonNull
	public Constraint<T> parseConstraint(@NonNull String entry) {
		List<String> split = TRANSFORM.split(entry, 2);

		if (split.size() < 2) {
			throw new ParseException("Unable to read constraint: " + entry);
		}

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
			String line = COMMENT_PATTERN.replace(string, "");
			if (line.isEmpty()) {
				continue;
			}
			Match<String> matcher = IMPORT.match(line);
			if (matcher.matches()) {
				if (matcher.end() >= 0) {
					String parent = PARENT_PATH.replace(basePath, "$1");
					String filePath = parent + matcher.group(1);
					try {
						String data = fileHandler.read(filePath);
						Iterable<String> list = lines(data);
						parse(list);
						continue;
					} catch (IOException e) {
						LOG.error("Unexpected failure encountered: {}", e);
						throw new ParseException("Unable to read from "
								+ filePath, e);
					}
				}
			}

			ParseZone zone = determineZone(line.trim());
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
				if (code != null && !code.isEmpty()) {
					featureIndices.put(code, i);
				}
			} else if (!entry.trim().isEmpty()) {
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
				String group = matcher.group(2);
				List<String> values = TAB_PATTERN.split(group, -1);
				FeatureArray<T> array = new SparseFeatureArray<>(featureModel);
				int i = 0;
				for (String value : values) {
					if (!value.isEmpty()) {
						array.set(i, featureType.parseValue(value));
					}
					i++;
				}
				String diacritic = DOTTED_CIRCLE.replace(symbol, "");
				String norm = normalize(diacritic, Form.NFD);
				diacritics.put(norm, array);
			} else if (!entry.trim().isEmpty()) {
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

				if (size > values.size()) {
					String message = Templates.create()
							.add("Too few features are provided!")
							.add("Found {} but was expecting {}")
							.with(values.size(), size)
							.data(entry)
							.build();
					throw new ParseException(message);
				}

				FeatureArray<T> features
						= new SparseFeatureArray<>(featureModel);
				for (int i = 0; i < size; i++) {
					String value = values.get(i);
					features.set(i, featureType.parseValue(value));
				}
				if (checkFeatureCollisions(symbol, featureMap, features)) {
					String norm = normalize(symbol, Form.NFD);
					featureMap.put(norm, features);
				}
			} else if (!entry.trim().isEmpty()) {
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

	private static <T> boolean checkFeatureCollisions(
			@NonNull String symbol,
			@NonNull Map<String, FeatureArray<T>> featureMap,
			@NonNull FeatureArray<T> features
	) {
		if (featureMap.containsValue(features)) {
			for (Entry<String, FeatureArray<T>> e : featureMap.entrySet()) {
				if (features.equals(e.getValue())) {
					LOG.warn("Collision between features {} and {} --- both "
							+ "have value {}", symbol, e.getKey(), features);
					return false;
				}
			}
		}
		return true;
	}
}
