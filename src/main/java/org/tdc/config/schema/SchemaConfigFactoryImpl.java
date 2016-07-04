package org.tdc.config.schema;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;
import org.tdc.util.ConfigFinder;

/**
 * A {@link SchemaConfigFactory} implementation.
 * 
 * <p>Caches configuration objects to ensure only one instance per {@linkplain org.tdc.util.Addr address}.
 */
public class SchemaConfigFactoryImpl implements SchemaConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(SchemaConfigFactoryImpl.class);

	private final Cache<SchemaConfig> cache = new CacheImpl<>();
	private final Path schemasConfigRoot;
	
	public SchemaConfigFactoryImpl(Path schemasConfigRoot) {
		this.schemasConfigRoot = schemasConfigRoot;
	}
	
	public Path getSchemasConfigRoot() {
		return schemasConfigRoot;
	}

	@Override
	public synchronized SchemaConfig getSchemaConfig(Addr addr) {
		SchemaConfig schemaConfig = cache.get(addr);
		if (schemaConfig == null) {
			schemaConfig = new SchemaConfigImpl.SchemaConfigBuilder(schemasConfigRoot, addr).build();
			cache.put(addr, schemaConfig);
		}
		else {
			log.debug("Found cached SchemaConfig for: {}", addr);
		}
		return schemaConfig;
	}

	@Override
	public List<SchemaConfig> getAllSchemaConfigs() {
		List<Addr> allConfigAddrs = ConfigFinder.findAllConfigsContainingConfigFile(
				schemasConfigRoot, 
				SchemaConfigImpl.CONFIG_FILE);
		List<SchemaConfig> schemaConfigs = new ArrayList<>();
		for (Addr addr : allConfigAddrs) {
			SchemaConfig schemaConfig = getSchemaConfig(addr);
			schemaConfigs.add(schemaConfig);
		}
		return schemaConfigs;
	}
}
