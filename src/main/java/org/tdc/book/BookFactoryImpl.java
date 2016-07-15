package org.tdc.book;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.modelinst.ModelInst;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.spreadsheet.SpreadsheetFileFactory;

/**
 * A {@link BookFactory} implementation.
 * 
 * <p>Creates {@link ModelInst} instances, as necessary, using provided {@link ModelInstFactory}.
 */
public class BookFactoryImpl implements BookFactory {
	
	private static final Logger log = LoggerFactory.getLogger(BookFactoryImpl.class);

	private final BookConfigFactory bookConfigFactory;
	private final ModelInstFactory modelInstFactory;
	private final SpreadsheetFileFactory spreadsheetFileFactory;
	
	public BookFactoryImpl(
			BookConfigFactory bookConfigFactory, 
			ModelInstFactory modelInstFactory, 
			SpreadsheetFileFactory spreadsheetFileFactory) {
		
		this.bookConfigFactory = bookConfigFactory;
		this.modelInstFactory = modelInstFactory;
		this.spreadsheetFileFactory = spreadsheetFileFactory;
	}

	@Override
	public Book getBook(Path bookFile) {
		return new BookImpl.Builder(
				bookFile, spreadsheetFileFactory, 
				bookConfigFactory, modelInstFactory).build();
	}
}
