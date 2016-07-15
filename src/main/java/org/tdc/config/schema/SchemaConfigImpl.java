package org.tdc.config.schema;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.util.Addr;

/**
 * A {@link SchemaConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class SchemaConfigImpl implements SchemaConfig {
	
	public static final String CONFIG_FILE = "TDCSchemaConfig.xml";
	
	private static final Logger log = LoggerFactory.getLogger(SchemaConfigImpl.class);

	private final Path schemasConfigRoot;
	private final Addr addr;
	private final Path schemaConfigRoot;
	private final Path schemaFilesRoot;
	
	private SchemaConfigImpl(Builder builder) {
		this.schemasConfigRoot = builder.schemasConfigRoot;
		this.addr = builder.addr;
		this.schemaConfigRoot = builder.schemaConfigRoot;
		this.schemaFilesRoot = builder.schemaFilesRoot;
	}
	
	@Override
	public Path getSchemasConfigRoot() {
		return schemasConfigRoot;
	}
	
	@Override
	public Addr getAddr() {
		return addr; 
	}

	@Override
	public Path getSchemaConfigRoot() {
		return schemaConfigRoot;
	}

	@Override
	public Path getSchemaFilesRoot() {
		return schemaFilesRoot;
	}

	public static class Builder {
		private final XMLConfigWrapper config;
		private final Path schemasConfigRoot;
		private final Addr addr;
		private final Path schemaConfigRoot;
		
		private Path schemaFilesRoot;
		
		public Builder(Path schemasConfigRoot, Addr addr) {
			log.info("Creating SchemaConfig: {}", addr);
			this.schemasConfigRoot = schemasConfigRoot;
			this.addr = addr;
			this.schemaConfigRoot = schemasConfigRoot.resolve(addr.getPath());
			if (!Files.isDirectory(schemaConfigRoot)) {
				throw new IllegalStateException("SchemaConfig root dir does not exist: " + schemaConfigRoot.toString());
			}
			Path schemaConfigFile = schemaConfigRoot.resolve(CONFIG_FILE);
			this.config = new XMLConfigWrapper(schemaConfigFile);
		}

		public SchemaConfig build() {
			schemaFilesRoot = schemaConfigRoot.resolve(config.getString("SchemaFilesRoot", null, true));
			if (!Files.isDirectory(schemaFilesRoot)) {
				throw new IllegalStateException("Schema files root dir does not exist: " + schemaFilesRoot.toString());
			}
			return new SchemaConfigImpl(this);
		}
	}
}
