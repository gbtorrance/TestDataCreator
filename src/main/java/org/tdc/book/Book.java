package org.tdc.book;

import java.util.Map;

import org.tdc.config.book.BookConfig;
import org.tdc.modelinst.ModelInst;

/**
 * Defines the core functionality for a Book. 
 * 
 * <p>A Book is conceptually mapped to a spreadsheet "workbook", and is
 * used for representing and storing test cases composed of one or more
 * test documents (each of which is structurally defined by a schema/model).
 * 
 * <p>A Book is composed of one or more {@link Page}s, and each {@link Page} 
 * is defined by a particular {@link ModelInst}.
 */
public interface Book {
	BookConfig getConfig();
	Map<String, Page> getPages();
}
