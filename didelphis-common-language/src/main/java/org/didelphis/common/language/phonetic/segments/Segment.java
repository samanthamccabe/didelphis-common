package org.didelphis.common.language.phonetic.segments;

import org.didelphis.common.language.phonetic.ModelBearer;
import org.didelphis.common.language.phonetic.features.FeatureArray;

/**
 * Created by samantha on 2/15/17.
 */
public interface  Segment<N extends Number> 
		extends ModelBearer<N>, Comparable<Segment<N>> {

	Segment<N> alter(Segment<N> segment);

	boolean matches(Segment<N> segment);

	String getSymbol();

	FeatureArray<N> getFeatures();
}
