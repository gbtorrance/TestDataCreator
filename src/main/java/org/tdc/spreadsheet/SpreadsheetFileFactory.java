package org.tdc.spreadsheet;

import java.nio.file.Path;

/**
 * Factory for creating {@link SpreadsheetFileFactory} instances.
 */
public interface SpreadsheetFileFactory {
	SpreadsheetFile getSpreadsheetFile();
	SpreadsheetFile getSpreadsheetFileFromPath(Path path);
}
