package org.didelphis.common.language.phonetic.model.empty;


import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.model.Constraint;
import org.didelphis.common.language.phonetic.model.FeatureType;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by samantha on 2/20/17.
 */
public final class EmptyFeatureModel<N extends Number> implements FeatureModel<N> {
	
	public static final FeatureModel<Double>  DOUBLE = new EmptyFeatureModel<>();
	public static final FeatureModel<Integer> INT    = new EmptyFeatureModel<>();
	public static final FeatureModel<Float>   FLOAT  = new EmptyFeatureModel<>();
	public static final FeatureModel<Byte>    BYTE   = new EmptyFeatureModel<>(); 
	
	private EmptyFeatureModel(){}
	
	@Override
	public List<Constraint<N>> getConstraints() {
		return Collections.emptyList();
	}

	@Override
	public FeatureArray<N> parseFeatureString(String string) {
		return new SparseFeatureArray<>(this);
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Map<String, Integer> getFeatureIndices() {
		return Collections.emptyMap();
	}

	@Override
	public int getIndex(String featureName) {
		return -1;
	}

	@Override
	public List<String> getFeatureNames() {
		return Collections.emptyList();
	}

	@Override
	public List<FeatureType> getFeatureTypes() {
		return Collections.emptyList();
	}
	
}
