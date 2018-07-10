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

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Class {@code Logger}
 *
 * A fairly normal logger class; creating our own logger is not, in general, a 
 * very good idea. However, there are a couple of reasons why it seems warranted
 *      1) It reduces our external dependencies
 *      2) It reduces complexity for transpilation
 *      3) It allows us to more easily log data into a UI
 *
 * @author Samantha Fiona McCabe
 * @date 6/7/18
 */
@SuppressWarnings ("UseOfSystemOutOrSystemErr")
public final class Logger {

	private static final PrintStream SYSTEM = System.out;
	
	// Is a static collection a good idea? Not generally.
	private static final Set<PrintStream> APPENDERS = new HashSet<>();
	
	private static Level logLevel;
	
	private final Class<?> targetClass;

	private Logger(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	public static Logger create(Class<?> targetClass) {
		return new Logger(targetClass);
	}

	public static void addAppender(PrintStream stream) {
		APPENDERS.add(stream);
	}

	public void trace(String template, Object... data) {
		print(Level.TRACE, template, data);
	}

	public void debug(String template, Object... data) {
		print(Level.DEBUG, template, data);
	}

	public void info(String template, Object... data) {
		print(Level.INFO, template, data);
	}

	public void warn(String template, Object... data) {
		print(Level.WARN, template, data);
	}

	public void error(String template, Object... data) {
		print(Level.ERROR, template, data);
	}

	private String generate(Level level, String template, Object... data) {

		StringBuilder sb = new StringBuilder();
		sb.append(level);

		sb.append(targetClass.getSimpleName());
		sb.append(": ");

		int i = 0;
		String[] split = template.split("\\{}");
		while (i<split.length && i < data.length) {
			sb.append(split[i]);
			sb.append(getString(data[i]));
			i++;
		}

		while (i < data.length) {
			Object datum = data[i];
			sb.append(getString(datum));
			i++;
		}

		return sb.toString();
	}

	private void print(Level level, String template, Object... data) {
		
		if (level.ordinal() <= logLevel.ordinal()) {
			String string = generate(level, template, data);
			for (PrintStream appender: APPENDERS) {
				appender.println(string);
			}
		}
	}

	private static String getString(Object datum) {
		return datum == null ? "NULL" : datum.toString();
	}
	
	private enum Level {
		ERROR ("[ERROR] "),
		WARN  ("[INFO]  "),
		INFO  ("[WARN]  "),
		DEBUG ("[DEBUG] "),
		TRACE ("[TRACE] ");

		private final String level;
		
		Level(String level) {
			this.level = level;
		}
		
		@Override
		public @NotNull String toString() {
			return level;
		}
	}
}
