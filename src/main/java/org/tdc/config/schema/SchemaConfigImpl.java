package org.tdc.config.schema;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.util.Addr;

public class SchemaConfigImpl implements SchemaConfig {
	
	private static final Logger log = LoggerFactory.getLogger(SchemaConfigImpl.class);
	private static final String CONFIG_FILE = "TDCModelConfig.xml";

	private Path schemasRoot;
	private Addr addr;
	private Path schemaRoot;
	private Path schemaConfigFile;
	
	public SchemaConfigImpl(Path schemasRoot, Addr addr) {
		this.schemasRoot = schemasRoot;
		this.addr = addr;
		this.schemaRoot = schemasRoot.resolve(addr.getPath());
		this.schemaConfigFile = schemaRoot.resolve(CONFIG_FILE);
		validateDirectories();
		log.debug("Creating SchemaConfigImpl: {}", addr);
	}
	
	@Override
	public Path getSchemasRoot() {
		return schemasRoot;
	}
	
	@Override
	public Addr getAddr() {
		return addr; 
	}

	@Override
	public Path getSchemaRoot() {
		return schemaRoot;
	}

	private void validateDirectories() {
		if (!Files.isDirectory(schemaRoot)) {
			throw new IllegalStateException("Schema dir does not exist: " + schemaRoot.toString());
		}
	}

	private void loadConfig() {
		XMLConfigWrapper config = new XMLConfigWrapper(schemaConfigFile.toFile());
		loadConfigItems(config);
	}

	private void loadConfigItems(XMLConfigWrapper config) {
		// nothing needed yet
	}
}
