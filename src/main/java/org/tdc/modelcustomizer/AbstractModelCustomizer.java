package org.tdc.modelcustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.model.AttribNode;
import org.tdc.model.CompositorNode;
import org.tdc.model.ElementNode;
import org.tdc.model.ModelVisitor;
import org.tdc.model.TDCNode;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.ModelDef;
import org.tdc.spreadsheet.Spreadsheet;

/**
 * Abstract class containing common functionality for classes responsible for customizer reading and writing.  
 */
public abstract class AbstractModelCustomizer {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractModelCustomizer.class);
	
	protected static final int COL_OCCURS = 0;
	protected static final int COL_OCCURS_OVERRIDE = 1;
	protected static final int COL_CUSTOM_BASE = 2;
	
	private final ElementNodeDef rootElement;
	private final ModelCustomizerConfig config; 
	private final Spreadsheet sheet;
	private final int nodeRowStart;
	private final int dataColStart;
	
	/**
	 * Constructor. 
	 * 
	 * @param rootElement Root {@link ElementNodeDef} of the {@link ModelDef} to customize. 
	 * @param format{@link ModelCustomizerConfig} containing customizer config information (format, style, params).
	 * @param sheet {@link Spreadsheet} to write to or read from.
	 */
	public AbstractModelCustomizer(ElementNodeDef rootElement, ModelCustomizerConfig config, Spreadsheet sheet) {
		this.rootElement = rootElement;
		this.config = config;
		this.sheet = sheet;
		this.nodeRowStart = 3;
		this.dataColStart = config.getTreeStructureColumnCount() + 1;
	}
	
	protected final ElementNodeDef getRootElement() {
		return rootElement;
	}
	
	protected final Spreadsheet getSheet() {
		return sheet;
	}
	
	protected final ModelCustomizerConfig getConfig() {
		return config;
	}
	
	protected final int getNodeRowStart() {
		return nodeRowStart;
	}
	
	protected final int getDataColStart() {
		return dataColStart;
	}
	
	protected final int getNodeRow(TDCNode node) {
		return nodeRowStart + node.getRowOffset();
	}
	
	protected final int getNodeCol(TDCNode node) {
		return 1 + node.getColOffset();
	}
	
	protected final int getDataCol(int dataColOffset) {
		return dataColStart + dataColOffset;
	}
	
	protected void processTree() {
		rootElement.accept(new CustomizerVisitor());
	}
	
	protected abstract void processAttribNode(AttribNode attribNode);
	
	protected abstract void processCompositorNode(CompositorNode compositorNode);
	
	protected abstract void processElementNode(ElementNode elementNode);
	
	class CustomizerVisitor implements ModelVisitor {
		@Override
		public void visit(AttribNode attribNode) {
			processAttribNode(attribNode);
		}

		@Override
		public void visit(CompositorNode compositorNode) {
			processCompositorNode(compositorNode);
		}

		@Override
		public void visit(ElementNode elementNode) {
			processElementNode(elementNode);
		}
	}
}
