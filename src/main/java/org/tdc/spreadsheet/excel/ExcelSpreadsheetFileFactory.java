package org.tdc.spreadsheet.excel;

import java.io.FileInputStream;
import java.nio.file.Path;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;

/**
 * A {@link SpreadsheetFileFactory} implementation.
 */
public class ExcelSpreadsheetFileFactory implements SpreadsheetFileFactory {

	@Override
	public SpreadsheetFile getSpreadsheetFile() {
		XSSFWorkbook workbook = new XSSFWorkbook();
		return new ExcelSpreadsheetFile.SpreadsheetFileBuilder(workbook).build();
	}

	@Override
	public SpreadsheetFile getSpreadsheetFileFromPath(Path path) {
		try (FileInputStream fis = new FileInputStream(path.toFile())) {
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fis);
			return new ExcelSpreadsheetFile.SpreadsheetFileBuilder(xssfWorkbook).build();
		}
		catch (Exception ex) {
			throw new RuntimeException("Unable to read Excel file: " + path.toString(), ex);
		}
	}
}
