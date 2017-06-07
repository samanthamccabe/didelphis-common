package org.didelphis.common.language.phonetic;

import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;

/**
 * Created by samantha on 2/19/17.
 */
public interface ModelBearer<N>
		extends SpecificationBearer {
	
	FeatureModel<N> getFeatureModel();
}
