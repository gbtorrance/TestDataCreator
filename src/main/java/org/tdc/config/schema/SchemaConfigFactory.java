package org.tdc.config.schema;

import java.nio.file.Path;
import java.util.List;

import org.tdc.util.Addr;

/**
 * Factory for creating {@link SchemaConfig} instances.
 */
public interface SchemaConfigFactory {
	Path getSchemasConfigRoot();
	SchemaConfig getSchemaConfig(Addr addr);
	List<SchemaConfig> getAllSchemaConfigs();
}
