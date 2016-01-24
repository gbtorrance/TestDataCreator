package org.tdc.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.schema.SchemaConfig;

public class SchemaImpl implements Schema {
	
	private static final Logger log = LoggerFactory.getLogger(SchemaImpl.class);
	
	private SchemaConfig config;
	
	public SchemaImpl(SchemaConfig config) {
		this.config = config;
		log.debug("Creating SchemaImpl: {}", config.getAddr());
	}

}
