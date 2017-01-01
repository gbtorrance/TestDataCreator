package org.tdc.shared.util;

import java.time.format.DateTimeFormatter;

/**
 * Various shared constants.
 */
public class SharedConst {
	public static final DateTimeFormatter DATE_TIME_FORMATTER = 
			DateTimeFormatter.ofPattern("yyMMdd_kkmmss_SSS");
	
	public static final int UNDEFINED = -1;
	public static final int NO_LIMIT = -1;
}
