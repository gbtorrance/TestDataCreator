package org.tdc.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheImpl<T> implements Cache<T> {
	
	private static final Logger log = LoggerFactory.getLogger(CacheImpl.class);
	
	private Map<Addr, T> cache = new HashMap<>();

	@Override
	public T get(Addr addr) {
		return cache.get(addr);
	}
	
	@Override
	public void put(Addr addr, T value) {
		cache.put(addr, value);
	}
}
