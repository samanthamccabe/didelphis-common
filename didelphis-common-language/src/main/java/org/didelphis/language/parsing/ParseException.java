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

package org.didelphis.language.parsing;

import org.didelphis.utilities.Exceptions;
import org.jetbrains.annotations.Nullable;

/**
 * Exception {@code ParseException}
 *
 * @author Samantha Fiona McCabe
 * @date 8/25/2014
 * @since 0.1.0
 */
public class ParseException extends RuntimeException {

	public ParseException() {
	}

	public ParseException(String message) {
		super(message);
	}

	@Deprecated
	public ParseException(String message, String data) {
		this(message + " --- Data: " + data);
	}
	
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParseException(Throwable cause) {
		super(cause);
	}

	public static Exceptions.ExceptionBuilder<ParseException> builder() {
		return builder(null);
	}

	public static Exceptions.ExceptionBuilder<ParseException> builder(
			@Nullable Throwable cause
	) {
		return Exceptions.create(ParseException.class, cause);
	}

}
