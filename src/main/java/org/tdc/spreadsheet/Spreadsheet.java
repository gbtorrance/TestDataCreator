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
}
