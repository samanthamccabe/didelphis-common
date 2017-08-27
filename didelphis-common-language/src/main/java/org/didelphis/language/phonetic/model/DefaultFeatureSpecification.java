/*=============================================================================
 = Copyright (c) 2017. Samantha Fiona McCabe (Didelphis)                                  
 =                                                                              
 = Licensed under the Apache License, Version 2.0 (the "License");              
 = you may not use this file except in compliance with the License.             
 = You may obtain a copy of the License at                                      
 =     http://www.apache.org/licenses/LICENSE-2.0                               
 = Unless required by applicable law or agreed to in writing, software          
 = distributed under the License is distributed on an "AS IS" BASIS,            
 = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     
 = See the License for the specific language governing permissions and          
 = limitations under the License.                                               
 =============================================================================*/

package org.didelphis.language.phonetic.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by samantha on 2/16/17.
 * <p>
 * Reference implementation of the {@code FeatureSpecification} interface
 */
@ToString
@EqualsAndHashCode
public final class DefaultFeatureSpecification implements FeatureSpecification {

	public static final FeatureSpecification EMPTY
			= new DefaultFeatureSpecification();

	private final int size;

	//                                                                        <*
	@Getter(onMethod=@__({@Override, @NotNull})) 
	private final List<String> featureNames;
	
	@Getter(onMethod=@__({@Override, @NotNull}))
	private final Map<String, Integer> featureIndices;
	//                                                                        *>

	public DefaultFeatureSpecification() {
		this(new ArrayList<>(), new HashMap<>());
	}

	/**
	 * @param names
	 * @param indices
	 */
	public DefaultFeatureSpecification(
			@NotNull List<String> names, @NotNull Map<String, Integer> indices
	) {
		size = names.size();
		featureNames = Collections.unmodifiableList(names);
		featureIndices = Collections.unmodifiableMap(indices);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int getIndex(@NotNull String featureName) {
		Integer index = featureIndices.get(featureName);
		return index == null ? -1 : index;
	}

}
