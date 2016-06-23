package org.tdc.schemaparse.extractor;

import java.util.List;

import org.tdc.config.XMLConfigWrapper;

/**
 * Interface defining factory for creating SchemaExtractor instances based
 * on information extracted from an XML config file.
 */
public interface SchemaExtractorFactory {
	SchemaExtractor createSchemaExtractor(XMLConfigWrapper config, String extractorKey);
	List<SchemaExtractor> createSchemaExtractors(XMLConfigWrapper config, String extractorsKey);
}
