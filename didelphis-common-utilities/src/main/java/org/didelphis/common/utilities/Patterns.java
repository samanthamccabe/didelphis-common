package org.didelphis.common.utilities;

import java.util.regex.Pattern;

/**
 * Created by samantha on 2/24/17.
 */
public final class Patterns {
	
	private Patterns() {}
	
	public static Pattern template(String head, String... vars) {
		String regex = head;
		for (int i = 0; i < vars.length;i++) {
			regex = regex.replace("$"+(i+1), vars[i]);
		}
		return Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
	}
	
	public static Pattern compile(String head, String... tail) {
		String regex = concat(head, tail);
		return Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
	}

	private static String concat(String head, String... tail) {
		StringBuilder sb = new StringBuilder(0x100);
		sb.append(head);
		for (String string : tail) {
			sb.append(string);
		}
		return sb.toString();
	}
}
