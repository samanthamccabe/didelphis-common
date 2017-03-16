package org.didelphis.common.language.machines.simple;

import org.didelphis.common.language.machines.interfaces.MachineMatcher;
import org.didelphis.common.structures.tuples.Tuple;

import java.util.Collection;

/**
 * Created by samantha on 3/3/17.
 */
public class SimpleMatcher implements MachineMatcher<String> {

	@Override
	public int match(String target, String arc, int index) {
		return 0;
	}
}
