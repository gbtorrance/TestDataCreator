package org.tdc.config.book;

import java.nio.file.Path;
import java.util.Map;

import org.tdc.spreadsheet.CellStyle;
import org.tdc.util.Addr;

/**
 * Defines getters for configuration items applicable to {@link org.tdc.book.Book Books}.
 */
public interface BookConfig {
	Path getBooksConfigRoot();
	Addr getAddr();
	Path getBookConfigRoot();
	Map<String, DocTypeConfig> getDocTypeConfigs();
	Map<String, PageConfig> getPageConfigs();
	String getConfigSheetName();
	int getConfigSheetBookAddrRow();
	int getConfigSheetBookAddrCol();
	CellStyle getDefaultStyle();
	CellStyle getDefaultHeaderStyle();
	CellStyle getDefaultColumnStyle();
	CellStyle getDefaultNodeStyle();
	CellStyle getParentNodeStyle();
	CellStyle getAttribNodeStyle();
	CellStyle getCompositorNodeStyle();
	CellStyle getChoiceMarkerStyle();
	CellStyle getOccurMarkerStyle();
	int getTreeStructureColumnCount();
	int getTreeStructureColumnWidth();
	int getHeaderRowCount();
	String getTreeStructureHeaderLabel(int headerRowNum);
}
