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
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.didelphis.language.phonetic.ModelBearer;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.jetbrains.annotations.NotNull;

/**
 * @author Samantha Fiona McCabe
 * @date 3/1/2016
 */
@RequiredArgsConstructor
@ToString(exclude = "featureModel")
@EqualsAndHashCode(exclude = "featureModel")
public class Constraint<T> implements ModelBearer<T> {

	private final FeatureModel<T> featureModel;

	@Getter private final FeatureArray<T> source;
	@Getter private final FeatureArray<T> target;

	public Constraint(@NotNull Constraint<T> constraint) {
		source = constraint.source;
		target = constraint.target;
		featureModel = constraint.featureModel;
	}

	@NotNull
	@Override
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}

}
