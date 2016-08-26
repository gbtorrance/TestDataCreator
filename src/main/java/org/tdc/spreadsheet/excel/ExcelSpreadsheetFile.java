package org.tdc.spreadsheet.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.util.Util;

/**
 * A {@link SpreadsheetFile} implementation for Excel workbooks.
 */
public class ExcelSpreadsheetFile implements SpreadsheetFile {

	private final XSSFWorkbook workbook;
	private final Map<String, Spreadsheet> spreadsheets;

	private ExcelSpreadsheetFile(Builder builder) {
		this.workbook = builder.workbook;
		this.spreadsheets = builder.spreadsheets;
	}

	@Override
	public Spreadsheet getSpreadsheet(String name) {
		return spreadsheets.get(name);
	}

	@Override
	public Spreadsheet createSpreadsheet(String name) {
		Spreadsheet spreadsheet = new ExcelSpreadsheet.Builder(workbook).build(name);
		spreadsheets.put(spreadsheet.getName(), spreadsheet);
		return spreadsheet;
	}

	@Override
	public void save(Path path) {
		try (FileOutputStream fileOut = new FileOutputStream(path.toFile())) {
			workbook.write(fileOut);
			workbook.close();
		}
		catch (Exception ex) {
			throw new RuntimeException("Unable to save Excel file: " + path.toString(), ex);
		}
	}

	@Override
	public void saveAsNew(Path path) {
		if (Files.exists(path)) {
			throw new RuntimeException("Unable to save Excel file; already exists: " + path.toString());
		}
		save(path);
	}

	@Override
	public void close() {
		try {
			workbook.close();
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to close ExcelSpreadsheetFile", e);
		}

	}

	@Override
	public void setSpreadsheetHidden(String name, boolean hide) {
		int index = workbook.getSheetIndex(name);
		if (index == Util.UNDEFINED) {
			throw new IllegalStateException("Unable to hide worksheet '" + name + "'; does not exist");
		}
		workbook.setSheetHidden(index, hide);
	}

	public static class Builder {
		private final XSSFWorkbook workbook;

		private Map<String, Spreadsheet> spreadsheets;

		public Builder(XSSFWorkbook workbook) {
			this.workbook = workbook;
		}

		public SpreadsheetFile build() {
			spreadsheets = new ExcelSpreadsheet.Builder(workbook).buildAll();
			return new ExcelSpreadsheetFile(this);
		}
	}
}
