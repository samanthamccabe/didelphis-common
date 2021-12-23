package org.didelphis.utilities;


public class Strings {

	private Strings() {}

	public static String joinNL(String... strings) {
		return String.join("\n", strings);
	}

	public static String padLeft(String string, int width) {
		StringBuilder stringBuilder = new StringBuilder();
		int spaces = width - getPrintableLength(string);
		stringBuilder.append(" ".repeat(Math.max(0, spaces)));
		stringBuilder.append(string);
		return stringBuilder.toString();
	}

	public static String padRight(String string, int width) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(string);
		int spaces = width - getPrintableLength(string);
		stringBuilder.append(" ".repeat(Math.max(0, spaces)));
		return stringBuilder.toString();
	}

	public static int getPrintableLength(String string) {
		int visible = 0;
		for (char c : string.toCharArray()) {
			if (Character.getType(c) != Character.NON_SPACING_MARK) {
				visible++;
			}
		}
		return visible;
	}
}
