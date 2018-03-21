/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.language.phonetic.model;

import lombok.NonNull;
import lombok.ToString;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.UndefinedSegment;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Class {@code EmptyFeatureMapping}
 *
 * @author Samantha Fiona McCabe
 * @date 1/1/18
 */
@ToString
public enum EmptyFeatureMapping implements FeatureMapping<Object> {
	INSTANCE;
	
	@NonNull
	@Override
	public String findBestSymbol(@NonNull FeatureArray<Object> featureArray) {
		return "?";
	}

	@NonNull
	@Override
	public Set<String> getSymbols() {
		return Collections.emptySet();
	}

	@Override
	public boolean containsKey(@NonNull String key) {
		return false;
	}

	@NonNull
	@Override
	public Map<String, FeatureArray<Object>> getFeatureMap() {
		return Collections.emptyMap();
	}

	@NonNull
	@Override
	public Map<String, FeatureArray<Object>> getModifiers() {
		return Collections.emptyMap();
	}

	@Override
	public @Nullable FeatureArray<Object> getFeatureArray(@NonNull String key) {
		return null;
	}

	@NonNull
	@Override
	public Segment<Object> parseSegment(@NonNull String string) {
		return new UndefinedSegment<>(string, EmptyFeatureModel.INSTANCE);
	}

	@NonNull
	@Override
	public FeatureModel<Object> getFeatureModel() {
		return EmptyFeatureModel.INSTANCE;
	}
}
