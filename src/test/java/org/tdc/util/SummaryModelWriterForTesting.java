package org.tdc.util;

import org.tdc.model.TDCNode;
import org.tdc.util.Util;

/**
 * A {@link AbstractModelWriterForTesting} implementation for creating 
 * node representations to use for general summary purposes.
 * 
 * <p>The output produced by this class is useful for getting an 
 * overall understanding of a Model tree structure.
 */
public class SummaryModelWriterForTesting extends AbstractModelWriterForTesting {

	public SummaryModelWriterForTesting(TDCNode rootNode, int indentSize) {
		super(rootNode, indentSize);
	}

	@Override
	protected String tempBuildNodeString(TDCNode node, int level) {
		String summary = node.toTestSummaryString();
		return Util.spaces(level * getIndentSize()) + summary;
	}
}
