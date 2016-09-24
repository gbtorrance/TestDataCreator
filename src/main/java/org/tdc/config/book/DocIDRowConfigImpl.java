package org.tdc.config.book;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.tdc.config.XMLConfigWrapper;

/**
 * A {@link DocIDRowConfig} implementation.
 */
public class DocIDRowConfigImpl implements DocIDRowConfig {
	private final DocIDType docIDType;
	private final String variableName;
	private final String label;
	private final int index;
	private final int rowNum;
	
	private DocIDRowConfigImpl(Builder builder) {
		this.docIDType = builder.docIDType;
		this.variableName = builder.variableName;
		this.label = builder.label;
		this.index = builder.index;
		this.rowNum = builder.docIDRowStart + index;
	}
	
	@Override
	public DocIDType getType() {
		return docIDType;
	}

	@Override
	public String getVariableName() {
		return variableName;
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public int getIndex() {
		return index;
	}
	
	@Override
	public int getRowNum() {
		return rowNum;
	}
	
	@Override
	public boolean isIdentityEqual(DocIDRowConfig compareTo) {
		if (this == compareTo) {
			return true;
		}
		return Objects.equals(docIDType, compareTo.getType()) &&
				Objects.equals(variableName, compareTo.getVariableName());
	}
	
	public static class Builder {
		private static final String CONFIG_PREFIX = ".DocIDRows.Row";

		private final XMLConfigWrapper config;
		private final String pageKey;
		private final int docIDRowStart;
		
		private DocIDType docIDType;
		private String variableName;
		private String label;
		private int index;
		
		public Builder(XMLConfigWrapper config, String pageKey, int docIDRowStart) {
			this.config = config;
			this.pageKey = pageKey;
			this.docIDRowStart = docIDRowStart;
		}
		
		public List<DocIDRowConfig> buildAll() {
			List<DocIDRowConfig> rows = new ArrayList<>();
			int count = config.getCount(pageKey + CONFIG_PREFIX);
			int caseNumTypeCount = 0;
			int setNameTypeCount = 0;
			for (index = 0; index < count; index++) {
				DocIDRowConfig row = build();
				rows.add(row);
				if (row.getType() == DocIDType.CASE_NUM) {
					caseNumTypeCount++;
				}
				if (row.getType() == DocIDType.SET_NAME) {
					setNameTypeCount++;
				}
			}
			if (caseNumTypeCount != 1) {
				throw new IllegalStateException("Exactly one DocIDRow of type '" + 
						DocIDType.CASE_NUM.getConfigType() + "' must be specified: " + pageKey);
			}
			if (setNameTypeCount > 1) {
				throw new IllegalStateException("At most one DocIDRow of type '" + 
						DocIDType.SET_NAME.getConfigType() + "' can be specified: " + pageKey);
			}
			return rows;
		}
	
		private DocIDRowConfig build() {
			String indexPrefix = pageKey + CONFIG_PREFIX + "(" + index + ")";
			String type = config.getString(indexPrefix + "[@type]", null, true);
			try {
				docIDType = DocIDType.getDocIDTypeByConfigType(type);
			}
			catch (Exception ex) {
				throw new IllegalStateException("DocIDRow type '" + type + 
						"' invalid for config key: " + indexPrefix);
			}
			variableName = config.getString(indexPrefix + "[@variableName]", 
					null, docIDType.isVariableType());
			if (variableName != null && !docIDType.isVariableType()) {
				throw new IllegalStateException(
						"DocIDRow 'variableName' cannot be specified for rows of type '" + 
						docIDType.getConfigType() + "': " + indexPrefix);
			}
			label = config.getString(indexPrefix + ".Label", "", false);
			return new DocIDRowConfigImpl(this);
		}
	}
}
