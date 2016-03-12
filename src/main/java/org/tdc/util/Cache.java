package org.tdc.util;

/**
 * Defines support for {@linkplain Addr address}-based object caching. 
 * 
 * <p>Used by Factories to share object instances.
 */
public interface Cache<T> {
	T get(Addr addr);
	void put(Addr addr, T value);
}
