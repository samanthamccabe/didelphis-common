package org.didelphis.common.language.phonetic.model.doubles;

import org.didelphis.common.io.FileHandler;
import org.didelphis.common.language.enums.FormatterMode;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.model.AbstractFeatureMapping;
import org.didelphis.common.language.phonetic.model.empty.EmptyFeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.loaders.AbstractFeatureMappingLoader;
import org.didelphis.common.language.phonetic.model.loaders.FeatureModelLoader;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by samantha on 2/28/17.
 */
public final class DoubleFeatureMapping extends AbstractFeatureMapping<Double> {
	
	private static final DoubleFeatureMapping EMPTY = new DoubleFeatureMapping(
			EmptyFeatureModel.DOUBLE,
			new HashMap<>(),
			new HashMap<>());
	
	private DoubleFeatureMapping(FeatureModel<Double> featureModel,
			Map<String, FeatureArray<Double>> featureMap,
			Map<String, FeatureArray<Double>> modifiers) {
		super(featureModel, featureMap, modifiers);
	}
	
	public static DoubleFeatureMapping getEmpty() {
		return EMPTY;
	}

	public static DoubleFeatureMapping load(String path, FileHandler handler,
			FormatterMode mode) {
		Loader loader = new Loader(path, handler, mode);
		return new DoubleFeatureMapping(loader.getFeatureModel(),
				loader.getFeatureMap(),
				loader.getDiacritics());
	}

	@Override
	protected double totalDifference(FeatureArray<Double> left,
			FeatureArray<Double> right) {
		if (left.size() != right.size()) {
			throw new IllegalArgumentException(
					"Cannot compare feature arrays of different sizes:"
					+ "\n\tleft:  "
					+ left.size()
					+ "\n\tright: "
					+ right.size());
		}
		return IntStream.range(0, left.size())
				.mapToDouble(i -> difference(left.get(i), right.get(i)))
				.sum();
	}

	@Override
	protected double difference(Double x, Double y) {
		return Math.abs(x - y);
	}

	private static final class Loader
			extends AbstractFeatureMappingLoader<Double> {

		private Loader(String path, FileHandler handler, FormatterMode mode) {
			super(path, handler, mode);
		}

		@Override
		protected Double getValue(String string, Double defaultValue) {
			Form form = Form.NFKC;
			String normalized = Normalizer.normalize(string, form);

			if (normalized.equals("+")) {
				return 1.0;
			} else if (normalized.equals("-")) {
				return -1.0;
			} else if (string.isEmpty()) {
				return defaultValue;
			} else {
				return Double.valueOf(normalized);
			}
		}

		@Override
		protected FeatureModel<Double> loadModel(String path) {
			return FeatureModelLoader.loadDouble(path, fileHandler);
		}

		@Override
		protected FeatureModel<Double> loadModel(List<String> lines) {
			return FeatureModelLoader.loadDouble(lines);
		}
	}
}
