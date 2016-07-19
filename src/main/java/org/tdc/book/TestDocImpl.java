package org.tdc.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tdc.config.book.DocIDRowConfig;
import org.tdc.config.book.PageConfig;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * A {@link TestDoc} implementation.
 */
public class TestDocImpl implements TestDoc {
	private final PageConfig pageConfig;
	private final int colNum;
	private final int caseNum;
	private final String setName;
	private final Map<String, String> docVariables;
	
	private TestDocImpl(Builder builder) {
		this.pageConfig = builder.pageConfig;
		this.colNum = builder.colNum;
		this.caseNum = builder.caseNum;
		this.setName = builder.setName;
		this.docVariables = Collections.unmodifiableMap(builder.docVariables); // unmodifiable 
	}
	
	@Override
	public PageConfig getPageConfig() {
		return pageConfig;
	}

	@Override
	public int getColNum() {
		return colNum;
	}

	@Override
	public int getCaseNum() {
		return caseNum;
	}

	@Override
	public String getSetName() {
		return setName;
	}

	@Override
	public Map<String, String> getDocVariables() {
		return docVariables;
	}
	
	public static class Builder {
		private final PageConfig pageConfig;
		private final SpreadsheetFile spreadsheetFile;
		
		private int colNum;
		private int caseNum;
		private String setName;
		private Map<String, String> docVariables;
		
		public Builder(PageConfig pageConfig, SpreadsheetFile spreadsheetFile) {
			this.pageConfig = pageConfig;
			this.spreadsheetFile = spreadsheetFile;
		}
		
		public List<TestDoc> buildAll() {
			List<TestDoc> testDocs = new ArrayList<>();
			Spreadsheet sheet = spreadsheetFile.getSpreadsheet(pageConfig.getPageName());
			if (sheet != null) {
				int caseNumRowNum = pageConfig.getCaseNumDocIDRowConfig().getRowNum();
				int startCol = pageConfig.getTestDocColStart();
				int endCol = sheet.getLastColNum(caseNumRowNum);
				for (int colNum = startCol; colNum <= endCol; colNum++) {
					String caseNum = sheet.getCellValue(caseNumRowNum, colNum);
					// if case num specified, consider this a valid test doc; otherwise ignore
					if (caseNum.trim().length() > 0) {
						TestDoc testDoc = build(sheet, colNum);
						testDocs.add(testDoc);
					}
				}
			}
			return testDocs;
		}
		
		private TestDoc build(Spreadsheet sheet, int colNum) {
			this.colNum = colNum;
			String caseNumStr = sheet.getCellValue(pageConfig.getCaseNumDocIDRowConfig().getRowNum(), colNum);
			try {
				caseNum = Integer.parseUnsignedInt(caseNumStr);
			}
			catch (NumberFormatException ex) {
				throw new RuntimeException("Case Num must be a positive number for page '" + 
						sheet.getName() + "' column " + colNum, ex);
			}
			DocIDRowConfig setNameConfig = pageConfig.getSetNameDocIDRowConfig();
			setName = setNameConfig == null ? "" : sheet.getCellValue(setNameConfig.getRowNum(), colNum);
			docVariables = new HashMap<>();
			List<DocIDRowConfig> varConfigs = pageConfig.getVarDocIDRowConfigs();
			for (DocIDRowConfig var : varConfigs) {
				String varName = var.getDocVariableName();
				String value = sheet.getCellValue(var.getRowNum(), colNum).trim();
				docVariables.put(varName, value);
			}
			return new TestDocImpl(this);
		}
	}
}
