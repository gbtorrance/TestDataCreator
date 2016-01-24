package org.tdc.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.CacheImpl;

public class SchemaFactoryImpl implements SchemaFactory {

	private static final Logger log = LoggerFactory.getLogger(SchemaFactoryImpl.class);
	
	private Cache<Schema> cache = new CacheImpl<>();
	
	@Override
	public Schema getSchema(SchemaConfig config) {
		Addr addr = config.getAddr();
		Schema schema = cache.get(addr);
		if (schema == null) {
			schema = new SchemaImpl(config);
			cache.put(addr, schema);
		}
		else {
			log.debug("Found cached Schema for: {}", addr);
		}
		return schema;
	}
}
