package org.didelphis.common.language.phonetic.model.empty;

import org.didelphis.common.language.phonetic.segments.StandardSegment;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.model.DefaultFeatureSpecification;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureMapping;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;
import org.didelphis.common.language.phonetic.segments.Segment;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by samantha on 2/22/17.
 */
public final class EmptyFeatureMapping<N extends Number> implements
		FeatureMapping<N> {
	
	public static final FeatureMapping<Double>  DOUBLE = new EmptyFeatureMapping<>(EmptyFeatureModel.DOUBLE);
	public static final FeatureMapping<Integer> INT    = new EmptyFeatureMapping<>(EmptyFeatureModel.INT);
	public static final FeatureMapping<Float>   FLOAT  = new EmptyFeatureMapping<>(EmptyFeatureModel.FLOAT);
	public static final FeatureMapping<Byte>    BYTE   = new EmptyFeatureMapping<>(EmptyFeatureModel.BYTE);
	
	private final FeatureModel<N> model;

	private EmptyFeatureMapping(FeatureModel<N> model) {
		this.model = model;
	}
	
	@Override
	public FeatureModel<N> getFeatureModel() {
		return model;
	}

	@Override
	public FeatureSpecification getSpecification() {
		return DefaultFeatureSpecification.EMPTY;
	}

	@Override
	public String findBestSymbol(FeatureArray<N> featureArray) {
		return "";
	}

	@Override
	public Set<String> getSymbols() {
		return Collections.emptySet();
	}

	@Override
	public boolean containsKey(String key) {
		return false;
	}

	@Override
	public Map<String, FeatureArray<N>> getFeatureMap() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, FeatureArray<N>> getModifiers() {
		return Collections.emptyMap();
	}

	@Override
	public FeatureArray<N> getFeatureArray(String key) {
		return new SparseFeatureArray<>(model);
	}

	@Override
	public Segment<N> getSegment(String string) {
		return new StandardSegment<>(string, getFeatureArray(string), model);
	}
}
