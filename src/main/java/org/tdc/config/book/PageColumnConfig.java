package org.tdc.config.book;

import org.tdc.spreadsheet.CellStyle;

/**
 * Defines the information needed to configure a Book Page Column.
 */
public interface PageColumnConfig {
	String getHeaderLabel(int headerRowNum);
	int getWidth();
	CellStyle getStyle(); 
	String getReadFromVariable();
}
