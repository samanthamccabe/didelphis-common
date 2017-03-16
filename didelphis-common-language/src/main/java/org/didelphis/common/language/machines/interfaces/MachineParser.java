package org.didelphis.common.language.machines.interfaces;

import org.didelphis.common.language.machines.Expression;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by samantha on 2/23/17.
 */
public interface MachineParser<T> {

	/**
	 * Transform an expression string into a corresponding state machine
	 * @param expression
	 * @return
	 */
	T transform(String expression);

	/**
	 * Parse an expression to a list of sub-expressions
	 * @param expression
	 * @return
	 */
	List<Expression> parseExpression(String expression);

	/**
	 * Provides a uniform value for epsilon transitions 
	 * @return a uniform value for epsilon transitions 
	 */
	T epsilon();

	/**
	 * Provides a {@code collection} of supported special symbols and their
	 * corresponding literal values
	 * @return a {@code collection} of supported special symbols and their
	 * corresponding literal values
	 */
	Map<String, Collection<T>> getSpecials();

	/**
	 * Provides a uniform value for "dot" transitions, which accept any value,
	 * corresponding to "." in traditional regular expression languages
	 * @return a uniform value for "dot" transitions, which accept any value
	 */
	T getDot();

	/**
	 * Determines the length of the provided element, where applicable. In some
	 * implementations, this may simply be 1 in all cases.
	 * @param t the data element whose length is to be determined
	 * @return the length of the provided element
	 */
	int lengthOf(T t);
	
}
