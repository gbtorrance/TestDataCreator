package org.tdc.modelcustomizer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.model.Attrib;
import org.tdc.model.CanHaveAttributes;
import org.tdc.model.CanHaveChildren;
import org.tdc.model.NonAttrib;
import org.tdc.modeldef.AttribNodeDef;
import org.tdc.modeldef.CompositorNodeDef;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.ModelDef;
import org.tdc.modeldef.NodeDef;
import org.tdc.modeldef.NonAttribNodeDef;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.Spreadsheet;

/**
 * A {@link ModelCustomizerWriter} implementation. 
 */
public class ModelCustomizerWriterImpl implements ModelCustomizerWriter {
	
	private static final Logger log = LoggerFactory.getLogger(ModelCustomizerWriterImpl.class);

	private final ElementNodeDef rootElement;
	private final ModelCustomizerFormat format; 
	private final Spreadsheet sheet;
	private final int rowStart;
	private final int colStart;
	
	private int maxColOffset;
	
	/**
	 * Constructor. 
	 * 
	 * @param rootElement Root {@link ElementNodeDef} of the {@link ModelDef} to customize. 
	 * @param format{@link ModelCustomizerFormat} containing format and style information to use for writing.
	 * @param sheet {@link Spreadsheet} to write to.
	 * @param rowStart Row start position
	 * @param colStart Column start position
	 */
	public ModelCustomizerWriterImpl(ElementNodeDef rootElement, ModelCustomizerFormat format, Spreadsheet sheet, int rowStart, int colStart) {
		this.rootElement = rootElement;
		this.format = format;
		this.sheet = sheet;
		this.rowStart = rowStart;
		this.colStart = colStart;
	}
	
	/**
	 * Write customizer information as per the constructor definition. 
	 */
	@Override
	public void writeCustomizer() {
		sheet.setDefaultCellStyle(format.getDefaultNodeStyle());
		maxColOffset = 0;
		processNode(rootElement);
		formatColumns();
	}
	
	private void processNode(NodeDef node) {
		maxColOffset = Integer.max(maxColOffset, node.getColOffset());
		writeNode(node);
		if (node instanceof CanHaveAttributes) {
			CanHaveAttributes canHaveAttributes = (CanHaveAttributes)node;
			List<? extends Attrib> attribs = canHaveAttributes.getAttributes();
			for (Attrib attrib : attribs) {
				processNode((AttribNodeDef)attrib);
			}
		}
		if (node instanceof CanHaveChildren) {
			CanHaveChildren canHaveChildren = (CanHaveChildren)node;
			List<? extends NonAttrib> nonAttribs = canHaveChildren.getChildren();
			for (NonAttrib nonAttrib : nonAttribs) {
				processNode((NonAttribNodeDef)nonAttrib);
			}
		}
	}

	private void writeNode(NodeDef node) {
		String name;
		CellStyle cellStyle = null;
		if (node instanceof AttribNodeDef) {
			AttribNodeDef attribNodeDef = (AttribNodeDef)node;
			name = "@" + attribNodeDef.getName();
			cellStyle = format.getAttribNodeStyle();
		}
		else {
			if (node instanceof ElementNodeDef) {
				ElementNodeDef elementNodeDef = (ElementNodeDef)node;
				name = elementNodeDef.getName();
				if (elementNodeDef.hasChild()) {
					cellStyle = format.getParentNodeStyle();
				}
			}
			else if (node instanceof CompositorNodeDef) {
				CompositorNodeDef compositorNodeDef = (CompositorNodeDef)node;
				name = "[" + compositorNodeDef.getType().toString() + "]";
				cellStyle = format.getCompositorNodeStyle();
			}
			else {
				throw new IllegalStateException("Node not expected to be of type: " + node.getClass().getTypeName());
			}
			// node is a subclass of NonAttribNodeDef...
			outputNonAttribChoiceMarker((NonAttribNodeDef)node);
		}
		sheet.setCellValue(name, rowStart + node.getRowOffset(), colStart + node.getColOffset(), cellStyle);
	}

	private void outputNonAttribChoiceMarker(NonAttribNodeDef node) {
		if (node.isChildOfChoice()) {
			CellStyle cellStyle = format.getChoiceMarkerStyle(); 
			sheet.setCellValue(">", rowStart + node.getRowOffset(), colStart + node.getColOffset() - 1, cellStyle);
		}
	}

	private void formatColumns() {
		int cols = format.getTreeStructureColumnCount(); 
		if (cols < maxColOffset + 1) {
			throw new RuntimeException("TreeStructureColumnCount (" + cols + ") must be at least " + (maxColOffset+1) + " to support this particular model");
		}
		for (int i = 1; i <= cols; i++) {
			sheet.setColumnWidth(i, format.getTreeStructureColumnWidth());
		}
	}
}
