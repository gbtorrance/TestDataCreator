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
import org.tdc.spreadsheet.Spreadsheet;

/**
 * A {@link ModelCustomizerWriter} implementation. 
 */
public class ModelCustomizerWriterImpl implements ModelCustomizerWriter {
	
	private static final Logger log = LoggerFactory.getLogger(ModelCustomizerWriterImpl.class);

	private final ElementNodeDef rootElement;
	private final Spreadsheet sheet;
	private final int rowStart;
	private final int colStart;
	
	/**
	 * Constructor. 
	 * 
	 * @param rootElement Root {@link ElementNodeDef} of the {@link ModelDef} to customize. 
	 * @param sheet {@link Spreadsheet} to write to.
	 * @param rowStart Row start position
	 * @param colStart Column start position
	 */
	public ModelCustomizerWriterImpl(ElementNodeDef rootElement, Spreadsheet sheet, int rowStart, int colStart) {
		this.rootElement = rootElement;
		this.sheet = sheet;
		this.rowStart = rowStart;
		this.colStart = colStart;
	}
	
	/**
	 * Write customizer information as per the constructor definition. 
	 */
	@Override
	public void writeCustomizer() {
		processNode(rootElement);
	}
	
	private void processNode(NodeDef node) {
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
		if (node instanceof ElementNodeDef) {
			ElementNodeDef elementNodeDef = (ElementNodeDef)node;
			name = elementNodeDef.getName();
		}
		else if (node instanceof AttribNodeDef) {
			AttribNodeDef attribNodeDef = (AttribNodeDef)node;
			name = "@" + attribNodeDef.getName();
		}
		else if (node instanceof CompositorNodeDef) {
			CompositorNodeDef compositorNodeDef = (CompositorNodeDef)node;
			name = "[" + compositorNodeDef.getType().toString() + "]";
			
		}
		else {
			throw new IllegalStateException("Node not expected to be of type: " + node.getClass().getTypeName());
		}
		sheet.setCellValue(name, rowStart + node.getRowOffset(), colStart + node.getColOffset());
	}
}
