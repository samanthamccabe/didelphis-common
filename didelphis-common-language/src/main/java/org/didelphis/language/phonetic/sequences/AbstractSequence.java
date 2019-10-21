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

package org.didelphis.language.phonetic.sequences;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Delegate;

import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.structures.contracts.Delegating;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract Class {@code AbstractSequence}
 *
 * @since 0.1.0
 */
@EqualsAndHashCode
public abstract class AbstractSequence<T>
		implements Sequence<T>, Delegating<List<Segment<T>>> {

	@Delegate private final List<Segment<T>> segments;

	private final FeatureModel<T> featureModel;

	protected AbstractSequence(@NonNull Sequence<T> sequence) {
		segments = new ArrayList<>(sequence);
		featureModel = sequence.getFeatureModel();
	}

	protected AbstractSequence(@NonNull Segment<T> segment) {
		this(segment.getFeatureModel());
		getSegments().add(segment);
	}

	protected AbstractSequence(@NonNull FeatureModel<T> featureModel) {
		segments = new LinkedList<>();
		this.featureModel = featureModel;
	}

	protected AbstractSequence(
			@NonNull Collection<Segment<T>> segments,
			@NonNull FeatureModel<T> featureModel
	) {
		this.segments = new LinkedList<>(segments);
		this.featureModel = featureModel;
	}

	@NonNull
	@Override
	public List<Segment<T>> getDelegate() {
		return getSegments();
	}

	protected List<Segment<T>> getSegments() {
		return segments;
	}

	@NonNull
	@Override
	public final FeatureModel<T> getFeatureModel() {
		return featureModel;
	}
}
