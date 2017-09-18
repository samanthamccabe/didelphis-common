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

package org.didelphis.io;

import lombok.ToString;
import lombok.NonNull;

/**
 * @author Samantha Fiona McCabe
 * @date 10/13/2014
 */
@ToString
public enum NullFileHandler implements FileHandler {
	INSTANCE;

	@Override
	public CharSequence read( @NonNull String path) {
		return "";
	}

	@Override
	public boolean writeString( @NonNull String path,  @NonNull CharSequence data) {
		return false;
	}

}
