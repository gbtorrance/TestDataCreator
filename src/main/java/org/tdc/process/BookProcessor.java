package org.tdc.process;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.tdc.book.Book;
import org.tdc.book.BookFileWriter;
import org.tdc.book.BookSchemaValidator;
import org.tdc.book.BookSpreadsheetLogWriter;
import org.tdc.book.BookTestDataLoader;
import org.tdc.config.book.BookConfig;
import org.tdc.filter.CompositeFilter;
import org.tdc.filter.Filter;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.task.TaskProcessor;
import org.tdc.util.Addr;

/**
 * Package-level class used by ProcessorImpl to handle Book-specific processing.
 */
class BookProcessor {
	private Processor processor;
	
	public void setProcessor(Processor processor) {
		this.processor = processor;
	}
	
	public String getTargetBookFileExtension(Addr bookAddr) {
		BookConfig bookConfig = processor.getBookConfigFactory().getBookConfig(bookAddr);
		return getTargetBookFileExtension(bookConfig);
	}
	
	private String getTargetBookFileExtension(BookConfig bookConfig) {
		String extension = bookConfig.getBookTemplateFileExtension();
		return extension == null ? "xlsx" : extension;
	}
	
	public void createBook(Addr bookAddr, Path targetPath, 
			Path basedOnBookPath, boolean overwriteExisting) {
		
		SpreadsheetFile basedOnBookSF = getBasedOnSpreadsheetFile(basedOnBookPath);
		Book basedOnBook = getBasedOnBook(basedOnBookSF);
		BookConfig bookConfig = getBookConfig(bookAddr);
		verifyTargetPathExtension(bookConfig, targetPath);
		SpreadsheetFile newBookSF = createNewSpreadsheetFile(bookConfig);
		writeToSpreadsheetFile(bookConfig, newBookSF, basedOnBook, basedOnBookSF);
		saveNewBook(newBookSF, targetPath, overwriteExisting);
	}

	private SpreadsheetFile getBasedOnSpreadsheetFile(Path basedOnBookPath) {
		SpreadsheetFile spreadsheetFile = null;
		if (basedOnBookPath != null) {
			spreadsheetFile = processor.getSpreadsheetFileFactory()
					.createReadOnlySpreadsheetFileFromPath(basedOnBookPath); 
		}
		return spreadsheetFile;
	}

	private Book getBasedOnBook(SpreadsheetFile basedOnBookSF) {
		Book book = null;
		if (basedOnBookSF != null) {
			book = processor.getBookFactory().getBook(basedOnBookSF); 
		}
		return book;
	}

	private BookConfig getBookConfig(Addr bookAddr) {
		return processor.getBookConfigFactory().getBookConfig(bookAddr);
	}

	private void verifyTargetPathExtension(BookConfig bookConfig, Path targetPath) {
		String expectedExtension = getTargetBookFileExtension(bookConfig);
		String targetFilename = targetPath.toString();
		String targetExtension = targetFilename.substring(targetFilename.lastIndexOf(".") + 1);
		if (!expectedExtension.equals(targetExtension)) {
			throw new IllegalStateException("Extension of target Book file " + 
					targetFilename + " does not match expected extension '." + 
					expectedExtension + "'");
		}
	}

	private SpreadsheetFile createNewSpreadsheetFile(BookConfig bookConfig) {
		SpreadsheetFileFactory spreadsheetFileFactory = processor.getSpreadsheetFileFactory();
		Path templateFile = bookConfig.getBookTemplateFile();
		SpreadsheetFile newSpreadsheetFile = 
				templateFile == null ?
				spreadsheetFileFactory.createNewSpreadsheetFile() :
				spreadsheetFileFactory.createEditableSpreadsheetFileFromPath(templateFile);
		return newSpreadsheetFile;
	}

	private void writeToSpreadsheetFile(
			BookConfig bookConfig, SpreadsheetFile newBookSF, 
			Book basedOnBook, SpreadsheetFile basedOnBookSF) {
		
		BookFileWriter bookFileWriter =  new BookFileWriter(
				bookConfig, newBookSF, processor.getModelInstFactory());
		if (basedOnBook == null) {
			bookFileWriter.write();
		} 
		else {
			bookFileWriter.writeWithTestDataFromExistingBook(basedOnBook, basedOnBookSF);
		}
		bookFileWriter.deleteDefaultSheetIfExists();
	}

	private void saveNewBook(SpreadsheetFile newBookSF, 
			Path targetPath, boolean overwriteExisting) {
		
		if (overwriteExisting) {
			newBookSF.save(targetPath);
		}
		else {
			newBookSF.saveAsNew(targetPath);
		}
	}

	public Book loadAndProcessBook(
			Path bookPath, boolean schemaValidate, boolean processTasks,
			List<String> taskIDsToProcess, Map<String, String> taskParams,
			Path targetPath, boolean overwriteExisting) {
		
		SpreadsheetFileFactory spreadsheetFileFactory = processor.getSpreadsheetFileFactory();
		SpreadsheetFile spreadsheetFile = 
				targetPath == null ? 
				spreadsheetFileFactory.createReadOnlySpreadsheetFileFromPath(bookPath) : 
				spreadsheetFileFactory.createEditableSpreadsheetFileFromPath(bookPath); 
		Book book = processor.getBookFactory().getBook(spreadsheetFile);
		Filter filter = new CompositeFilter
				.Builder(processor.getFilterFactory(), book)
				.build();
		BookTestDataLoader loader = new BookTestDataLoader(book, spreadsheetFile, filter);
		loader.loadTestData();
		if (schemaValidate) {
			BookSchemaValidator schemaValidator = new BookSchemaValidator(
					book, processor.getSchemaValidatorFactory(), filter);
			schemaValidator.validate();
		}
		if (processTasks) {
			TaskProcessor taskProcessor = new TaskProcessor
					.Builder(processor.getTaskFactory(), book)
					.setTaskIDList(taskIDsToProcess) 
					.setTaskParams(taskParams)
					.setFilter(filter)
					.build();
			taskProcessor.processTasks();
		}
		if (targetPath != null) {
			BookSpreadsheetLogWriter logWriter = 
					new BookSpreadsheetLogWriter(book, spreadsheetFile, filter);
			logWriter.writeLog();
			if (overwriteExisting) {
				spreadsheetFile.save(targetPath);
			}
			else {
				spreadsheetFile.saveAsNew(targetPath);
			}
		}
		return book;
	}
}
