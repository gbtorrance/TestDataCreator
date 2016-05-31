package org.tdc.modelcustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.ModelDef;
import org.tdc.spreadsheet.Spreadsheet;

/**
 * Abstract class containing common functionality for classes responsible for customizer reading and writing.  
 */
public abstract class AbstractModelCustomizer {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractModelCustomizer.class);

	private final ElementNodeDef rootElement;
	private final ModelCustomizerFormat format; 
	private final Spreadsheet sheet;
	private final int rowStart;
	private final int colStart;
	
	/**
	 * Constructor. 
	 * 
	 * @param rootElement Root {@link ElementNodeDef} of the {@link ModelDef} to customize. 
	 * @param format{@link ModelCustomizerFormat} containing format and style information to use for writing.
	 * @param sheet {@link Spreadsheet} to write to.
	 */
	public AbstractModelCustomizer(ElementNodeDef rootElement, ModelCustomizerFormat format, Spreadsheet sheet) {
		this.rootElement = rootElement;
		this.format = format;
		this.sheet = sheet;
		this.rowStart = 2;
		this.colStart = 1;
	}
	
	protected final ElementNodeDef getRootElement() {
		return rootElement;
	}
	
	protected final Spreadsheet getSheet() {
		return sheet;
	}
	
	protected final ModelCustomizerFormat getFormat() {
		return format;
	}
	
	protected final int getRowStart() {
		return rowStart;
	}
	
	protected final int getColStart() {
		return colStart;
	}
}
