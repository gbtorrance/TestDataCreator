package org.tdc.shared.util;

import java.time.format.DateTimeFormatter;

/**
 * Various shared constants.
 */
public class SharedConst {
	public static final DateTimeFormatter DT_FORMAT_SORTABLE = 
			DateTimeFormatter.ofPattern("yyMMdd_kkmmss_SSS");
	public static final DateTimeFormatter DT_FORMAT_USER = 
			DateTimeFormatter.ofPattern("MM/dd/yyyy kk:mm:ss");
	
	public static final int UNDEFINED = -1;
	public static final int NO_LIMIT = -1;
}
