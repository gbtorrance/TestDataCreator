package org.tdc.book;

import java.util.Map;

import org.tdc.config.book.BookConfig;
import org.tdc.config.book.DocIDRowConfig;
import org.tdc.config.book.PageConfig;

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
	int getCaseNum();
	String getSetName();
	Map<String, String> getDocVariables();
}
