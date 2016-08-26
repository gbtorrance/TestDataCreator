package org.tdc.rest.process;

import java.io.File;

import org.tdc.rest.dto.BookDTO;
import org.tdc.rest.resource.BooksResource;

/**
 * Defines server-specific functionality for processing Books 
 * (that is distinct from communication-specific REST functionality,
 * that will be handled by {@link BooksResource}).
 */
public interface BooksProcessor {
	String uploadBookFile(File bookFile);
	BookDTO getBookInfo(String bookID);
}
