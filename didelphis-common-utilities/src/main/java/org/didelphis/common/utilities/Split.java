package org.didelphis.common.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Split (Utility) - a class for general string segmentation tools, bracket
 * matching, and related tasks
 * Created by samantha on 3/3/17.
 */
public final class Split {
	
	private Split(){}

	/**
	 * 
	 * @param string
	 * @param special
	 * @return
	 */
	public static List<String> splitToList(String string, Iterable<String> special) {
		List<String> strings = new ArrayList<>();
		for (int i = 0; i < string.length(); i++) {

			int index = parseParens(string, i);
			if (index >= 0) {
				strings.add(string.substring(i, index));
				i = index - 1;
			} else {
				String substring = string.substring(i);
				String matchedSpecial = "";
				if (special != null) {
					for (String s : special) {
						if (substring.startsWith(s)) {
							matchedSpecial = s;
							break;
						}
					}
				}

				if (!matchedSpecial.isEmpty()) {
					strings.add(matchedSpecial);
					i += matchedSpecial.length() - 1;
				} else {
					strings.add(string.substring(i, i+1));
				}
			}
		}
		return strings;
	}

	/**
	 * 
	 * @param string
	 * @param index
	 * @return
	 */
	public static int parseParens(CharSequence string, int index) {
		switch (string.charAt(index)) {
			case '[':
				return findClosingBracket(string, '[', ']', index);
			case '(':
				return findClosingBracket(string, '(', ')', index);
			case '{':
				return findClosingBracket(string, '{', '}', index);
			default:
				return -1;
		}
	}

	/**
	 * 
	 * @param string
	 * @param left
	 * @param right
	 * @param startIndex
	 * @return
	 */
	public static int findClosingBracket(CharSequence string, char left,
			char right, int startIndex) {
		int count = 1;
		int endIndex = startIndex;

		boolean matched = false;
		for (int i = startIndex + 1; i < string.length() && !matched; i++) {
			char ch = string.charAt(i);
			if (ch == right && count == 1) {
				matched = true;
				endIndex = i;
			} else if (ch == right) {
				count++;
			} else if (ch == left) {
				count--;
			}
		}
		return endIndex + 1;
	}
}
