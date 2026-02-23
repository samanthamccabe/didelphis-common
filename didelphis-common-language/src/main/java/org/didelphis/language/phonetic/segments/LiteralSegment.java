package org.didelphis.language.phonetic.segments;

import lombok.NonNull;

import org.didelphis.language.phonetic.features.EmptyFeatureArray;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.literal.LiteralFeatureModel;

public class LiteralSegment implements Segment {

	private static final FeatureArray EMPTY_FEATURE_ARRAY
			= new EmptyFeatureArray(LiteralFeatureModel.INSTANCE);

	private final String symbol;

	public LiteralSegment(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public @NonNull FeatureModel getFeatureModel() {
		return LiteralFeatureModel.INSTANCE;
	}

	@Override
	public boolean alter(@NonNull Segment segment) {
		return false;
	}

	@Override
	public @NonNull String getSymbol() {
		return symbol;
	}

	@Override
	public @NonNull FeatureArray getFeatures() {
		return EMPTY_FEATURE_ARRAY;
	}

	@Override
	public boolean isDefinedInModel() {
		return false;
	}
}
