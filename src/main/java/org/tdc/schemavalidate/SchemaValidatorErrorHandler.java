package org.tdc.schemavalidate;

import javax.xml.validation.Validator;

import org.tdc.book.BookUtil;
import org.tdc.book.TestDoc;
import org.tdc.message.Message;
import org.tdc.message.TestDocMessageType;
import org.tdc.modelinst.NodeInst;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * {@link ErrorHandler} implementation that collects {@link Message}s 
 * during the validation of a {@link TestDoc}.  
 */
class SchemaValidatorErrorHandler implements ErrorHandler {
	
	private static final String CURRENT_ELEMENT_PROPERTY = "http://apache.org/xml/properties/dom/current-element-node";
	
	private final Validator validator;
	private final TestDoc testDoc;
	
	public SchemaValidatorErrorHandler(Validator validator, TestDoc testDoc) {
		this.validator = validator;
		this.testDoc = testDoc;
	}
	
	@Override
	public void error(SAXParseException ex) throws SAXException {
		logParseException(ex, TestDocMessageType.SCHEMA_ERROR);
	}

	@Override
	public void fatalError(SAXParseException ex) throws SAXException {
		logParseException(ex, TestDocMessageType.SCHEMA_FATAL_ERROR);
	}

	@Override
	public void warning(SAXParseException ex) throws SAXException {
		logParseException(ex, TestDocMessageType.SCHEMA_WARNING);
	}
	
	private void logParseException(SAXParseException ex, TestDocMessageType type) {
		try {
			Element currentElement = (Element)validator.getProperty(CURRENT_ELEMENT_PROPERTY);
			NodeInst currentNodeInst = (NodeInst)currentElement.getUserData(BookUtil.DOM_USER_DATA_RELATED_TDC_NODE);
			int rowNum = testDoc.getPageConfig().getNodeRowStart() + currentNodeInst.getRowOffset();
			int colNum = testDoc.getColNum();
			String cellRef = testDoc.getColLetter() + rowNum;
			Message message = 
					new Message.Builder(type, ex.getMessage())
					.setRowNumColNum(rowNum, colNum)
					.setCellRef(cellRef)
					.setValue(currentElement.getNodeValue())
					.build();
			testDoc.getMessages().addMessage(message);
		}
		catch (SAXNotRecognizedException | SAXNotSupportedException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
