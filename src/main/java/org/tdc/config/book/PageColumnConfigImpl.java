package org.tdc.config.book;

import java.util.ArrayList;
import java.util.List;

import org.tdc.config.XMLConfigWrapper;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link PageColumnConfigImpl} implementation.
 */
public class PageColumnConfigImpl implements PageColumnConfig {
	private final String[] headerLabels;
	private final int width;
	private final CellStyle style;
	private final String readFromVariable;
	private final String readFromProperty;
	
	private PageColumnConfigImpl(Builder builder) {
		this.headerLabels = builder.headerLabels;
		this.width = builder.width;
		this.style = builder.style;
		this.readFromVariable = builder.readFromVariable;
		this.readFromProperty = builder.readFromProperty;
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

	public static class Builder {
		private static final String CONFIG_PREFIX = ".Columns.Column";

		private final XMLConfigWrapper config;
		private final String pageKey;
		private final int headerRowCount;
		private final CellStyle defaultColumnStyle;
		
		private String[] headerLabels;
		private int width;
		private CellStyle style;
		private String readFromVariable;
		private String readFromProperty;
		
		public Builder(XMLConfigWrapper config, 
				String pageKey, int headerRowCount, CellStyle defaultColumnStyle) {
			this.config = config;
			this.pageKey = pageKey;
			this.headerRowCount = headerRowCount;
			this.defaultColumnStyle = defaultColumnStyle;
		}
		
		public List<PageColumnConfig> buildAll() {
			List<PageColumnConfig> columns = new ArrayList<>();
			int count = config.getCount(pageKey + CONFIG_PREFIX);
			for (int i = 0; i < count; i++) {
				PageColumnConfig column = build(i);
				columns.add(column);
			}
			return columns;
		}
	
		private PageColumnConfig build(int index) {
			String indexPrefix = pageKey + CONFIG_PREFIX + "(" + index + ").";
			headerLabels = config.getHeaderLabels(
					indexPrefix + "HeaderLabels", headerRowCount);
			width = config.getInt(indexPrefix + "Width", 0, true);
			style = config.getCellStyle(indexPrefix + "Style", defaultColumnStyle, false);
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
			return new PageColumnConfigImpl(this);
		}
	}
}
