package org.didelphis.language.phonetic.model.literal;

import lombok.NonNull;

import org.didelphis.language.phonetic.model.FeatureSpecification;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Singleton implementation to support the use if literal values which consist
 * only of Strings.
 */
public enum LiteralFeatureSpecification implements FeatureSpecification {
	INSTANCE;

	@Override
	public int size() {
		return 0;
	}

	@Override
	public @NonNull Map<String, Integer> getFeatureIndices() {
		return Collections.emptyMap();
	}

	@Override
	public int getIndex(@NonNull String featureName) {
		return -1;
	}

	@Override
	public @NonNull List<String> getFeatureNames() {
		return Collections.emptyList();
	}
}
