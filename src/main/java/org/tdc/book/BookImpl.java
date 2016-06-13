package org.tdc.book;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.BookConfig;
import org.tdc.modelinst.ModelInstFactory;

/**
 * A {@link Book} implementation.
 */
public class BookImpl implements Book {

	private static final Logger log = LoggerFactory.getLogger(BookImpl.class);
	
	private final BookConfig config;
	private final Map<String, Page> pages;
	
	private BookImpl(BookBuilder builder) {
		this.config = builder.config;
		this.pages = Collections.unmodifiableMap(builder.pages); // unmodifiable
	}
	
	@Override
	public BookConfig getConfig() {
		return config;
	}
	
	@Override
	public Map<String, Page> getPages() {
		return pages;
	}
	
	public static class BookBuilder {
		private final BookConfig config;
		private final ModelInstFactory modelInstFactory;
		
		private Map<String, Page> pages;
		
		public BookBuilder(BookConfig config, ModelInstFactory modelInstFactory) {
			log.info("Creating Book: {}", config.getAddr());
			this.config = config;
			this.modelInstFactory = modelInstFactory;
		}
		
		public Book build() {
			pages = new PageImpl.PageBuilder(config.getPageConfigs(), modelInstFactory).buildAll();
			return new BookImpl(this);
		}
	}
}
