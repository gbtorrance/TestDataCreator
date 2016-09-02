package org.tdc.config.book;

import java.nio.file.Path;
import java.util.List;
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
	String getBookName();
	String getBookDescription();
	Map<String, DocTypeConfig> getDocTypeConfigs();
	Map<String, PageConfig> getPageConfigs();
	List<TaskConfig> getTaskConfigs();
	CellStyle getDefaultStyle();
	CellStyle getDefaultHeaderStyle();
	CellStyle getDefaultNodeDetailColumnStyle();
	CellStyle getDefaultNodeStyle();
	CellStyle getParentNodeStyle();
	CellStyle getAttribNodeStyle();
	CellStyle getCompositorNodeStyle();
	CellStyle getChoiceMarkerStyle();
	CellStyle getOccurMarkerStyle();
	int getNodeColumnCount();
	int getNodeColumnWidth();
	int getHeaderRowCount();
	String getNodeHeaderLabel(int headerRowNum);
}
