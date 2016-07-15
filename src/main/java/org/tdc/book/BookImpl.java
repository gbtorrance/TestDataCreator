package org.tdc.book;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.util.Addr;

/**
 * A {@link Book} implementation.
 */
public class BookImpl implements Book {

	private static final Logger log = LoggerFactory.getLogger(BookImpl.class);
	
	private final BookConfig config;
	private final Map<String, Page> pages;
	
	private BookImpl(Builder builder) {
		this.config = builder.config;
		this.pages = Collections.unmodifiableMap(builder.pages); // unmodifiable
	}
	
	@Override
	public BookConfig getConfig() {
		return config;
	}
	
	@Override
	public Map<String, Page> getPages() {
		return pages;
	}
	
	public static class Builder {
		private final Path bookFile; 
		private final SpreadsheetFileFactory spreadsheetFileFactory; 
		private final BookConfigFactory bookConfigFactory; 
		private final ModelInstFactory modelInstFactory;

		private BookConfig config;
		private Map<String, Page> pages;
		
		public Builder(
				Path bookFile, 
				SpreadsheetFileFactory spreadsheetFileFactory, 
				BookConfigFactory bookConfigFactory, 
				ModelInstFactory modelInstFactory) {
			
			log.info("Creating Book from Book file: {}", bookFile.toAbsolutePath());
			this.bookFile = bookFile;
			this.spreadsheetFileFactory = spreadsheetFileFactory;
			this.bookConfigFactory = bookConfigFactory;
			this.modelInstFactory = modelInstFactory;
		}
		
		public Book build() {
			SpreadsheetFile spreadsheetFile = spreadsheetFileFactory.getSpreadsheetFileFromPath(bookFile);
			Addr addr = getBookAddrFromConfigSheet(bookFile, spreadsheetFile);
			config = bookConfigFactory.getBookConfig(addr);
			pages = new PageImpl.Builder(config.getPageConfigs(), modelInstFactory, spreadsheetFile).buildAll();

			// build TestSets (which, in turn, will build TestCases)
			
			// TODO where to verify?
			
			return new BookImpl(this);
		}
		
		private Addr getBookAddrFromConfigSheet(Path bookFile, SpreadsheetFile spreadsheetFile) {
			Spreadsheet sheet = spreadsheetFile.getSpreadsheet(BookUtil.CONFIG_SHEET_NAME);
			if (sheet == null) {
				throw new RuntimeException("Configuration worksheet '" + BookUtil.CONFIG_SHEET_NAME + "' does not exist");
			}
			String addrStr = sheet.getCellValue(BookUtil.CONFIG_SHEET_BOOK_ADDR_ROW, BookUtil.CONFIG_SHEET_BOOK_ADDR_COL).trim();
			if (addrStr.equals("")) {
				throw new RuntimeException("Configuration worksheet '" + 
						BookUtil.CONFIG_SHEET_NAME + "' must contain a Book address in row " + 
						BookUtil.CONFIG_SHEET_BOOK_ADDR_ROW + ", column " + BookUtil.CONFIG_SHEET_BOOK_ADDR_COL);
			}
			return new Addr(addrStr);
		}
	}
}
