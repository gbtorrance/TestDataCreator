package org.tdc.config.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;

public class BookDefConfigFactoryImpl implements BookDefConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(BookDefConfigFactoryImpl.class);

	private Cache<BookDefConfig> cache = new CacheImpl<>();

	@Override
	public BookDefConfig getBookDefConfig(Addr addr) {
		BookDefConfig bookDefConfig = cache.get(addr);
		if (bookDefConfig == null) {
			bookDefConfig = new BookDefConfigImpl(addr);
			cache.put(addr, bookDefConfig);
		}
		else {
			log.debug("Found cached BoodDefConfig for: {}", addr);
		}
		return bookDefConfig;
	}
}
