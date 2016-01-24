package org.tdc.config.schema;

import org.tdc.util.Addr;

public interface SchemaConfigFactory {
	SchemaConfig getSchemaConfig(Addr addr);
}
