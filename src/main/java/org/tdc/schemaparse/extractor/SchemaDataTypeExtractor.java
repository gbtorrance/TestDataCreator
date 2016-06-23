package org.tdc.schemaparse.extractor;

import org.apache.xerces.xs.XSTypeDefinition;
import org.tdc.modeldef.NodeDef;

/**
 * Interface defining functionality for extracting data type
 * information during schema processing.
 * 
 * @see DefaultDataTypeExtractor
 */
public interface SchemaDataTypeExtractor extends SchemaExtractor {
	void extractDataType(XSTypeDefinition xsTypeDef, NodeDef nodeDef);
}
