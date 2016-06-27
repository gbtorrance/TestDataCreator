package org.tdc.schemaparse.extractor;

import org.apache.xerces.xs.StringList;
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
	private final boolean verbose;
	
	public DefaultSchemaDataTypeExtractor(String variableName, boolean verbose) {
		this.variableName = variableName;
		this.verbose = verbose;
	}
	
	@Override
	public void extractDataType(XSTypeDefinition xsTypeDef, NodeDef nodeDef) {
		String dataType = getDataType(xsTypeDef);
		nodeDef.setVariable(variableName, dataType);
	}
	
	private String getDataType(XSTypeDefinition xsTypeDef) {
		String type = xsTypeDef.getName();
		if (type == null) {
			type = getDataType(xsTypeDef.getBaseType());
		}
		if (xsTypeDef instanceof XSSimpleTypeDefinition) {
			XSSimpleTypeDefinition xsSimpleTypeDef = (XSSimpleTypeDefinition)xsTypeDef;
			if (verbose) {
				XSFacet facet;
				facet = (XSFacet)xsSimpleTypeDef.getFacet(XSSimpleTypeDefinition.FACET_MINLENGTH);
				String minLength = facet == null ? "" : "minLength:" + facet.getIntFacetValue() + ",";
				facet = (XSFacet)xsSimpleTypeDef.getFacet(XSSimpleTypeDefinition.FACET_MAXLENGTH);
				String maxLength = facet == null ? "" : "maxLength:" + facet.getIntFacetValue() + ",";
				facet = (XSFacet)xsSimpleTypeDef.getFacet(XSSimpleTypeDefinition.FACET_LENGTH);
				String length = facet == null ? "" : "length:" + facet.getIntFacetValue() + ",";
				facet = (XSFacet)xsSimpleTypeDef.getFacet(XSSimpleTypeDefinition.FACET_TOTALDIGITS);
				String totalDigits = facet == null ? "" : "totalDigits:" + facet.getIntFacetValue() + ",";
				facet = (XSFacet)xsSimpleTypeDef.getFacet(XSSimpleTypeDefinition.FACET_FRACTIONDIGITS);
				String fractionDigits = facet == null ? "" : "fractionDigits:" + facet.getIntFacetValue() + ",";
				String enumeration = getEnumerationInfo(xsSimpleTypeDef);
				String pattern = getPatternInfo(xsSimpleTypeDef);
				String join = minLength + maxLength + length + totalDigits + fractionDigits + enumeration + pattern;
				if (join.length() > 0) {
					type = type + " {" + join.substring(0, join.length()-1) + "}";
				}
			}
			else {
				XSFacet facet;
				facet = (XSFacet)xsSimpleTypeDef.getFacet(XSSimpleTypeDefinition.FACET_MAXLENGTH);
				String maxLength = facet == null ? "" : "" + facet.getIntFacetValue();
				facet = (XSFacet)xsSimpleTypeDef.getFacet(XSSimpleTypeDefinition.FACET_LENGTH);
				String length = facet == null ? "" : "" + facet.getIntFacetValue();
				facet = (XSFacet)xsSimpleTypeDef.getFacet(XSSimpleTypeDefinition.FACET_TOTALDIGITS);
				String totalDigits = facet == null ? "" : "" + facet.getIntFacetValue();
				String join = "";
				if (join.length() == 0) {
					join = maxLength;
				}
				if (join.length() == 0) {
					join = length;
				}
				if (join.length() == 0) {
					join = totalDigits;
				}
				if (join.length() > 0) {
					type = type + " {" + join + "}";
				}
			}
		}
		return type.equals("anyType") ? "" : type;
	}

	private String getEnumerationInfo(XSSimpleTypeDefinition xsSimpleTypeDef) {
		StringList sl = xsSimpleTypeDef.getLexicalEnumeration();
		StringBuilder sb = new StringBuilder();
		String enumeration = "";
		for (int i = 0; i < sl.getLength(); i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(sl.item(i));
		}
		if (sb.length() > 0) {
			sb.insert(0, "enum:(");
			sb.append("),");
			enumeration = sb.toString();
		}
		return enumeration;
	}

	private String getPatternInfo(XSSimpleTypeDefinition xsSimpleTypeDef) {
		StringList sl = xsSimpleTypeDef.getLexicalPattern();
		StringBuilder sb = new StringBuilder();
		String pattern = "";
		for (int i = 0; i < sl.getLength(); i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(sl.item(i));
		}
		if (sb.length() > 0) {
			sb.insert(0, "pattern:(");
			sb.append("),");
			pattern = sb.toString();
		}
		return pattern;
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
		boolean verbose = config.getBoolean(key + ".Verbose", false, false);
		return new DefaultSchemaDataTypeExtractor(variableName, verbose);
	}
}
