/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.language.phonetic.features;


import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.utilities.Templates;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;


/**
 * Class {@code EmptyFeatureArray}
 * <p>
 * A simple immutable implementation of {@link FeatureArray} with no data.
 *
 * @since 0.2.0
 */
@ToString
@EqualsAndHashCode
public final class EmptyFeatureArray<T> implements FeatureArray<T> {

	private final FeatureModel<T> featureModel;
	private final int size;

	public EmptyFeatureArray(FeatureModel<T> featureModel) {
		this.featureModel = featureModel;
		size = featureModel.getSpecification().size();
	}

	public EmptyFeatureArray(FeatureArray<T> featureArray) {
		featureModel = featureArray.getFeatureModel();
		size = featureModel.getSpecification().size();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void set(int index, @Nullable T value) {
		String message = Templates.create()
				.add("{} is immutable and does not support method #set")
				.with(getClass())
				.build();
		throw new UnsupportedOperationException(message);
	}

	@Override
	public @Nullable T get(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " +
					featureModel.getSpecification().size());
		}
		return null;
	}

	@Override
	public boolean matches(@NonNull FeatureArray<T> array) {
		return array instanceof EmptyFeatureArray;
	}

	@Override
	public boolean alter(@NonNull FeatureArray<T> array) {
		String message = Templates.create()
				.add("{} is immutable and does not support method #alter")
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
