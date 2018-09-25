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

import java.io.IOException;
import java.io.OutputStream;
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
	
	@ToString
	public enum Level {
		ERROR ("[ERROR] ",  2),
		WARN  ("[WARN]  ",  1),
		INFO  ("[INFO]  ",  0),
		DEBUG ("[DEBUG] ", -1),
		TRACE ("[TRACE] ", -2),
		NONE  ("[*****] ", Integer.MIN_VALUE);

		private final String level;
		private final int priority;
		
		Level(String level, int priority) {
			this.level = level;
			this.priority = priority;
		}
		
		private String level() {
			return level;
		}
		
		private int priority() {
			return priority;
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
	private static Level globalLevel = Level.INFO;
	
	private final Level logLevel;
	private final Class<?> targetClass;
	private final Set<OutputStream> appenders;

	private Logger(Class<?> targetClass) {
		logLevel = Level.NONE;
		appenders = new HashSet<>(APPENDERS);
		this.targetClass = targetClass;
	}
	
	private Logger(Class<?> targetClass, Level level) {
		logLevel = level;
		appenders = new HashSet<>(APPENDERS);
		this.targetClass = targetClass;
	}

	public static Logger create(Class<?> targetClass) {
		return new Logger(targetClass);
	}
	public static Logger create(Class<?> targetClass, Level level) {
		return new Logger(targetClass, level);
	}

	/**
	 * Sets the global logger level; this method should be used with caution and
	 * called only from a program entry point (main method or init)
	 * @param level the new default logger level for new loggers
	 */
	public static void setGlobalLevel(Level level) {
		globalLevel = level;
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
		sb.append(level.level());
		sb.append(targetClass.getSimpleName());
		sb.append(": ");
		sb.append(Templates.compile(template, data));
		return sb.toString();
	}

	private void print(Level level, String template, Object... data) {
		if (logLevel == Level.NONE 
				&& level.priority() < globalLevel.priority()
				|| level.priority() < logLevel.priority()) {
			return;
		}
		String string = generate(level, template, data);
		for (OutputStream appender : APPENDERS) {
			try {
				appender.write(string.getBytes("UTF-8"));
			} catch (IOException e) {
				System.out.println("Failed to write message to output" +
						" stream for appender " + appender + ' ' + e);
			}
		}
	}
}
