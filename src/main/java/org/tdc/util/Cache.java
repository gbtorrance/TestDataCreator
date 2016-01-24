package org.tdc.util;

public interface Cache<T> {
	T get(Addr addr);
	void put(Addr addr, T value);
}
