package org.tdc.config.book;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.util.Addr;

/**
 * A {@link BookConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class BookConfigImpl implements BookConfig {
	
	private static final Logger log = LoggerFactory.getLogger(BookConfigImpl.class);

	private final Path booksConfigRoot;
	private final Addr addr;
	private final Path bookConfigRoot;
	private final Map<String, DocTypeConfig> docTypeConfigs;
	private final Map<String, PageConfig> pageConfigs;
	
	private BookConfigImpl(BookConfigBuilder builder) {
		this.booksConfigRoot = builder.booksConfigRoot;
		this.addr = builder.addr;
		this.bookConfigRoot = builder.bookConfigRoot;
		this.docTypeConfigs = Collections.unmodifiableMap(builder.docTypeConfigs); // unmodifiable
		this.pageConfigs = Collections.unmodifiableMap(builder.pageConfigs); // unmodifiable
	}
	
	@Override
	public Path getBooksConfigRoot() {
		return booksConfigRoot;
	}

	@Override
	public Addr getAddr() {
		return addr;
	}

	@Override
	public Path getBookConfigRoot() {
		return bookConfigRoot;
	}
	
	@Override
	public Map<String, DocTypeConfig> getDocTypeConfigs() {
		return docTypeConfigs;
	}

	@Override
	public Map<String, PageConfig> getPageConfigs() {
		return pageConfigs;
	}

	public static class BookConfigBuilder {
		private static final String CONFIG_FILE = "TDCBookConfig.xml";
		
		private final XMLConfigWrapper config;
		private final Path booksConfigRoot;
		private final Addr addr;
		private final Path bookConfigRoot;
		
		private Map<String, DocTypeConfig> docTypeConfigs;
		private Map<String, PageConfig> pageConfigs;
		
		public BookConfigBuilder(Path booksConfigRoot, Addr addr) {
			log.info("Creating BookConfig: {}", addr);
			this.booksConfigRoot = booksConfigRoot;
			this.addr = addr;
			this.bookConfigRoot = booksConfigRoot.resolve(addr.getPath());
			if (!Files.isDirectory(bookConfigRoot)) {
				throw new IllegalStateException("BookConfig root dir does not exist: " + bookConfigRoot.toString());
			}
			Path bookConfigFile = bookConfigRoot.resolve(CONFIG_FILE);
			this.config = new XMLConfigWrapper(bookConfigFile);
		}

		public BookConfig build() {
			docTypeConfigs = new DocTypeConfigImpl.DocTypeConfigBuilder(config).buildAll();
			pageConfigs = new PageConfigImpl.PageConfigBuilder(config, docTypeConfigs).buildAll();
			return new BookConfigImpl(this);
		}
	}
}
