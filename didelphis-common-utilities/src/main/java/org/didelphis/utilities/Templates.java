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

package org.didelphis.utilities;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Utility Class {@code Templates}
 *
 * This class provides a fluent API for building messages with attached data for
 * exception handling and logging purposes. It uses curly-brace-style {@code {}}
 * templates to easily insert instance data into a message template. It also
 * uses varargs methods to more cleanly allow concatenation of strings over
 * multiple lines, <i>e.g.</i>
 *
 * {@code
 *  Templates.create()
 * 	    .add("Cannot create a table with",
 * 	        "negative dimensions! The",
 * 	        "dimension provided was {}")
 * 	    .with(dimension);
 * }
 *
 * rather than
 *
 * {@code
 *  Templates.create()
 * 	    .add("Cannot create a table with"
 *          + "negative dimensions! The"
 * 	        + "dimension provided was {}")
 * 	    .with(dimension);
 * }
 *
 * The former of these is slightly more concise and works a bit better with
 * the Didelphis code standards and formatter.
 *
 * The only non-factory method exposed by this utility class is
 * {@link #compile(String, Object...)}
 * which can be used for basic templating; a single message, with provided data.
 * This is used internally by the builder, but is also used by {@link Logger} to
 * create its messages, and uses the same pattern as any of its log commands.
 *
 * see also
 * {@link Logger#info(String, Object...)},
 * {@link Logger#warn(String, Object...)},
 * {@link Logger#error(String, Object...)},
 * <i>etc.</i>
 *
 */
@UtilityClass
public class Templates {

	/**
	 * Creates a new, empty template builder
	 * @return a new, empty template builder; will not be null
	 */
	@NonNull
	public static Builder create() {
		return new Builder();
	}

	/**
	 * Template builder class
	 */
	@SuppressWarnings ("PublicInnerClass")
	public static final class Builder {
		private final Deque<String> messages;
		private final List<Object> data;

		private Builder() {
			messages = new LinkedList<>();
			data = new ArrayList<>();
		}

		public Builder add(String string, String... strings) {
			if (strings.length > 0) {
				Collection<String> list = new ArrayList<>();
				list.add(string);
				list.addAll(Arrays.asList(strings));
				String message = String.join(" ", list);
				messages.add(message);
			} else {
				messages.add(string);
			}
			return this;
		}

		@NonNull
		public Builder with(Object... objects) {
			String message = compile(messages.removeLast(), objects);
			messages.add(message);
			return this;
		}

		@NonNull
		public Builder data(Object object, Object... objects) {
			data.add(object);
			Collections.addAll(data, objects);
			return this;
		}

		@NonNull
		public String build() {
			StringBuilder sb = new StringBuilder();
			String baseMessage = messages.isEmpty()
					? "No message provided."
					: String.join(" ", messages);
			sb.append(baseMessage);
			if (!data.isEmpty()) {
				sb.append("\nWith Data:\n");
				for (Object datum : data) {
					String string = Objects.toString(datum, "null");
					sb.append('\t');
					sb.append(string.replace("\n","\n\t"));
					sb.append('\n');
				}
				sb.append('\n');
			}
			return sb.toString();
		}
	}

	@NonNull
	public String compile(String template, Object... data) {
		StringBuilder sb = new StringBuilder();

		if (data.length == 0) {
			sb.append(template);
			return sb.toString();
		}

		int i = 0;
		int index = template.indexOf("{}");
		if (index < 0) {
			sb.append(template);
		} else {
			int lastIndex = 0;
			while (index > 0) {
				String head = template.substring(lastIndex, index);
				sb.append(head);
				sb.append(data[i]);
				i++;
				lastIndex = index + 2;
				index = template.indexOf("{}", index + 2);
			}
		}

		while (i < data.length) {
			Object datum = data[i];
			sb.append(getString(datum));
			i++;
		}
		return sb.toString();
	}

	private static String getString(Object datum) {
		return datum == null ? "NULL" : datum.toString();
	}
}
