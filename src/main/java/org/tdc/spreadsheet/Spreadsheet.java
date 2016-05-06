package org.tdc.spreadsheet;

/**
 * Defines core functionality for working with an individual spreadsheet.
 * 
 * <p>Does not support multiple spreadsheet pages/tabs -- just a single sheet.
 * Supporting multiple pages/tabs is outside the scope of this interface.
 * 
 * <p>Note that indexes for this interface are 1-based.
 */
public interface Spreadsheet {
	
	/**
	 * Returns the string value for a particular cell.
	 * 
	 * @param rowNum Row number for cell value to retrieve (1-based index)
	 * @param colNum Column number for cell value to retrieve (1-based index)
	 * @return String value
	 */
	String getCellValue(int rowNum, int colNum);

	/**
	 * Sets the string value for a particular cell.
	 *
	 * @param value String value for cell
	 * @param rowNum Row number for cell value to retrieve (1-based index)
	 * @param colNum Column number for cell value to retrieve (1-based index)
	 */
	void setCellValue(String value, int rowNum, int colNum);

	/**
	 * Sets the string value for a particular cell (with optional formatting).
	 *
	 * @param value String value for cell
	 * @param rowNum Row number for cell value to retrieve (1-based index)
	 * @param colNum Column number for cell value to retrieve (1-based index)
	 * @param cellStyle Desired formatting for cell; null = use default (if specified)
	 */
	void setCellValue(String value, int rowNum, int colNum, CellStyle cellStyle);
	
	/**
	 * Set default cell style to use when not is specified
	 * 
	 * @param cellStyle Default CellStyle or null
	 */
	void setDefaultCellStyle(CellStyle cellStyle);
	
	/**
	 * Set column width in units of 1/256 the width of a character (see Apache POI docs for further details).
	 * 
	 * @see <a href="https://poi.apache.org/apidocs/org/apache/poi/xssf/usermodel/XSSFSheet.html#setColumnWidth%28int,%20int%29">Apache POI XSSFSheet.setColumnWidth()</a>
	 *
	 * @param colNum Column number for which to set width (1-based index)
	 * @param colWidth Column width in units of 1/256 the width of a character
	 */
	void setColumnWidth(int colNum, int colWidth);
}
