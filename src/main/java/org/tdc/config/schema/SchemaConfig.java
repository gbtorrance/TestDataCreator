package org.tdc.config.schema;

import java.nio.file.Path;

import org.tdc.util.Addr;

public interface SchemaConfig {
	Path getSchemasRoot();
	Addr getAddr();
	Path getSchemaRoot();
	Path getSchemaConfigRoot();
}
