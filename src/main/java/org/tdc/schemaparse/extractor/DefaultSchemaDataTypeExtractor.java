package org.tdc.schemaparse.extractor;

import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.modeldef.NodeDef;

/**
 * Implementation of {@link SchemaDataTypeExtractor}.
 * 
 * <p>This implementation supports the reading of basic data type information from schemas
 * and the subsequent setting of these data type values to variables associated with the 
 * corresponding {@link NodeDef}s.
 */
public class DefaultSchemaDataTypeExtractor implements SchemaDataTypeExtractor {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultSchemaDataTypeExtractor.class);
	
	private final String variableName;
	
	public DefaultSchemaDataTypeExtractor(String variableName) {
		this.variableName = variableName;
	}
	
	@Override
	public void extractDataType(XSTypeDefinition xsTypeDef, NodeDef nodeDef) {
		String dataType = getDataType(xsTypeDef);
		nodeDef.setVariable(variableName, dataType);
	}
	
	private String getDataType(XSTypeDefinition xsTypeDef) {
		// TODO consider alternative ways to extract data type?
		String type = xsTypeDef.getName();
		if (type == null) {
			type = getDataType(xsTypeDef.getBaseType());
		}
		if (xsTypeDef instanceof XSSimpleTypeDefinition) {
			XSSimpleTypeDefinition xsSimpleTypeDef = (XSSimpleTypeDefinition)xsTypeDef;
			XSFacet xsMaxLength = (XSFacet)xsSimpleTypeDef.getFacet(XSSimpleTypeDefinition.FACET_MAXLENGTH);
			if (xsMaxLength != null) {
				type = type + " {" + xsMaxLength.getIntFacetValue() + "}";
			}
		}
		return type.equals("anyType") ? "" : type;
	}

	/**
	 * Static builder method to create an instance of this class based on XML configuration settings.
	 * 
	 * @param config XMLConfigWrapper class.
	 * @param key Configuration key pointing to location of information to read.
	 * @return Instance of this class.
	 */
	public static DefaultSchemaDataTypeExtractor build(XMLConfigWrapper config, String key) {
		String variableName = config.getString(key + ".VariableName", null, true);
		return new DefaultSchemaDataTypeExtractor(variableName);
	}
}
