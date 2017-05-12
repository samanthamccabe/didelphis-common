package org.didelphis.common.language.machines.interfaces;

import org.didelphis.common.structures.tuples.Tuple;

/**
 * Created by samantha on 2/23/17.
 */
public interface MachineMatcher<T> {

	/**
	 * @param target the input to the state machine
	 * @param index
	 *
	 * @return
	 */
	int match(T target, T arc, int index);
}