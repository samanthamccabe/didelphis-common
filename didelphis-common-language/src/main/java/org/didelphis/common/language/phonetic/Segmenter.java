package org.didelphis.common.language.phonetic;

import java.util.List;

/**
 * Created by samantha on 1/22/17.
 */
public interface Segmenter {

	/**
	 * Splits a string into components using reserved symbols
	 * @param string string to be segmented
	 * @return a list of strings
	 */
	List<String> split(String string);

	/**
	 * Splits a string into components using reserved symbols
	 * @param string string to be segmented
	 * @param special reserved characters to be treated as unitary
	 * @return a list of strings
	 */
	List<String> split(String string, Iterable<String> special);
}
