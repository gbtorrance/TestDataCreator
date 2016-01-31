package org.tdc.config.schema;

import java.nio.file.Path;

import org.tdc.util.Addr;

public interface SchemaConfig {
	Path getSchemasConfigRoot();
	Addr getAddr();
	Path getSchemaConfigRoot();
	Path getSchemaFilesRoot();
}
