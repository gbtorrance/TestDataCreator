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
	
	private static final Logger log = LoggerFactory.getLogger(SchemaConfigImpl.class);
	private static final String CONFIG_FILE = "TDCSchemaConfig.xml";

	private Path schemasConfigRoot;
	private Addr addr;
	private Path schemaConfigRoot;
	private Path schemaConfigFile;
	private Path schemaFilesRoot;
	
	public SchemaConfigImpl(Path schemasConfigRoot, Addr addr) {
		log.info("Creating SchemaConfigImpl: {}", addr);
		this.schemasConfigRoot = schemasConfigRoot;
		this.addr = addr;
		this.schemaConfigRoot = schemasConfigRoot.resolve(addr.getPath());
		if (!Files.isDirectory(schemaConfigRoot)) {
			throw new IllegalStateException("Schema config root dir does not exist: " + schemaConfigRoot.toString());
		}
		this.schemaConfigFile = schemaConfigRoot.resolve(CONFIG_FILE);
		loadConfig();
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

	private void loadConfig() {
		XMLConfigWrapper config = new XMLConfigWrapper(schemaConfigFile.toFile());
		loadConfigItems(config);
	}

	private void loadConfigItems(XMLConfigWrapper config) {
		schemaFilesRoot = this.schemaConfigRoot.resolve(config.getString("SchemaFilesRoot", null, true));
		if (!Files.isDirectory(schemaFilesRoot)) {
			throw new IllegalStateException("Schema files root dir does not exist: " + schemaFilesRoot.toString());
		}
	}
}
