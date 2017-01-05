package org.tdc.server.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.book.Book;
import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.system.ServerConfig;
import org.tdc.process.BookProcessor;
import org.tdc.process.ModelProcessor;
import org.tdc.server.mapper.BookConfigMapper;
import org.tdc.server.mapper.BookMapper;
import org.tdc.server.mapper.ModelConfigMapper;
import org.tdc.server.mapper.SchemaConfigMapper;
import org.tdc.shared.dto.BookConfigsDTO;
import org.tdc.shared.dto.BookDTO;
import org.tdc.shared.dto.ModelConfigsDTO;
import org.tdc.shared.dto.SchemaConfigsDTO;
import org.tdc.shared.dto.ServerInfoDTO;
import org.tdc.shared.util.SharedConst;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.LRUCache;
import org.tdc.util.Util;

/**
 * A {@link ServerProcessor} implementation.
 */
public class ServerProcessorImpl implements ServerProcessor {
	private static final Logger log = LoggerFactory.getLogger(ServerProcessorImpl.class);
	private static final DateTimeFormatter YEAR_PLUS_DAY_OF_YEAR_FORMATTER = 
			DateTimeFormatter.ofPattern("yyDDD"); // e.g. 1/24/2016 = "16024"
	private static final String BOOK_FILE_NAME = "Book.xlsx"; // TODO use correct file extension; might be xlsm
	
	private final ServerConfig serverConfig;
	private final ModelProcessor modelProcessor;
	private final BookProcessor bookProcessor;
	private final ServerInfoDTO serverInfoDTO;
	private final SchemaConfigsDTO schemaConfigsDTO;
	private final ModelConfigsDTO modelConfigsDTO;
	private final BookConfigsDTO bookConfigsDTO;
	private final Cache<String,Book> bookCache;
	
	private int currentYearPlusDay;
	private int seq;
	
	private ServerProcessorImpl(Builder builder) {
		this.serverConfig = builder.serverConfig;
		this.modelProcessor = builder.modelProcessor;
		this.bookProcessor = builder.bookProcessor;
		this.serverInfoDTO = builder.serverInfoDTO;
		this.schemaConfigsDTO = builder.schemaConfigsDTO;
		this.modelConfigsDTO = builder.modelConfigsDTO;
		this.bookConfigsDTO = builder.bookConfigsDTO;
		this.bookCache = builder.bookCache;
	}
	
	@Override
	public ServerInfoDTO getServerInfoDTO() {
		return serverInfoDTO;
	}
	
	@Override
	public SchemaConfigsDTO getSchemaConfigsDTO() {
		return schemaConfigsDTO;
	}

	@Override
	public ModelConfigsDTO getModelConfigsDTO() {
		return modelConfigsDTO;
	}

	@Override
	public BookConfigsDTO getBookConfigsDTO() {
		return bookConfigsDTO;
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
					Util.createDirectory(bookWorkingRoot);
					return bookID;
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
		Path bookPath = getSpreadsheetFilePath(bookID);
		Book book = bookProcessor.loadAndProcessBook(
				bookPath, 
				true,  // schemaValidate 
				false, // processTasks 
				null,  // taskIDsToProcess 
				null,  // taskParams
				null,  // targetPath
				true); // overwriteExisting
		return book;
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
		private final SchemaConfigFactory schemaConfigFactory;
		private final ModelConfigFactory modelConfigFactory;
		private final BookConfigFactory bookConfigFactory;
		private final ModelProcessor modelProcessor;
		private final BookProcessor bookProcessor;
		
		private ServerInfoDTO serverInfoDTO;
		private SchemaConfigsDTO schemaConfigsDTO;
		private ModelConfigsDTO modelConfigsDTO;
		private BookConfigsDTO bookConfigsDTO;
		
		private Cache<String,Book> bookCache;

		public Builder(
				ServerConfig serverConfig, 
				SchemaConfigFactory schemaConfigFactory,
				ModelConfigFactory modelConfigFactory,
				BookConfigFactory bookConfigFactory,
				ModelProcessor modelProcessor, 
				BookProcessor bookProcessor) {
			
			this.serverConfig = serverConfig;
			this.schemaConfigFactory = schemaConfigFactory;
			this.modelConfigFactory = modelConfigFactory;
			this.bookConfigFactory = bookConfigFactory;
			this.modelProcessor = modelProcessor;
			this.bookProcessor = bookProcessor;
		}
		
		public ServerProcessor build() {
			initServerInfoDTO();
			initConfigDTOs();
			ensureBooksWorkingRootExists();
			bookCache = new LRUCache<>(serverConfig.getBookCacheMaxSize());
			return new ServerProcessorImpl(this);
		}

		private void initServerInfoDTO() {
			serverInfoDTO = new ServerInfoDTO();
			serverInfoDTO.setServerStartTime(
					LocalDateTime.now().format(SharedConst.DT_FORMAT_USER));
		}

		private void initConfigDTOs() {
			Map<Addr, Exception> exceptions = new HashMap<>();
			List<SchemaConfig> schemaConfigs = schemaConfigFactory.getAllSchemaConfigs(exceptions);
			schemaConfigsDTO = SchemaConfigMapper.INSTANCE
					.schemaConfigsToSchemaConfigsDTO(schemaConfigs, exceptions);

			exceptions.clear();
			List<ModelConfig> modelConfigs = modelConfigFactory.getAllModelConfigs(exceptions);
			modelConfigsDTO = ModelConfigMapper.INSTANCE
					.modelConfigsToModelConfigsDTO(modelConfigs, exceptions);
			
			exceptions.clear();
			List<BookConfig> bookConfigs = bookConfigFactory.getAllBookConfigs(exceptions);
			bookConfigsDTO = BookConfigMapper.INSTANCE
					.bookConfigsToBookConfigsDTO(bookConfigs, exceptions);
		}

		private void ensureBooksWorkingRootExists() {
			if (!Files.isDirectory(serverConfig.getBooksWorkingRoot())) {
				Util.createDirectory(serverConfig.getBooksWorkingRoot());
			}
		}
	}
}
