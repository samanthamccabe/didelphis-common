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

import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
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
@ToString
public final class Logger {

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

	/* TODO: load a static configuration:
	 * this can specify class and package patterns with different log levels and
	 * patterns
	 * 
	 * these configurations can be referenced whenever a logger is created via 
	 * the #create() method
	 */
	
	// Is a static collection a good idea? Not generally.
	private static final Set<OutputStream> APPENDERS = new HashSet<>();
	private static final Level DEFAULT_LEVEL = Level.INFO;
	
	private final Level logLevel;
	private final Class<?> targetClass;
	private final Set<OutputStream> appenders;

	private Logger(Class<?> targetClass) {
		logLevel = DEFAULT_LEVEL;
		appenders = new HashSet<>(APPENDERS);
		this.targetClass = targetClass;
	}

	public static Logger create(Class<?> targetClass) {
		return new Logger(targetClass);
	}

	public static void addAppender(OutputStream stream) {
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
		if (split.length != 1) {
			while (i < split.length && i < data.length) {
				sb.append(split[i]);
				sb.append(getString(data[i]));
				i++;
			}
		} else {
			sb.append(template);
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
			for (OutputStream appender: APPENDERS) {
				try {
					appender.write(string.getBytes(Charset.forName("UTF-8")));
				} catch (IOException e) {
					System.err.println("Failed to write message to output" +
							" stream for appender " + appender + ' ' + e);
				}
			}
		}
	}
	
	private static String getString(Object datum) {
		return datum == null ? "NULL" : datum.toString();
	}
}
