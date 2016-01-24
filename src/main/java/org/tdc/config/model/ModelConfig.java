package org.tdc.config.model;

import java.nio.file.Path;

import org.tdc.config.schema.SchemaConfig;
import org.tdc.util.Addr;

public interface ModelConfig {
	SchemaConfig getSchemaConfig();

	Addr getAddr();
	Path getModelRoot();
	Path getModelConfigRoot();
	
	String getSchemaRootFile();
	Path getSchemaRootFileFullPath();
	String getSchemaRootElementName();
	String getSchemaRootElementNamespace();
	boolean isFailOnParserWarning();
	boolean isFailOnParserNonFatalError();

	int getDefaultOccurrenceDepth();
	int getMPathOccurrenceDepth(String mpath);
}
