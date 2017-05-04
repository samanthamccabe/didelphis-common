package org.didelphis.common.language.phonetic.model.interfaces;

import org.didelphis.common.language.phonetic.model.FeatureType;

import java.util.List;
import java.util.Map;

/**
 * Created by samantha on 2/16/17.
 * 
 * The feature featureModel holds only the feature names and types.
 */
public interface FeatureSpecification {

	int size();

	Map<String, Integer> getFeatureIndices();

	int getIndex(String featureName);

	List<String> getFeatureNames();

	List<FeatureType> getFeatureTypes();

}
