package org.didelphis.common.structures.contracts;

/**
 * Indicates that a data structure delegates some functionality to an inner
 * collection object and guarantees the structure is available through the API.
 * @param <T> the type of the delegate object.
 */
public interface Delegating<T> {

	/**
	 * Provides access to the delegate used by the implementing class
	 * @return the delegate object; this must not return null.
	 */
	T getDelegate();
}
