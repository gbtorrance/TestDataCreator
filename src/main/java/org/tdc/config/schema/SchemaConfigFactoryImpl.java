package org.tdc.config.schema;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;

public class SchemaConfigFactoryImpl implements SchemaConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(SchemaConfigFactoryImpl.class);

	private Cache<SchemaConfig> cache = new CacheImpl<>();
	private Path schemasRoot;
	
	public SchemaConfigFactoryImpl(Path schemasRoot) {
		this.schemasRoot = schemasRoot;
	}

	@Override
	public SchemaConfig getSchemaConfig(Addr addr) {
		SchemaConfig schemaConfig = cache.get(addr);
		if (schemaConfig == null) {
			schemaConfig = new SchemaConfigImpl(schemasRoot, addr);
			cache.put(addr, schemaConfig);
		}
		else {
			log.debug("Found cached SchemaConfig for: {}", addr);
		}
		return schemaConfig;
	}
}
