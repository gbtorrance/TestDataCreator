package org.tdc.modelcustomizer;

import org.tdc.modeldef.ModelDef;
import org.tdc.modelinst.ModelInst;

/**
 * Defines the core functionality for outputting a new "model customizer".
 * 
 * <p>A model customizer is a spreadsheet containing a structured representation of a 
 * {@link ModelDef} used for configuration purposes. This can be used for customizing 
 * the structure of a {@link ModelInst} created based on the {@link ModelDef}.
 *  
 * <p>One of the primary customizations is the specification of the number of occurrences of 
 * each repeating element to include in a {@link ModelInst}.
 */
public interface ModelCustomizerWriter {
	
	/**
	 * Write customizer information per implementation definition.
	 */
	public void writeCustomizer();

}
