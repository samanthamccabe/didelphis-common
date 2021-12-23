package org.didelphis.language.phonetic.model.literal;

import lombok.NonNull;

import org.didelphis.language.phonetic.features.EmptyFeatureArray;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import org.didelphis.language.phonetic.features.IntegerFeature;
import org.didelphis.language.phonetic.model.Constraint;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureSpecification;

import java.util.Collections;
import java.util.List;

public enum LiteralFeatureModel implements FeatureModel {
	INSTANCE;

	@Override
	public @NonNull FeatureSpecification getSpecification() {
		return LiteralFeatureSpecification.INSTANCE;
	}

	@Override
	public @NonNull List<Constraint> getConstraints() {
		return Collections.emptyList();
	}

	@Override
	public @NonNull FeatureArray parseFeatureString(@NonNull String string) {
		return new EmptyFeatureArray(this);
	}

	@Override
	public @NonNull FeatureType getFeatureType() {
		return IntegerFeature.INSTANCE;
	}
}
