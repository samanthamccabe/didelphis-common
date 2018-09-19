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

package org.didelphis.language.phonetic.features;


import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.utilities.Templates;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;


/**
 * Class {@code EmptyFeatureArray}
 * 
 * A simple immutable implementation of {@link FeatureArray} with no data.
 *
 * @author Samantha Fiona McCabe
 * @since 0.2.0
 */
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode
public final class EmptyFeatureArray<T> implements FeatureArray<T> {

	private final FeatureModel<T> featureModel;

	@Override
	public int size() {
		return getSpecification().size();
	}

	@Override
	public void set(int index, @Nullable T value) { 
		String message = Templates.create()
				.add("{} is immutable and does not support #set(...)")
				.with(getClass())
				.build();
		throw new UnsupportedOperationException(message);
	}

	@Override
	public @Nullable T get(int index) {
		return null;
	}

	@Override
	public boolean matches(@NonNull FeatureArray<T> array) {
		return array instanceof EmptyFeatureArray;
	}

	@Override
	public boolean alter(@NonNull FeatureArray<T> array) {
		String message = Templates.create()
				.add("{} is immutable and does not support #alter(...)")
				.with(getClass())
				.build();
		throw new UnsupportedOperationException(message);
	}

	@Override
	public boolean contains(@Nullable T value) {
		return false;
	}

	@Override
	public int compareTo(@NonNull FeatureArray<T> o) {
		return equals(o) ? 0 : -1;
	}

	@NonNull
	@Override
	public Iterator<T> iterator() {
		return Collections.emptyIterator();
	}

	@NonNull
	@Override
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}
}
