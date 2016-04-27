package org.tdc.spreadsheet.excel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.spreadsheet.Spreadsheet;

/**
 * A {@link Spreadsheet} implementation for Excel worksheets.
 * 
 * <p>Note that row and column index values are 1-based. 
 * (Internally, though, the Apache POI libraries use 0-based indexes.)
 */
public class ExcelSpreadsheet implements Spreadsheet {
	
	private static final Logger log = LoggerFactory.getLogger(ExcelSpreadsheet.class);

	private XSSFSheet xssfSheet;
	
	public ExcelSpreadsheet(XSSFSheet xssfSheet) {
		this.xssfSheet = xssfSheet;
	}
	
	@Override 
	public String getCellValue(int rowNum, int colNum) {
		XSSFCell cell = getCell(rowNum, colNum);
		return getCellValue(cell);
	}

	@Override
	public void setCellValue(String value, int rowNum, int colNum) {
		XSSFCell cell = getCell(rowNum, colNum);
		cell.setCellValue(value);
	}
	
	private String getCellValue(XSSFCell cell) {
		return getCellValueByType(cell, cell.getCellType());
	}
	
	private XSSFCell getCell(int rowNum, int colNum) {
		XSSFRow row = getRow(rowNum);
		XSSFCell cell = getCell(row, colNum);
		return cell;
	}
	
	private XSSFCell getCell(XSSFRow row, int colNum) {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)

		XSSFCell cell = row.getCell(colNum-1);
		if (cell == null) {
			cell = row.createCell(colNum-1);
		}
		return cell;
	}
	
	private XSSFRow getRow(int rowNum) {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)
		
		XSSFRow row = xssfSheet.getRow(rowNum-1);
		if (row == null) {
			row = xssfSheet.createRow(rowNum-1);
		}
		return row;
	}
	
	private String getCellValueByType(XSSFCell cell, int cellType) {
		String value = null;
		switch (cellType) {
			case XSSFCell.CELL_TYPE_BLANK:
				value = "";
				break;
			case XSSFCell.CELL_TYPE_BOOLEAN:
				value = "" + cell.getBooleanCellValue();
				break;
			case XSSFCell.CELL_TYPE_ERROR:
				// TODO better way to handle this?
				throw new UnsupportedOperationException("Currently do not support errors [sheet: " + cell.getSheet().getSheetName() + ", row: " + (cell.getRowIndex()+1) + ", col: " + (cell.getColumnIndex()+1) + "]");
			case XSSFCell.CELL_TYPE_FORMULA:
				value = getCellValueByType(cell, cell.getCachedFormulaResultType());
				break;
			case XSSFCell.CELL_TYPE_NUMERIC:
				value = cell.getRawValue();
				break;
			case XSSFCell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
				break;
			default:
				throw new IllegalStateException("Cell type " + cell.getCellType() + " is unknown");
		}
		return value;
	}
}
