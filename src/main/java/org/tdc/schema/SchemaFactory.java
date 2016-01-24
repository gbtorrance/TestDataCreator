package org.tdc.schema;

import org.tdc.config.schema.SchemaConfig;

public interface SchemaFactory {
	Schema getSchema(SchemaConfig config);
}
