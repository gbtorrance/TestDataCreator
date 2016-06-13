package org.tdc.book;

import java.util.Collection;

import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * A {@link BookWriter} implementation.
 */
public class BookWriterImpl implements BookWriter {
	
	private final Book book;
	
	public BookWriterImpl(Book book) {
		this.book = book;
	}

	@Override
	public void write(SpreadsheetFile spreadsheetFile) {
		Collection<Page> pages = book.getPages().values();
		for (Page page : pages) {
			writePage(spreadsheetFile, page);
		}
	}
	
	private void writePage(SpreadsheetFile spreadsheetFile, Page page) {
		Spreadsheet sheet = spreadsheetFile.createSpreadsheet(page.getName());
		
		// TODO write to spreadsheet
		sheet.setCellValue("Need to populate this worksheet with initial Page contents", 1, 1);
	}
}
