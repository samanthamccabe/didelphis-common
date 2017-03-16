package org.didelphis.common.language.phonetic.model;

import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samantha on 2/16/17.
 * 
 * Reference implementation of the {@code FeatureSpecification} interface
 */
public final class DefaultFeatureSpecification implements FeatureSpecification {
	
	public static final FeatureSpecification EMPTY = new DefaultFeatureSpecification();
	
	private final int size;

	private final List<String> featureNames;
	private final List<FeatureType> featureTypes;
	private final Map<String, Integer> featureIndices;

	private DefaultFeatureSpecification() {
		this(new ArrayList<>(), new ArrayList<>(), new HashMap<>());
	}
	
	public DefaultFeatureSpecification(
			List<String> names, 
			List<FeatureType> types,
			Map<String, Integer> indices) {
		size = names.size();
		featureNames = names;
		featureTypes = types;
		featureIndices = indices;
	}
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public Map<String, Integer> getFeatureIndices() {
		return Collections.unmodifiableMap(featureIndices);
	}

	@Override
	public int getIndex(String featureName) {
		Integer index = featureIndices.get(featureName);
		return index == null ? -1 : index;
	}

	@Override
	public List<String> getFeatureNames() {
		return Collections.unmodifiableList(featureNames);
	}

	@Override
	public List<FeatureType> getFeatureTypes() {
		return Collections.unmodifiableList(featureTypes);
	}

	@Override
	public int hashCode() {
		int code = size;
		code *= 31 + featureIndices.hashCode();
		code *= 31 + featureNames.hashCode();
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof DefaultFeatureSpecification) {
			DefaultFeatureSpecification that = (DefaultFeatureSpecification) obj;
			return size == that.size 
			       && featureIndices.equals(that.featureIndices) 
			       && featureNames.equals(that.featureNames);
		}
		return false;
	}

	@Override
	public String toString() {
		return "DefaultFeatureSpecification"
		       + "{ size=" + size
		       + ", featureNames=" + featureNames
		       + ", featureTypes=" + featureTypes
		       + ", featureIndices=" + featureIndices
		       + " }";
	}
}
