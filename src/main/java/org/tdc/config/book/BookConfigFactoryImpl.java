package org.tdc.config.book;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;
import org.tdc.util.ConfigFinder;

/**
 * A {@link BookConfigFactory} implementation.
 * 
 * <p>Caches configuration objects to ensure only one instance per {@linkplain org.tdc.util.Addr address}.
 */
public class BookConfigFactoryImpl implements BookConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(BookConfigFactoryImpl.class);

	private final Cache<BookConfig> cache = new CacheImpl<>();
	private final Path booksConfigRoot;
	private final ModelConfigFactory modelConfigFactory;
	private final TaskConfigFactory taskConfigFactory;
	
	public BookConfigFactoryImpl(Path booksConfigRoot, 
			ModelConfigFactory modelConfigFactory,
			TaskConfigFactory taskConfigFactory) {
		
		this.booksConfigRoot = booksConfigRoot;
		this.modelConfigFactory = modelConfigFactory;
		this.taskConfigFactory = taskConfigFactory;
	}

	@Override
	public synchronized BookConfig getBookConfig(Addr addr) {
		BookConfig bookConfig = cache.get(addr);
		if (bookConfig == null) {
			bookConfig = new BookConfigImpl.Builder(
					booksConfigRoot, addr, modelConfigFactory, taskConfigFactory).build();
			cache.put(addr, bookConfig);
		}
		else {
			log.debug("Found cached BookConfig for: {}", addr);
		}
		return bookConfig;
	}

	@Override
	public List<BookConfig> getAllBookConfigs() {
		List<Addr> allConfigAddrs = ConfigFinder.findAllConfigsContainingConfigFile(
				booksConfigRoot, 
				BookConfigImpl.CONFIG_FILE);
		List<BookConfig> bookConfigs = new ArrayList<>();
		for (Addr addr : allConfigAddrs) {
			BookConfig bookConfig = getBookConfig(addr);
			bookConfigs.add(bookConfig);
		}
		return bookConfigs;
	}
}
