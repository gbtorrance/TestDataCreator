package org.tdc.config.book;

import java.util.ArrayList;
import java.util.List;

import org.tdc.config.XMLConfigWrapper;

/**
 * A {@link DocIDRowConfig} implementation.
 */
public class DocIDRowConfigImpl implements DocIDRowConfig {
	private final DocIDType docIDType;
	private final String docVariableName;
	private final String label;
	private final int index;
	private final int rowNum;
	
	public DocIDRowConfigImpl(Builder builder) {
		this.docIDType = builder.docIDType;
		this.docVariableName = builder.docVariableName;
		this.label = builder.label;
		this.index = builder.index;
		this.rowNum = builder.docIDRowStart + index;
	}
	
	@Override
	public DocIDType getType() {
		return docIDType;
	}

	@Override
	public String getDocVariableName() {
		return docVariableName;
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
	
	public static class Builder {
		private static final String CONFIG_PREFIX = ".DocIDRows.Row";

		private final XMLConfigWrapper config;
		private final String pageKey;
		private final int docIDRowStart;
		
		private DocIDType docIDType;
		private String docVariableName;
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
			docVariableName = config.getString(indexPrefix + "[@variableName]", 
					null, docIDType == DocIDType.DOC_VARIABLE);
			if (docVariableName != null && docIDType != DocIDType.DOC_VARIABLE) {
				throw new IllegalStateException(
						"DocIDRow variableName can only be specified for a row of type 'doc-variable': " + 
						indexPrefix);
			}
			label = config.getString(indexPrefix + ".Label", "", false);
			return new DocIDRowConfigImpl(this);
		}
	}
}
