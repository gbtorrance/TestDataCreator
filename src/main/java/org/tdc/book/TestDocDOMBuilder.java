package org.tdc.book;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.message.Message;
import org.tdc.message.TestDocMessageType;
import org.tdc.message.TestDocMessages;
import org.tdc.modelinst.AttribNodeInst;
import org.tdc.modelinst.CompositorNodeInst;
import org.tdc.modelinst.ElementNodeInst;
import org.tdc.modelinst.ModelInst;
import org.tdc.modelinst.NonAttribNodeInst;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Builds DOM {@link Document}s for {@link TestDoc}s from the data in a particular column of a {@link Spreadsheet}.
 *
 * <p>Note: Apache POI spreadsheet processing is NOT thread safe; 
 * as such, the processing for a {@link SpreadsheetFile} must be done in a single thread.
*/
public class TestDocDOMBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(TestDocDOMBuilder.class);
	
	private final DocumentBuilder documentBuilder;
	
	private ModelInst modelInst;
	private Spreadsheet sheet;
	private int nodeRowStart;
	private int testDocColNum;
	private String testDocColLetter;
	private String namespace;
	private TestDocMessages messages;
	private Document document;
	
	public TestDocDOMBuilder() {
		documentBuilder = createDocumentBuilder();
		nodeRowStart = -1;
		testDocColNum = -1;
	}
	
	public TestDocDOMBuilder setModelInst(ModelInst modelInst) {
		this.modelInst = modelInst;
		return this;
	}
	
	public TestDocDOMBuilder setSpreadsheet(Spreadsheet sheet) {
		this.sheet = sheet;
		return this;
	}
	
	public TestDocDOMBuilder setNodeRowStart(int nodeRowStart) {
		this.nodeRowStart = nodeRowStart;
		return this;
	}
	
	public TestDocDOMBuilder setTestDocColNumAndLetter(int testDocColNum, String testDocColLetter) {
		this.testDocColNum = testDocColNum;
		this.testDocColLetter = testDocColLetter;
		return this;
	}
	
	public TestDocDOMBuilder setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}
	
	public TestDocDOMBuilder setMessages(TestDocMessages messages) {
		this.messages = messages;
		return this;
	}
	
	public Document build() {
		if (modelInst == null || sheet == null || 
				nodeRowStart == -1 || testDocColNum == -1 || namespace == null) {
			throw new RuntimeException("Required TestDocDOMBuilder properties not initialized");
		}
		
		document = createDocument();
		Node rootDOMElement = buildDOMTreeFromElementInst(modelInst.getRootElement(), true);
		document.appendChild(rootDOMElement);
		return document;
	}
	
	private Element buildDOMTreeFromElementInst(ElementNodeInst elementInst, boolean isRoot) {
		int rowNum = nodeRowStart + elementInst.getRowOffset();
		String value = sheet.getCellValue(rowNum, testDocColNum).trim();
		boolean hasEmptyTag = value.startsWith(BookUtil.EMPTY_TAG);
		value = hasEmptyTag ? "" : value;
		
		if (elementInst.hasChild() && value.length() > 0) {
			parentCannotHaveValueWarning(elementInst, rowNum, value);
		}

		List<Attr> childAttribs = buildChildAttribs(elementInst);
		List<Element> childElements = buildChildElements(elementInst);
		
		Element element = null;
		if (isRoot || hasEmptyTag || value.length() > 0 || 
				childAttribs.size() > 0 || childElements.size() > 0) {
			
			element = createElement(elementInst);
			addChildAttribs(element, childAttribs);
			addChildElementsOrElementValue(element, childElements, value);
		}
		return element;
	}

	private Element createElement(ElementNodeInst elementInst) {
		Element element = document.createElementNS(namespace, elementInst.getName());
		element.setUserData(BookUtil.DOM_USER_DATA_RELATED_TDC_NODE, elementInst, null);
		return element;
	}

	private void addChildAttribs(Element element, List<Attr> childAttribs) {
		for (Attr attrib : childAttribs) {
			element.setAttributeNodeNS(attrib);
		}
	}
	
	private void addChildElementsOrElementValue(Element element, List<Element> childElements, String value) {
		if (childElements.size() > 0) {
			for (Element childElement : childElements) {
				element.appendChild(childElement);
			}
		}
		else if (value.length() > 0) { 
			element.appendChild(document.createTextNode(value));
		}
	}

	private List<Element> buildDOMTreeFromCompositorInst(CompositorNodeInst compositorInst) {
		int rowNum = nodeRowStart + compositorInst.getRowOffset();
		String value = sheet.getCellValue(rowNum, testDocColNum).trim();
		if (value.length() > 0) {
			compositorCannotHaveValueWarning(compositorInst, rowNum, value);
		}
		return buildChildElements(compositorInst);
	}
	
	private List<Attr> buildChildAttribs(ElementNodeInst elementInst) {
		List<Attr> childAttribs = new ArrayList<>();
		for (AttribNodeInst attribInstChild : elementInst.getAttributes()) {
			int rowNum = nodeRowStart + attribInstChild.getRowOffset();
			String value = sheet.getCellValue(rowNum, testDocColNum).trim();
			boolean hasEmptyTag = value.startsWith(BookUtil.EMPTY_TAG);
			value = hasEmptyTag ? "" : value;
			
			Attr attr = null;
			if (hasEmptyTag || value.length() > 0) {
				attr = createAttr(attribInstChild, value);
				childAttribs.add(attr);
			}
		}
		return childAttribs;
	}

	private Attr createAttr(AttribNodeInst attribInstChild, String value) {
		Attr attr = document.createAttributeNS(null,  attribInstChild.getNodeDef().getName());
		attr.setUserData(BookUtil.DOM_USER_DATA_RELATED_TDC_NODE, attribInstChild, null);
		attr.setValue(value);
		return attr;
	}
	
	private List<Element> buildChildElements(NonAttribNodeInst nonAttribInst) {
		List<Element> childElements = new ArrayList<>();
		for (NonAttribNodeInst nonAttribInstChild : nonAttribInst.getChildren()) {
			if (nonAttribInstChild instanceof ElementNodeInst) {
				ElementNodeInst childElementInst = (ElementNodeInst)nonAttribInstChild;
				Element childElement = buildDOMTreeFromElementInst(childElementInst, false);
				if (childElement != null) {
					childElements.add(childElement);
				}
			}
			else if (nonAttribInstChild instanceof CompositorNodeInst) {
				CompositorNodeInst childCompositorInst = (CompositorNodeInst)nonAttribInstChild;
				List<Element> childList = buildDOMTreeFromCompositorInst(childCompositorInst);
				childElements.addAll(childList);
			}
			else {
				throw new IllegalStateException("NonAttributeNodeInstance of unknown type: " + 
						nonAttribInstChild.getClass().getName());
			}
		}
		return childElements;
	}
	
	private DocumentBuilder createDocumentBuilder() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			return dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException("Unable to create new DOM DocumentBuilder: ", e);
		}
	}
	
	private Document createDocument() {
		Document document = documentBuilder.newDocument();
		document.setXmlStandalone(true);
		return document;
	}

	private void parentCannotHaveValueWarning(
			ElementNodeInst elementInst, int rowNum, String value) {
		
		String messageStr = "Element '" + elementInst.getDispName() + 
				"' cannot have a value because it is a parent node; will be ignored";
		warning(messageStr, rowNum, value);
	}

	private void compositorCannotHaveValueWarning(
			CompositorNodeInst compositorInst, int rowNum, String value) {

		String messageStr = "Compositor '" + compositorInst.getDispName() +  
				"' cannot have a value; will be ignored";
		warning(messageStr, rowNum, value);
	}
	
	private void warning(String messageStr, int rowNum, String value) {
		if (messages != null) {
			Message message = new Message
					.Builder(TestDocMessageType.WARNING, messageStr)
					.setRowNumColNum(rowNum, testDocColNum)
					.setCellRef(testDocColLetter + rowNum)
					.setValue(value)
					.build();
			messages.addMessage(message);
		}
	}
}