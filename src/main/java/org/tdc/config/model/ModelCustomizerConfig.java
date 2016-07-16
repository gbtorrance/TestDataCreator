package org.tdc.config.model;

import java.nio.file.Path;
import java.util.List;

import org.tdc.spreadsheet.CellStyle;

/**
 * Defines the information needed to configure a Model Customizer.
 */
public interface ModelCustomizerConfig {
	Path getFilePath();
	CellStyle getDefaultStyle();
	CellStyle getDefaultHeaderStyle();
	CellStyle getDefaultNodeDetailColumnStyle();
	CellStyle getDefaultNodeStyle();
	CellStyle getParentNodeStyle();
	CellStyle getAttribNodeStyle();
	CellStyle getCompositorNodeStyle();
	CellStyle getChoiceMarkerStyle();
	int getNodeColumnCount();
	int getNodeColumnWidth();
	int getHeaderRowCount();
	String getNodeHeaderLabel(int headerRowNum);
	String getReadOccursCountOverrideFromVariable();
	boolean getAllowMinMaxInvalidOccursCountOverride();
	int getDefaultOccursCount();
	List<ModelCustomizerColumnConfig> getNodeDetailColumns();
	int getHeaderRowStart();
	int getNodeRowStart();
	int getNodeColStart();
	int getNodeDetailColStart();
}
