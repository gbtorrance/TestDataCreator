package org.tdc.modelcustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.model.AttribNode;
import org.tdc.model.CompositorNode;
import org.tdc.model.ElementNode;
import org.tdc.model.NonAttribNode;
import org.tdc.model.TDCNode;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * Implementation of class with functionality for creating an initial customizer spreadsheet.
 */
public class ModelCustomizerWriter extends AbstractModelCustomizer {

	private static final Logger log = LoggerFactory.getLogger(ModelCustomizerWriter.class);

	private int maxColumns;
	
	public ModelCustomizerWriter(ElementNodeDef rootElement, ModelCustomizerConfig config, 
			SpreadsheetFile spreadsheetFile) {
		super(rootElement, config, spreadsheetFile);
	}
	
	public void writeCustomizer() {
		getSpreadsheetFile().createSpreadsheet(CUSTOMIZER_SHEET_NAME);
		getCustomizerSheet().setDefaultCellStyle(getConfig().getDefaultNodeStyle());
		maxColumns = 0;
		processTree();
		formatColumns();
	}
	
	@Override
	protected void processAttribNode(AttribNode node) {
		trackMaxColumns(node);

		outputNodeName(node, getConfig().getAttribNodeStyle());
		outputOccurs(node);
		outputOccursOverride(node, true);
	}

	@Override
	protected void processCompositorNode(CompositorNode node) {
		trackMaxColumns(node);

		if (node.isChildOfChoice()) {
			outputChoiceMarker(node, getConfig().getChoiceMarkerStyle());
		}
		
		outputNodeName(node, getConfig().getCompositorNodeStyle());
		outputOccurs(node);
		outputOccursOverride(node, false);
	}
	
	@Override
	protected void processElementNode(ElementNode node) {
		trackMaxColumns(node);

		if (node.isChildOfChoice()) {
			outputChoiceMarker(node, getConfig().getChoiceMarkerStyle());
		}
		
		CellStyle cellStyle = getConfig().getDefaultNodeStyle();
		if (node.hasChild()) {
			cellStyle = getConfig().getParentNodeStyle();
		}

		outputNodeName(node, cellStyle);
		outputOccurs(node);
		outputOccursOverride(node, false);
	}
	
	private void trackMaxColumns(TDCNode node) {
		maxColumns = Integer.max(maxColumns, node.getColOffset()+1);
	}
	
	private void outputChoiceMarker(NonAttribNode node, CellStyle cellStyle) {
		getCustomizerSheet().setCellValue(">", getNodeRow(node), getNodeCol(node) - 1, cellStyle);
	}

	private void outputNodeName(TDCNode node, CellStyle cellStyle) {
		getCustomizerSheet().setCellValue(node.getDispName(), getNodeRow(node), getNodeCol(node), cellStyle);
	}
	
	private void outputOccurs(TDCNode node) {
		getCustomizerSheet().setCellValue(node.getDispOccurs(), getNodeRow(node), getDataCol(COL_OCCURS)); 
	}
	
	private void outputOccursOverride(TDCNode node, boolean isAttrib) {
		int initialOverride;
		int defaultOccurs = getConfig().getDefaultOccursCount();
		if (isAttrib) {
			AttribNode attrib = (AttribNode)node;
			initialOverride = attrib.isRequired() || defaultOccurs > 0 ? 1 : 0;
		}
		else {
			NonAttribNode nonAttrib = (NonAttribNode)node;
			initialOverride = defaultOccurs;
			if (!nonAttrib.isUnbounded() && initialOverride > nonAttrib.getMaxOccurs()) {
				initialOverride = nonAttrib.getMaxOccurs();
			}
			if (initialOverride < nonAttrib.getMinOccurs()) {
				initialOverride = nonAttrib.getMinOccurs();
			}
		}
		// only output an override number if > 1 (to avoid cluttering the spreadsheet); 
		// zero and one will be handled by the default (unless manually overriden in the spreadsheet)
		String initialOverrideStr = initialOverride > 1 ? Integer.toString(initialOverride) : "";
		getCustomizerSheet().setCellValue(initialOverrideStr, getNodeRow(node), getDataCol(COL_OCCURS_OVERRIDE));
	}

	private void formatColumns() {
		int allowedColumns = getConfig().getTreeStructureColumnCount(); 
		if (allowedColumns < maxColumns) {
			throw new RuntimeException("TreeStructureColumnCount (" + allowedColumns + 
					") must be at least " + maxColumns + " to support this particular model");
		}
		for (int i = 1; i <= allowedColumns; i++) {
			getCustomizerSheet().setColumnWidth(i, getConfig().getTreeStructureColumnWidth());
		}
	}
}
