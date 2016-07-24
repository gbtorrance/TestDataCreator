package org.tdc.config.book;

import java.util.ArrayList;
import java.util.List;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link PageNodeDetailColumnConfigImpl} implementation.
 */
public class PageNodeDetailColumnConfigImpl implements PageNodeDetailColumnConfig {
	private final String[] headerLabels;
	private final int width;
	private final CellStyle style;
	private final String readFromVariable;
	private final String readFromProperty;
	private final int index;
	private final int colNum;
	
	private PageNodeDetailColumnConfigImpl(Builder builder) {
		this.headerLabels = builder.headerLabels;
		this.width = builder.width;
		this.style = builder.style;
		this.readFromVariable = builder.readFromVariable;
		this.readFromProperty = builder.readFromProperty;
		this.index = builder.index;
		this.colNum = builder.nodeDetailColStart + index;
	}

	@Override
	public String getHeaderLabel(int headerRowNum) {
		return headerLabels[headerRowNum-1];
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public CellStyle getStyle() {
		return style;
	}

	@Override
	public String getReadFromVariable() {
		return readFromVariable;
	}

	@Override
	public String getReadFromProperty() {
		return readFromProperty;
	}
	
	@Override
	public int getIndex() {
		return index;
	}
	
	@Override
	public int getColNum() {
		return colNum;
	}
	
	public static class Builder {
		private static final String CONFIG_PREFIX = ".NodeDetailColumns.Column";

		private final XMLConfigWrapper config;
		private final String pageKey;
		private final int headerRowCount;
		private final CellStyle defaultNodeDetailColumnStyle;
		private final int nodeDetailColStart;
		
		private String[] headerLabels;
		private int width;
		private CellStyle style;
		private String readFromVariable;
		private String readFromProperty;
		private int index;
		
		public Builder(XMLConfigWrapper config, String pageKey, 
				int headerRowCount, CellStyle defaultNodeDetailColumnStyle, int nodeDetailColStart) {
			this.config = config;
			this.pageKey = pageKey;
			this.headerRowCount = headerRowCount;
			this.defaultNodeDetailColumnStyle = defaultNodeDetailColumnStyle;
			this.nodeDetailColStart = nodeDetailColStart;
		}
		
		public List<PageNodeDetailColumnConfig> buildAll() {
			List<PageNodeDetailColumnConfig> columns = new ArrayList<>();
			int count = config.getCount(pageKey + CONFIG_PREFIX);
			for (index = 0; index < count; index++) {
				PageNodeDetailColumnConfig column = build();
				columns.add(column);
			}
			return columns;
		}
	
		private PageNodeDetailColumnConfig build() {
			String indexPrefix = pageKey + CONFIG_PREFIX + "(" + index + ").";
			headerLabels = config.getHeaderLabels(
					indexPrefix + "HeaderLabels", headerRowCount);
			width = config.getInt(indexPrefix + "Width", 0, true);
			style = config.getCellStyle(indexPrefix + "Style", defaultNodeDetailColumnStyle, false);
			readFromVariable = config.getString(
					indexPrefix + "ReadFromVariable", null, false);
			readFromProperty = config.getString(
					indexPrefix + "ReadFromProperty", null, false);
			if ((readFromVariable == null && readFromProperty == null) ||
				(readFromVariable != null && readFromProperty != null)	) {
				throw new IllegalStateException(
						"One and only one of ReadFromVariable or ReadFromProperty must be specified for: " + 
						indexPrefix);
			}
			return new PageNodeDetailColumnConfigImpl(this);
		}
	}
}