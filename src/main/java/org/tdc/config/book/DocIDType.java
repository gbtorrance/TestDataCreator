package org.tdc.config.book;

/**
 * Enum defining the possible types for a {@link DocIDRowConfig} entry in a {@link BookConfig} file.
 */
public enum DocIDType {
	CASE_NUM("case-num"),
	SET_NAME("set-name"),
	DOC_VARIABLE("doc-variable"),
	NOTE("note");

	private final String configType;
	
	private DocIDType(String configType) {
		this.configType = configType;
	}
	
	public String getConfigType() {
		return configType;
	}
	
	public static DocIDType getDocIDTypeByConfigType(String configType) {
		for (DocIDType docIDType : DocIDType.values()) {
			if (docIDType.getConfigType().equals(configType)) {
				return docIDType;
			}
		}
		throw new IllegalStateException("DocIDRow type '" + configType + "' is not valid");
	}
}
