package org.tdc.spreadsheet.excel;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
		return new ExcelSpreadsheetFile.Builder(workbook).build();
	}

	@Override
	public SpreadsheetFile getSpreadsheetFileFromPath(Path path) {
		try {
			// loading from a file rather than a stream, as less memory is used that way
			Workbook workbook = WorkbookFactory.create(path.toFile());
			if (!(workbook instanceof XSSFWorkbook)) {
				throw new RuntimeException(
						"Only XML-based Excel workbooks are supported (.XLSX, .XLSM): " + path.toString());
			}
			return new ExcelSpreadsheetFile.Builder((XSSFWorkbook)workbook).build();
		}
		catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			throw new RuntimeException("Unable to read Excel file: " + path.toString(), e);
		}
	}
}
