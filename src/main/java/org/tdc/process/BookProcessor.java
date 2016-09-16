package org.tdc.process;

import java.nio.file.Path;

import org.tdc.book.Book;
import org.tdc.book.BookFileWriter;
import org.tdc.book.BookSchemaValidator;
import org.tdc.book.BookSpreadsheetLogWriter;
import org.tdc.book.BookTestDataLoader;
import org.tdc.config.book.BookConfig;
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

		Book basedOnBook = loadBasedOnBookIfExists(basedOnBookPath); 
		BookConfig bookConfig = processor.getBookConfigFactory().getBookConfig(bookAddr);
		verifyTargetPathExtension(bookConfig, targetPath);
		SpreadsheetFile newBookFile = createNewSpreadsheetFile(bookConfig);
		writeToSpreadsheetFile(bookConfig, newBookFile, basedOnBook);
		if (overwriteExisting) {
			newBookFile.save(targetPath);
		}
		else {
			newBookFile.saveAsNew(targetPath);
		}
	}
	
	private Book loadBasedOnBookIfExists(Path basedOnBookPath) {
		if (basedOnBookPath == null) {
			return null;
		}
		SpreadsheetFile spreadsheetFile = processor.getSpreadsheetFileFactory()
				.createReadOnlySpreadsheetFileFromPath(basedOnBookPath);
		Book basedOnBook = processor.getBookFactory().getBook(spreadsheetFile);
		BookTestDataLoader loader = new BookTestDataLoader(basedOnBook, spreadsheetFile);
		loader.loadTestData();
		return basedOnBook;
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
			BookConfig bookConfig, SpreadsheetFile bookFile, Book basedOnBook) {
		
		BookFileWriter bookFileWriter =  new BookFileWriter(
				bookConfig, bookFile, processor.getModelInstFactory());
		if (basedOnBook == null) {
			bookFileWriter.write();
		} 
		else {
			bookFileWriter.writeWithTestDataFromExistingBook(basedOnBook);
		}
		bookFileWriter.deleteDefaultSheetIfExists();
	}

	public Book loadAndProcessBook(Path bookPath, boolean schemaValidate, boolean processTasks) {
		return loadAndProcessBookWithLogOutput(bookPath, schemaValidate, processTasks, null, false);
	}

	public Book loadAndProcessBookWithLogOutput(Path bookPath, boolean schemaValidate, boolean processTasks,
			Path targetPath, boolean overwriteExisting) {

		SpreadsheetFileFactory spreadsheetFileFactory = processor.getSpreadsheetFileFactory();
		SpreadsheetFile spreadsheetFile = 
				targetPath == null ? 
				spreadsheetFileFactory.createReadOnlySpreadsheetFileFromPath(bookPath) : 
				spreadsheetFileFactory.createEditableSpreadsheetFileFromPath(bookPath); 

		Book book = processor.getBookFactory().getBook(spreadsheetFile);
		
		BookTestDataLoader loader = new BookTestDataLoader(book, spreadsheetFile);
		loader.loadTestData();
		
		if (schemaValidate) {
			BookSchemaValidator schemaValidator = 
					new BookSchemaValidator(book, processor.getSchemaValidatorFactory());
			schemaValidator.validate();
		}
		
		if (processTasks) {
			TaskProcessor taskProcessor = new TaskProcessor
					.Builder(processor.getTaskFactory(), book).build();
			taskProcessor.processTasks();
		}
		
		if (targetPath != null) {
			BookSpreadsheetLogWriter logWriter = new BookSpreadsheetLogWriter(book, spreadsheetFile);
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
