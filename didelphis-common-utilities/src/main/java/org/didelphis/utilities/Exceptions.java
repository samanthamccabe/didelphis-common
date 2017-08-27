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

package org.didelphis.utilities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility Class {@code Exceptions}
 * <p>
 * Allows cleaner and more fluent generation of exceptions, and better reporting
 * and formatting of relevant data pertaining to the cause.
 *
 * @author Samantha Fiona McCabe
 * @date 2017-08-26
 * @since 0.2.0
 */
@Slf4j
@UtilityClass
public class Exceptions {

	private final Pattern COMPILE = Pattern.compile("\\{}");

	/**
	 * Creates a builder for the exception of the provided type.
	 *
	 * @param type the {@link Class} of the exception to be created
	 * @param <X> the type of the exception to be created
	 *
	 * @return a new builder
	 */
	public <X extends Exception> ExceptionBuilder<X> create(Class<X> type) {
		return new ExceptionBuilder<>(type, new ArrayDeque<>());
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Data
	public static final class ExceptionBuilder<X extends Exception> {

		private final Class<X> type;
		private final Deque<String> messages;

		private Object data;
		private Throwable cause;

		@NotNull
		public ExceptionBuilder<X> add(String string) {
			messages.push(string);
			return this;
		}

		@NotNull
		public ExceptionBuilder<X> with(Object... objects) {
			String message = messages.pop();
			for (Object object : objects) {
				String string = object == null ? "null" : object.toString();
				message = COMPILE.matcher(message).replaceFirst(string);
			}
			messages.push(message);
			return this;
		}

		public ExceptionBuilder<X> data(Object object) {
			data = object;
			return this;
		}

		@NotNull
		public X build() {
			String baseMessage = messages.isEmpty()
					? "No message provided."
					: messages.stream().collect(Collectors.joining(" "));
			StringBuilder stringBuilder = new StringBuilder(baseMessage);
			stringBuilder.append('\n');
			if (data != null) {
				stringBuilder.append("With Data:\n");
				stringBuilder.append(Objects.toString(data, "null"));
				stringBuilder.append('\n');
			}

			String message = stringBuilder.toString();
			try {
				return cause == null
						? type.getConstructor(String.class)
						.newInstance(message)
						: type.getConstructor(String.class, Throwable.class)
								.newInstance(message, cause);
			} catch (ReflectiveOperationException e) {
				throw new UnsupportedOperationException(message, e);
			}
		}

		public void throwException() throws X {
			throw build();
		}
	}
}
