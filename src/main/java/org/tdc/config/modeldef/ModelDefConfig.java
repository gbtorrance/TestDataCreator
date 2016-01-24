package org.tdc.config.modeldef;

import java.nio.file.Path;

import org.tdc.config.schema.SchemaConfig;
import org.tdc.util.Addr;

// TODO possibly change package to be model.def rather than modeldef (for consistency)

public interface ModelDefConfig {
	SchemaConfig getSchemaConfig();

	Addr getAddr();
	Path getModelDefRoot();
	Path getModelDefConfigRoot();
	
	String getSchemaRootFile();
	Path getSchemaRootFileFullPath();
	String getSchemaRootElementName();
	String getSchemaRootElementNamespace();
	boolean isFailOnParserWarning();
	boolean isFailOnParserNonFatalError();

}
