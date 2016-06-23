package org.tdc.config.book;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;

/**
 * A {@link BookConfigFactory} implementation.
 * 
 * <p>Caches configuration objects to ensure only one instance per {@linkplain org.tdc.util.Addr address}.
 */
public class BookConfigFactoryImpl implements BookConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(BookConfigFactoryImpl.class);

	private Cache<BookConfig> cache = new CacheImpl<>();
	private final Path booksConfigRoot;
	private final ModelConfigFactory modelConfigFactory;
	
	public BookConfigFactoryImpl(Path booksConfigRoot, ModelConfigFactory modelConfigFactory) {
		this.booksConfigRoot = booksConfigRoot;
		this.modelConfigFactory = modelConfigFactory;
	}

	@Override
	public synchronized BookConfig getBookConfig(Addr addr) {
		BookConfig bookConfig = cache.get(addr);
		if (bookConfig == null) {
			bookConfig = new BookConfigImpl.BookConfigBuilder(booksConfigRoot, addr, modelConfigFactory).build();
			cache.put(addr, bookConfig);
		}
		else {
			log.debug("Found cached BookConfig for: {}", addr);
		}
		return bookConfig;
	}
}
