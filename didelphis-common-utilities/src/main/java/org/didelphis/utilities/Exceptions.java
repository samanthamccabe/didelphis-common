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

package org.didelphis.utilities;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility Class {@code Exceptions}
 *
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
	 * @param cause the {@link Throwable} cause of this exception; may be null
	 *      if there is no cause.
	 * @param <X> the type of the exception to be created
	 *
	 * @return a new builder
	 */
	@NonNull
	public <X extends Exception> ExceptionBuilder<X> create(
			@NonNull Class<X> type, 
			@Nullable Throwable cause) {
		return new ExceptionBuilder<>(type, cause);
	}

	/**
	 * Creates a builder for the exception of the provided type.
	 *
	 * @param type the {@link Class} of the exception to be created
	 * @param <X> the type of the exception to be created
	 *
	 * @return a new builder
	 */
	public <X extends Exception> ExceptionBuilder<X> create(
			@NonNull Class<X> type) {
		return new ExceptionBuilder<>(type, null);
	}
	
	/**
	 * Convenience method for generating an {@link UnsupportedOperationException}
	 *
	 * @return an {@link ExceptionBuilder}
	 */
	@NonNull
	public ExceptionBuilder<UnsupportedOperationException> unsupportedOperation() {
		return new ExceptionBuilder<>(UnsupportedOperationException.class);
	}

	/**
	 * Convenience method for generating an {@link IndexOutOfBoundsException}
	 *
	 * @return an {@link ExceptionBuilder}
	 */
	@NonNull
	public ExceptionBuilder<IndexOutOfBoundsException> indexOutOfBounds() {
		return new ExceptionBuilder<>(IndexOutOfBoundsException.class);
	}

	/**
	 * Convenience method for generating an {@link IllegalArgumentException}
	 *
	 * @return an {@link ExceptionBuilder}
	 */
	@NonNull
	public ExceptionBuilder<IllegalArgumentException> illegalArgument() {
		return new ExceptionBuilder<>(IllegalArgumentException.class);
	}
	
	public static final class ExceptionBuilder<X extends Exception> {

		private final Class<X> type;
		private final Deque<String> messages;
		private final Collection<Object> data;

		private final Throwable cause;

		private ExceptionBuilder(Class<X> type) {
			this.type = type;
			messages = new ArrayDeque<>();
			data = new ArrayList<>();
			cause = null;
		}

		private ExceptionBuilder(Class<X> type, Throwable cause) {
			this.type = type;
			messages = new ArrayDeque<>();
			data = new ArrayList<>();
			this.cause = cause;
		}

		@NonNull
		public ExceptionBuilder<X> add(String string, String... strings) {
			if (strings.length > 0) {
				Collection<String> list = new ArrayList<>();
				list.add(string);
				list.addAll(Arrays.asList(strings));
				messages.push(list.stream().collect(Collectors.joining(" ")));
			} else {
				messages.push(string);
			}
			return this;
		}

		@NonNull
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
			data.add(object);
			return this;
		}

		/**
		 * 
		 * @return a new {@link Exception} of the builder's type. with message
		 *      and data as 
		 * @throws UnsupportedOperationException if the {@link Exception} being
		 *      created does not implement the standard constructors, because
		 *      instantiation is done reflectively
		 */
		@NonNull
		public X build() {
			String baseMessage = messages.isEmpty()
					? "No message provided."
					: messages.stream().collect(Collectors.joining(" "));
			StringBuilder stringBuilder = new StringBuilder(baseMessage);
			stringBuilder.append('\n');
			if (!data.isEmpty()) {
				stringBuilder.append("With Data:\n");
				for (Object datum : data) {
					stringBuilder.append(Objects.toString(datum, "null"));
					stringBuilder.append('\n');
				}
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
	}
}
