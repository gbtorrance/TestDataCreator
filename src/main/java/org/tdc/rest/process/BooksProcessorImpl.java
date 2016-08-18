package org.tdc.rest.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.server.ServerConfig;

/**
 * Implementation of {@link BooksProcessor}.
 */
public class BooksProcessorImpl implements BooksProcessor {
	private static final Logger log = LoggerFactory.getLogger(BooksProcessorImpl.class);
	private static final DateTimeFormatter YEAR_PLUS_DAY_OF_YEAR_FORMATTER = 
			DateTimeFormatter.ofPattern("yyDDD"); // e.g. 1/24/2016 = "16024"
	private static final String BOOK_FILE_NAME = "Book.xlsx"; // TODO use correct file extension; might be xlsm
	
	private final ServerConfig serverConfig;
	
	private int currentYearPlusDay;
	private int seq;

	public BooksProcessorImpl(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		ensureBooksWorkingRootExists();
	}

	@Override
	public String uploadBookFile(File bookFile) {
		String bookID = createNewBookIDAndDir();
		Path bookWorkingRoot = serverConfig.getBooksWorkingRoot().resolve(bookID);
		try (FileInputStream bookInput = new FileInputStream(bookFile)) {
			Path target = bookWorkingRoot.resolve(BOOK_FILE_NAME);
			Files.copy(bookInput, target, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to save uploaded file", e);
		}
		return bookID;
	}
	
	/**
	 * Attempts to find an unused Book ID, and when one is found, 
	 * a corresponding directory will be created in which book-specific
	 * files can be stored. 
	 */
	private synchronized String createNewBookIDAndDir() {
		boolean success = false;
		String bookID = null;
		while (!success) {
			bookID = getTrialBookID();
			Path bookWorkingRoot = serverConfig.getBooksWorkingRoot().resolve(bookID);
			if (!Files.exists(bookWorkingRoot)) {
				try {
					Files.createDirectory(bookWorkingRoot);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				success = true;
			}
		}
		return bookID;
	}
	
	/**
	 * Returns a 6 letter ID composed of 3 letters for the base 36 conversion of 
	 * a 2-digit year / 3-digit day-of-year combination followed by 3 letters for
	 * the base 36 conversion of a 5-digit sequence number starting at 10000 each day.
	 * 
	 * <p>Since the daily sequence number is re-initialized whenever the system restarts,
	 * it is possible that the ID returned will already have been used, in which case 
	 * this method should be called again until an unused ID is found. 
	 */
	private String getTrialBookID() {
		int yearPlusDay = Integer.valueOf(
				LocalDateTime.now().format(YEAR_PLUS_DAY_OF_YEAR_FORMATTER));
		if (yearPlusDay != currentYearPlusDay) {
			currentYearPlusDay = yearPlusDay;
			if (currentYearPlusDay < 02001 || currentYearPlusDay > 46365) {
				// to ensure 3-letter base 64 conversion 
				throw new RuntimeException("Current day is outside of supported range: 1/1/2002 to 12/31/2046");
			}
			seq = 10000; // start here to guarantee 3-letter seq after conversion to base 36
		}
		else {
			seq++;
		}
		String bookID = convertToBase36(currentYearPlusDay) + convertToBase36(seq);
		return bookID;
	}
	
	/**
	 * Converts an integer to its equivalent in base-36 (i.e. range 1-9 and a-z).
	 */
	private String convertToBase36(int convert) {
		return Integer.toString(convert, 36);
	}
	
	private void ensureBooksWorkingRootExists() {
		if (!Files.isDirectory(serverConfig.getBooksWorkingRoot())) {
			try {
				Files.createDirectory(serverConfig.getBooksWorkingRoot());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
