package org.tdc.book;

import java.util.ArrayList;
import java.util.List;

import org.tdc.config.book.PageConfig;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * A {@link TestDoc} implementation.
 */
public class TestDocImpl implements TestDoc {
	private final PageConfig pageConfig;
	private final int colNum;
	
	private TestDocImpl(Builder builder) {
		this.pageConfig = builder.pageConfig;
		this.colNum = builder.colNum;
	}

	public static class Builder {
		private final PageConfig pageConfig;
		private final SpreadsheetFile spreadsheetFile;
		
		private int colNum;
		
		public Builder(PageConfig pageConfig, SpreadsheetFile spreadsheetFile) {
			this.pageConfig = pageConfig;
			this.spreadsheetFile = spreadsheetFile;
		}
		
		public List<TestDoc> buildAll() {
			List<TestDoc> testDocs = new ArrayList<>();
			Spreadsheet sheet = spreadsheetFile.getSpreadsheet(pageConfig.getPageName());
			if (sheet != null) {
				// TODO iterate through all test case columns ...
				// add "base row" to PageConfig
				// build(...)
			}
			return testDocs;
		}
		
		private TestDoc build(Spreadsheet sheet, int colNum) {
			this.colNum = colNum;
			
			// TODO implement
			
			return new TestDocImpl(this);
		}
	}
}
