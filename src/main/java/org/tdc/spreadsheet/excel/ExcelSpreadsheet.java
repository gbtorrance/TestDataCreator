package org.tdc.spreadsheet.excel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.Spreadsheet;

/**
 * A {@link Spreadsheet} implementation for Excel worksheets.
 * 
 * <p>Multiple ExcelSpreadsheets are typically part of an {@link ExcelSpreadsheetFile}.
 * 
 * <p>Note that row and column index values are 1-based. 
 * (Internally, though, the Apache POI libraries use 0-based indexes.)
 */
public class ExcelSpreadsheet implements Spreadsheet {
	
	private static final Logger log = LoggerFactory.getLogger(ExcelSpreadsheet.class);

	private final XSSFSheet xssfSheet;
	
	private POICellStyleLookup poiCellStyleLookup = new POICellStyleLookup();
	
	private ExcelSpreadsheet(SpreadsheetBuilder builder) {
		this.xssfSheet = builder.xssfSheet;
	}
	
	@Override
	public String getName() {
		return xssfSheet.getSheetName();
	}
	
	@Override 
	public String getCellValue(int rowNum, int colNum) {
		XSSFCell cell = getCell(rowNum, colNum);
		return getCellValue(cell);
	}

	@Override
	public void setCellValue(String value, int rowNum, int colNum) {
		setCellValue(value, rowNum, colNum, null);
	}
	
	@Override
	public void setCellValue(String value, int rowNum, int colNum, CellStyle cellStyle) {
		XSSFCell cell = getCell(rowNum, colNum);
		cell.setCellValue(value);
		if (cellStyle != null) {
			cell.setCellStyle(poiCellStyleLookup.getPOICellStyle(cellStyle));
		}
		else if (poiCellStyleLookup.hasDefaultPOICellStyle()) {
			cell.setCellStyle(poiCellStyleLookup.getDefaultPOICellStyle());
		}
	}
	
	@Override
	public void setDefaultCellStyle(CellStyle cellStyle) {
		this.poiCellStyleLookup.setDefaultCellStyle(cellStyle);
	}

	@Override
	public void setColumnWidth(int colNum, int colWidth) {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)
		
		xssfSheet.setColumnWidth(colNum-1, colWidth);
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
	
	/**
	 * A cache/factory for creating and looking up POI-based CellStyle objects (i.e. specific to Excel files) using
	 * TDC-based CellStyle objects (i.e. generic).  
	 */
	private class POICellStyleLookup {
		private Map<CellStyle, org.apache.poi.ss.usermodel.CellStyle> map = new HashMap<>();
		private CellStyle defaultCellStyle;
		private org.apache.poi.ss.usermodel.CellStyle defaultPOICellStyle;
		
		public org.apache.poi.ss.usermodel.CellStyle getPOICellStyle(CellStyle cellStyle) {
			org.apache.poi.ss.usermodel.CellStyle poiStyle;
			poiStyle = map.get(cellStyle);
			if (poiStyle == null) {
				poiStyle = createPOICellStyleFromCellStyle(cellStyle);
				map.put(cellStyle, poiStyle);
			}
			return poiStyle;
		}
		
		public org.apache.poi.ss.usermodel.CellStyle getDefaultPOICellStyle() {
			return defaultPOICellStyle;
		}
		
		public boolean hasDefaultPOICellStyle() {
			return defaultPOICellStyle != null;
		}
		
		public void setDefaultCellStyle(CellStyle cellStyle) {
			this.defaultCellStyle = cellStyle;
			this.defaultPOICellStyle = (cellStyle == null ? null : getPOICellStyle(defaultCellStyle));
		}

		private org.apache.poi.ss.usermodel.CellStyle createPOICellStyleFromCellStyle(CellStyle cellStyle) {
			XSSFWorkbook workbook = ExcelSpreadsheet.this.xssfSheet.getWorkbook();
			XSSFFont font = workbook.createFont();
			font.setFontName(cellStyle.getFontName());
			font.setFontHeight(cellStyle.getFontHeight());
			font.setColor(new XSSFColor(cellStyle.getColor()));
			font.setItalic(cellStyle.getItalic());
			org.apache.poi.ss.usermodel.CellStyle poiCellStyle = workbook.createCellStyle();
			poiCellStyle.setFont(font);
			return poiCellStyle;
		}
	}
	
	public static class SpreadsheetBuilder {
		private final XSSFWorkbook workbook;
		
		private XSSFSheet xssfSheet;
		
		public SpreadsheetBuilder(XSSFWorkbook workbook) {
			this.workbook = workbook;
		}
		
		public Spreadsheet build(String name) {
			if (workbook.getSheet(name) != null) {
				throw new IllegalStateException("An Excel file with the name '" + name + "' already exists");
			}
			return build(workbook.createSheet(name));
		}
		
		public Map<String, Spreadsheet> buildAll() {
			Map<String, Spreadsheet> spreadsheets = new LinkedHashMap<>();
			int sheetCount = workbook.getNumberOfSheets();
			for (int i = 0; i < sheetCount; i++) {
				Spreadsheet spreadsheet = build(workbook.getSheetAt(i));
				spreadsheets.put(spreadsheet.getName(), spreadsheet);
			}
			return spreadsheets;
		}
		
		private Spreadsheet build(XSSFSheet xssfSheet) {
			this.xssfSheet = xssfSheet;
			return new ExcelSpreadsheet(this);
		}
	}
}
