package org.tdc.config.model;

import java.nio.file.Path;
import java.util.List;

import org.tdc.config.schema.SchemaConfig;
import org.tdc.schemaparse.extractor.SchemaExtractor;
import org.tdc.util.Addr;

/**
 * Defines getters for configuration items applicable to Models 
 * (represented internally by {@link org.tdc.modeldef.ModelDef ModelDef} and {@link org.tdc.modelinst.ModelInst ModelInst}).
 */
public interface ModelConfig {
	SchemaConfig getSchemaConfig();
	Addr getAddr();
	Path getModelConfigRoot();
	String getSchemaRootFile();
	Path getSchemaRootFileFullPath();
	String getSchemaRootElementName();
	String getSchemaRootElementNamespace();
	boolean getFailOnParserWarning();
	boolean getFailOnParserNonFatalError();
	int getDefaultOccursCount();
	List<SchemaExtractor> getSchemaExtractors();
	ModelCustomizerConfig getModelCustomizerConfig();
	boolean hasModelCustomizerConfig();
}
