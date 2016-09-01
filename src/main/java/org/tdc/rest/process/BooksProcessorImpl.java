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
import org.tdc.book.Book;
import org.tdc.book.BookFactory;
import org.tdc.book.BookSchemaValidator;
import org.tdc.book.BookTestDataLoader;
import org.tdc.config.server.ServerConfig;
import org.tdc.rest.dto.BookDTO;
import org.tdc.rest.mapper.BookMapper;
import org.tdc.schemavalidate.SchemaValidatorFactory;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.util.Cache;
import org.tdc.util.LRUCache;

/**
 * Implementation of {@link BooksProcessor}.
 */
public class BooksProcessorImpl implements BooksProcessor {
	private static final Logger log = LoggerFactory.getLogger(BooksProcessorImpl.class);
	private static final DateTimeFormatter YEAR_PLUS_DAY_OF_YEAR_FORMATTER = 
			DateTimeFormatter.ofPattern("yyDDD"); // e.g. 1/24/2016 = "16024"
	private static final String BOOK_FILE_NAME = "Book.xlsx"; // TODO use correct file extension; might be xlsm
	
	private final ServerConfig serverConfig;
	private final BookFactory bookFactory;
	private final SpreadsheetFileFactory spreadsheetFileFactory;
	private final SchemaValidatorFactory schemaValidatorFactory;
	private final Cache<String,Book> bookCache;
	
	private int currentYearPlusDay;
	private int seq;

	private BooksProcessorImpl(Builder builder) {
		this.serverConfig = builder.serverConfig;
		this.bookFactory = builder.bookFactory;
		this.spreadsheetFileFactory = builder.spreadsheetFileFactory;
		this.schemaValidatorFactory = builder.schemaValidatorFactory;
		this.bookCache = builder.bookCache;
	}

	@Override
	public String uploadBookFile(File bookFile) {
		String bookID = createNewBookIDAndWorkingDir();
		saveBookFileToLocalBookWorkingDir(bookFile, bookID);
		// don't need Book object now; just ensure it loads correctly and cache for future
		getBook(bookID);
		return bookID;
	}
	
	@Override
	public BookDTO getBookInfo(String bookID) {
		Book book = getBook(bookID);
		synchronized(book) {
			return BookMapper.INSTANCE.bookToBookDTO(book);
		}
	}
	
	private String createNewBookIDAndWorkingDir() {
		synchronized(serverConfig.getBooksWorkingRoot()) {
			while (true) {
				String bookID = getTrialBookID();
				Path bookWorkingRoot = serverConfig.getBooksWorkingRoot().resolve(bookID);
				if (!Files.exists(bookWorkingRoot)) {
					try {
						Files.createDirectory(bookWorkingRoot);
						return bookID;
					} 
					catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
	
	private void saveBookFileToLocalBookWorkingDir(File bookFile, String bookID) {
		Path bookWorkingRoot = serverConfig.getBooksWorkingRoot().resolve(bookID);
		try (FileInputStream bookInput = new FileInputStream(bookFile)) {
			Path target = bookWorkingRoot.resolve(BOOK_FILE_NAME);
			Files.copy(bookInput, target, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to save uploaded file", e);
		}
	}

	private Book getBook(String bookID) {
		synchronized(bookCache) {
			Book book = bookCache.get(bookID);
			if (book == null) {
				book = loadBook(bookID);
				bookCache.put(bookID, book);
			}
			return book;
		}
	}

	private Book loadBook(String bookID) {
		Path spreadsheetPath = getSpreadsheetFilePath(bookID);
		SpreadsheetFile spreadsheetFile = 
				spreadsheetFileFactory.createReadOnlySpreadsheetFileFromPath(spreadsheetPath);
		Book book = bookFactory.getBook(spreadsheetFile);
		BookTestDataLoader loader = new BookTestDataLoader(book, spreadsheetFile);
		loader.loadTestData();
		// TODO possibly move validation elsewhere; leave it here for now
		schemaValidateBook(book);
		return book;
	}
	
	private void schemaValidateBook(Book book) {
		BookSchemaValidator validator = new BookSchemaValidator(book, schemaValidatorFactory);
		validator.validate();
	}

	private Path getSpreadsheetFilePath(String bookID) {
		Path spreadsheetPath = serverConfig.getBooksWorkingRoot()
				.resolve(bookID)
				.resolve(BOOK_FILE_NAME);
		if (!Files.exists(spreadsheetPath)) {
			throw new RuntimeException("Unable to locate spreadsheet file: " + spreadsheetPath);
		}
		return spreadsheetPath;
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
	
	public static class Builder {
		private final ServerConfig serverConfig;
		private final BookFactory bookFactory;
		private final SpreadsheetFileFactory spreadsheetFileFactory;
		private final SchemaValidatorFactory schemaValidatorFactory;
		
		private Cache<String,Book> bookCache;
		
		public Builder(
				ServerConfig serverConfig, 
				BookFactory bookFactory, 
				SpreadsheetFileFactory spreadsheetFileFactory,
				SchemaValidatorFactory schemaValidatorFactory) {
			
			this.serverConfig = serverConfig;
			this.bookFactory = bookFactory;
			this.spreadsheetFileFactory = spreadsheetFileFactory;
			this.schemaValidatorFactory = schemaValidatorFactory;
		}
		
		public BooksProcessor build() {
			ensureBooksWorkingRootExists();
			bookCache = new LRUCache<>(serverConfig.getBookCacheMaxSize());
			return new BooksProcessorImpl(this);
		}
		
		private void ensureBooksWorkingRootExists() {
			if (!Files.isDirectory(serverConfig.getBooksWorkingRoot())) {
				try {
					Files.createDirectory(serverConfig.getBooksWorkingRoot());
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
