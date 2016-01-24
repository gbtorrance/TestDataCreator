package org.tdc.util;

import org.tdc.model.TDCNode;
import org.tdc.util.Util;

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
