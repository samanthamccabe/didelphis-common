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
 * @author Samantha Fiona McCabe
 * @date 2017-02-04
 * @since 0.1.0
 */
@EqualsAndHashCode
public abstract class AbstractSequence<T>
		implements Sequence<T>, Delegating<List<Segment<T>>> {

	@Delegate protected final List<Segment<T>> segments;

	protected final FeatureModel<T> featureModel;

	protected AbstractSequence(@NonNull Sequence<T> sequence) {
		segments = new ArrayList<>(sequence);
		featureModel = sequence.getFeatureModel();
	}

	protected AbstractSequence(@NonNull Segment<T> segment) {
		this(segment.getFeatureModel());
		segments.add(segment);
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
		return segments;
	}
}
