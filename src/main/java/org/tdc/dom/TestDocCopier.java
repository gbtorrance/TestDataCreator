package org.tdc.dom;

import java.util.List;

import org.tdc.book.TestDoc;
import org.tdc.config.book.DocIDRowConfig;
import org.tdc.config.book.DocIDType;
import org.tdc.config.book.PageConfig;
import org.tdc.model.MPathIndex;
import org.tdc.model.TDCNode;
import org.tdc.modelinst.ModelInst;
import org.tdc.modelinst.NodeInst;
import org.tdc.spreadsheet.Spreadsheet;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Copies and converts the contents of one or more {@link TestDoc}s to a 
 * target {@link ModelInst} structure, writing the results to a provided {@link Spreadsheet}.
 * 
 * <p>Makes use of the {@link MPathIndex} of the target {@link ModelInst} to determine
 * an appropriate mapping of rows in the source to the target.  
 */
public class TestDocCopier {
	private final ModelInst targetModelInst;
	private final PageConfig targetPageConfig;
	private final Spreadsheet targetSheet;
	private final MPathIndex<NodeInst> targetMPathIndex;
	
	public TestDocCopier(
			ModelInst targetModelInst, 
			PageConfig targetPageConfig, 
			Spreadsheet targetSheet) {
		
		this.targetModelInst = targetModelInst;
		this.targetPageConfig = targetPageConfig;
		this.targetSheet = targetSheet;
		targetMPathIndex = targetModelInst.getMPathIndex();
	}

	public void copyTestDoc(TestDoc testDoc, int colNum) {
		Element sourceDOMRootElement = testDoc.getDOMDocument().getDocumentElement();
		copyNodeTree(sourceDOMRootElement, colNum);
		copyDocIDRows(testDoc, colNum);
	}

	private void copyNodeTree(Node node, int colNum) {
		if (isTextNode(node)) {
			copyTextNodeValue(node, colNum);
		}
		NamedNodeMap attribs = node.getAttributes();
		int numAttribs = attribs == null ? 0 : attribs.getLength();
		for (int i = 0; i < numAttribs; i++) {
			copyNodeTree(attribs.item(i), colNum);
		}
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			copyNodeTree(children.item(i), colNum);
		}
	}
	
	private boolean isTextNode(Node node) {
		return node.hasChildNodes() && node.getFirstChild().getNodeType() == Node.TEXT_NODE;
	}
	
	private void copyTextNodeValue(Node node, int colNum) {
		// get NodeInst in source TestDoc model
		NodeInst sourceNodeInst = (NodeInst)node.getUserData(DOMUtil.DOM_USER_DATA_RELATED_TDC_NODE);
		if (sourceNodeInst != null) {
			// get mpath address of NodeInst in source model
			String mpath = sourceNodeInst.getMPath();
			// use mpath of source NodeInst to look up corresponding NodeInst in target model
			NodeInst targetNodeInst = targetMPathIndex.getNode(mpath);
			// if target NodeInst exists, write source TestDoc value to new Spreadsheet
			if (targetNodeInst != null) {
				String value = getTextNodeValue(node);
				targetSheet.setCellValue(value, getNodeRow(targetNodeInst), colNum);
			}
		}
	}
	
	private String getTextNodeValue(Node node) {
		return node.getFirstChild().getTextContent();
	}

	private void copyDocIDRows(TestDoc testDoc, int colNum) {
		copySetNameDocIDRow(testDoc, colNum);
		copyCaseNumDocIDRow(testDoc, colNum);
		copyVarDocIDRows(testDoc, colNum);
	}

	private void copySetNameDocIDRow(TestDoc testDoc, int colNum) {
		int rowNum = targetPageConfig.getSetNameDocIDRowConfig().getRowNum();
		String setName = testDoc.getSetName();
		targetSheet.setCellValue(setName, rowNum, colNum);
	}

	private void copyCaseNumDocIDRow(TestDoc testDoc, int colNum) {
		int rowNum = targetPageConfig.getCaseNumDocIDRowConfig().getRowNum();
		String caseNum = "" + testDoc.getCaseNum();
		targetSheet.setCellValue(caseNum, rowNum, colNum);
	}

	private void copyVarDocIDRows(TestDoc testDoc, int colNum) {
		List<DocIDRowConfig> varDocIDRows = targetPageConfig.getVarDocIDRowConfigs();
		for (DocIDRowConfig varDocIDRow : varDocIDRows) {
			int rowNum = varDocIDRow.getRowNum();
			DocIDType type = varDocIDRow.getType();
			String name = varDocIDRow.getVariableName();
			String value = type == DocIDType.CASE_VARIABLE ?
					testDoc.getCaseVariables().get(name) :
					testDoc.getDocVariables().get(name);
			targetSheet.setCellValue(value, rowNum, colNum);
		}
	}

	private int getNodeRow(TDCNode node) {
		return targetPageConfig.getNodeRowStart() + node.getRowOffset();
	}
}
