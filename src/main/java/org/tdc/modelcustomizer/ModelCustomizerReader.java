package org.tdc.modelcustomizer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelCustomizerColumnConfig;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.model.AttribNode;
import org.tdc.model.CompositorNode;
import org.tdc.model.ElementNode;
import org.tdc.model.NonAttribNode;
import org.tdc.model.TDCNode;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.NodeDef;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * Implementation of class with functionality for reading an existing customizer spreadsheet
 * and updating a ModelDef with customizations extracted from the spreadsheet.
 */
public class ModelCustomizerReader extends AbstractModelCustomizer {

	private static final Logger log = LoggerFactory.getLogger(ModelCustomizerReader.class);

	public ModelCustomizerReader(ElementNodeDef rootElement, ModelCustomizerConfig config, 
			SpreadsheetFile spreadsheetFile) {
		super(rootElement, config, spreadsheetFile);
	}
	
	public void readCustomizer() {
		if (getSpreadsheetFile().getSpreadsheet(CUSTOMIZER_SHEET_NAME) == null) {
			throw new IllegalStateException("Customizer is missing required '" + CUSTOMIZER_SHEET_NAME + "' worksheet");
		}
		processTree();
	}
	
	@Override
	protected void processAttribNode(AttribNode node) {
		validateNode(node);
		readOccursCountOverride(node, true);
		readCustomColumns(node);
	}

	@Override
	protected void processCompositorNode(CompositorNode node) {
		validateNode(node);
		readOccursCountOverride(node, false);
		readCustomColumns(node);
	}
	
	@Override
	protected void processElementNode(ElementNode node) {
		validateNode(node);
		readOccursCountOverride(node, false);
		readCustomColumns(node);
	}
	
	private void validateNode(TDCNode node) {
		validateNodeName(node);
		validateNodeOccurs(node);
	}
	
	private void validateNodeName(TDCNode node) {
		int row = getNodeRow(node);
		int col = getNodeCol(node);
		String expectedValue = node.getDispName();
		String actualValue = getCustomizerSheet().getCellValue(row, col);
		if (!actualValue.equals(expectedValue)) {
			exception(row, col, "Invalid node name", actualValue, "expected '" + expectedValue + "'");
		}
	}

	private void validateNodeOccurs(TDCNode node) {
		int row = getNodeRow(node);
		int col = getDataCol(COL_OCCURS);
		String expectedValue = node.getDispOccurs();
		String actualValue = getCustomizerSheet().getCellValue(row, col);
		if (!actualValue.equals(expectedValue)) {
			exception(row, col, "Invalid Occurs value", actualValue, "expected '" + expectedValue + "'");
		}
	}
	
	private void readOccursCountOverride(TDCNode node, boolean isAttrib) {
		int row = getNodeRow(node);
		int col = getDataCol(COL_OCCURS_OVERRIDE);
		String overrideStr = getCustomizerSheet().getCellValue(row, col).trim();
		int override = -1;
		if (overrideStr.length() > 0) {
			boolean allowInvalid = getConfig().getAllowMinMaxInvalidOccursCountOverride();
			AttribNode attrib = isAttrib ? (AttribNode)node : null;
			NonAttribNode nonAttrib = !isAttrib ? (NonAttribNode)node : null;
			try {
				override = Integer.parseUnsignedInt(overrideStr);
			}
			catch (NumberFormatException ex) {
				exception(row, col, "Invalid Occurs Override value", overrideStr, 
						"expected non-negative number");
			}
			if (isAttrib && override > 1) {
				exception(row, col, "Invalid Occurs Override value", overrideStr, 
						"attribute can have at most one occurrence");
			}
			if (node == getRootElement() && override != 1) {
				exception(row, col, "Invalid Occurs Override value", overrideStr, 
						"root element must have exactly one occurrence");
			}
			if (!allowInvalid) {
				if (isAttrib) {
					if (attrib.isRequired() && override != 1) {
						exception(row, col, "Invalid Occurs Override value", overrideStr, 
								"required attributes must have exactly one occurrence");
					}
				}
				else {
					if (!nonAttrib.isUnbounded() && override > nonAttrib.getMaxOccurs()) {
						exception(row, col, "Invalid Occurs Override value", overrideStr, 
								"cannot be greater than max " + nonAttrib.getMaxOccurs());
					}
					if (override < nonAttrib.getMinOccurs()) {
						exception(row, col, "Invalid Occurs Override value", overrideStr, 
								"cannot be less than min " + nonAttrib.getMinOccurs());
					}
				}
			}
		}
		NodeDef nodeDef = (NodeDef)node;
		nodeDef.setOccursCountOverride(override);
	}
	
	private void readCustomColumns(TDCNode node) {
		NodeDef nodeDef = (NodeDef)node; // cast so we can set variables;
		List<ModelCustomizerColumnConfig> columns = getConfig().getColumns(); 
		for (int i = 0; i < columns.size(); i++) {
			ModelCustomizerColumnConfig column = columns.get(i);
			String variable = column.getStoreValueWithVariableName();
			if (variable != null && !variable.equals("")) {
				String value = getCustomizerSheet().getCellValue(getNodeRow(node), getDataCol(COL_CUSTOM_BASE) + i).trim();
				nodeDef.setVariable(variable, value);
			}
		}
	}
	
	private void exception(int row, int col, String invalidMsg, String actualValue, String expectedMsg) {
		throw new RuntimeException(invalidMsg + " '" + actualValue + 
				"' at row " + row + " col " + col + "; " + expectedMsg);
	}
}
