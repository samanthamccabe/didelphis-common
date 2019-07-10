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
