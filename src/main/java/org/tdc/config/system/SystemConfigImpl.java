package org.tdc.config.system;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;

/**
 * A {@link SystemConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class SystemConfigImpl implements SystemConfig {
	public static final String CONFIG_FILE = "TDCSystemConfig.xml";
	
	private static final Logger log = LoggerFactory.getLogger(SystemConfigImpl.class);

	private final Path systemConfigRoot;
	private final Path schemasConfigRoot;
	private final Path booksConfigRoot;
	
	private SystemConfigImpl(Builder builder) {
		this.systemConfigRoot = builder.systemConfigRoot;
		this.schemasConfigRoot = builder.schemasConfigRoot;
		this.booksConfigRoot = builder.booksConfigRoot;
	}
	
	@Override
	public Path getSystemConfigRoot() {
		return systemConfigRoot;
	}

	@Override
	public Path getSchemasConfigRoot() {
		return schemasConfigRoot;
	}

	@Override
	public Path getBooksConfigRoot() {
		return booksConfigRoot;
	}

	public static class Builder {
		private final XMLConfigWrapper config;
		private final Path systemConfigRoot;
		
		private Path schemasConfigRoot;
		private Path booksConfigRoot;
		
		public Builder(Path systemConfigRoot) {
			log.info("Creating SystemConfig: {}", systemConfigRoot);
			this.systemConfigRoot = systemConfigRoot;
			if (!Files.isDirectory(systemConfigRoot)) {
				throw new IllegalStateException("SystemConfigRoot dir does not exist: " + systemConfigRoot.toString());
			}
			Path systemConfigFile = systemConfigRoot.resolve(CONFIG_FILE);
			this.config = new XMLConfigWrapper(systemConfigFile);
		}

		public SystemConfig build() {
			schemasConfigRoot = systemConfigRoot.resolve(config.getString("SchemasConfigRoot", null, true));
			if (!Files.isDirectory(schemasConfigRoot)) {
				throw new IllegalStateException("SchemasConfigRoot dir does not exist: " + schemasConfigRoot.toString());
			}
			booksConfigRoot = systemConfigRoot.resolve(config.getString("BooksConfigRoot", null, true));
			if (!Files.isDirectory(booksConfigRoot)) {
				throw new IllegalStateException("BooksConfigRoot dir does not exist: " + booksConfigRoot.toString());
			}
			return new SystemConfigImpl(this);
		}
	}
}
