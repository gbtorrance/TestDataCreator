package org.tdc.config.schema;

import org.tdc.util.Addr;

/**
 * Factory for creating {@link SchemaConfig} instances.
 */
public interface SchemaConfigFactory {
	SchemaConfig getSchemaConfig(Addr addr);
}
