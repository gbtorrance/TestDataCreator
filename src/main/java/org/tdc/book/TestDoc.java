package org.tdc.book;

import java.util.Map;

import org.tdc.config.book.BookConfig;
import org.tdc.config.book.DocIDRowConfig;
import org.tdc.config.book.PageConfig;
import org.tdc.message.TestDocMessages;
import org.w3c.dom.Document;

/**
 * Defines core functionality for a Test Doc.
 * 
 * <p> A TestDoc represents a single column of test data on a {@link Page}.
 * 
 * <p>TestDocs can contain Doc Variables if support for these is defined using
 * {@link DocIDRowConfig} entries in the {@link BookConfig} file 
 */
public interface TestDoc {
	PageConfig getPageConfig();
	int getColNum();
	String getColLetter(); 
	int getCaseNum();
	String getSetName();
	Map<String, String> getDocVariables();
	Document getDOMDocument();
	void setDOMDocument(Document domDocument);
	TestDocMessages getMessages();
}
