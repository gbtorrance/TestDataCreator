package org.tdc.process;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.tdc.book.Book;
import org.tdc.book.BookFactory;
import org.tdc.book.BookFactoryImpl;
import org.tdc.book.BookFileWriter;
import org.tdc.book.BookSchemaValidator;
import org.tdc.book.BookSpreadsheetLogWriter;
import org.tdc.book.BookTestDataLoader;
import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.book.BookConfigFactoryImpl;
import org.tdc.config.book.TaskConfigFactory;
import org.tdc.config.book.TaskConfigFactoryImpl;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modeldef.ModelDefFactoryImpl;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.modelinst.ModelInstFactoryImpl;
import org.tdc.schema.SchemaFactory;
import org.tdc.schema.SchemaFactoryImpl;
import org.tdc.schemavalidate.SchemaValidatorFactory;
import org.tdc.schemavalidate.SchemaValidatorFactoryImpl;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;
import org.tdc.task.TaskFactory;
import org.tdc.task.TaskFactoryImpl;
import org.tdc.task.TaskProcessor;
import org.tdc.util.Addr;

/**
 * A {@link Processor} implementation.
 */
public class ProcessorImpl implements Processor {
	private final Path schemasConfigRoot;
	private final Path booksConfigRoot;
	private final SchemaConfigFactory schemaConfigFactory;
	private final ModelConfigFactory modelConfigFactory;
	private final TaskConfigFactory taskConfigFactory;
	private final BookConfigFactory bookConfigFactory;
	private final SpreadsheetFileFactory spreadsheetFileFactory;
	private final SchemaFactory schemaFactory;
	private final ModelDefFactory modelDefFactory;
	private final ModelInstFactory modelInstFactory;
	private final BookFactory bookFactory;
	private final SchemaValidatorFactory schemaValidatorFactory; 
	private final TaskFactory taskFactory;
	
	private ProcessorImpl(Builder builder) {
		this.schemasConfigRoot = builder.schemasConfigRoot;
		this.booksConfigRoot = builder.booksConfigRoot;
		this.schemaConfigFactory = builder.schemaConfigFactory;
		this.modelConfigFactory = builder.modelConfigFactory;
		this.taskConfigFactory = builder.taskConfigFactory;
		this.bookConfigFactory = builder.bookConfigFactory;
		this.spreadsheetFileFactory = builder.spreadsheetFileFactory;
		this.schemaFactory = builder.schemaFactory;
		this.modelDefFactory = builder.modelDefFactory;
		this.modelInstFactory = builder.modelInstFactory;
		this.bookFactory = builder.bookFactory;
		this.schemaValidatorFactory = builder.schemaValidatorFactory;
		this.taskFactory = builder.taskFactory;
	}
	
	@Override
	public Path getSchemasConfigRoot() {
		return schemasConfigRoot;
	}

	@Override
	public Path getBooksConfigRoot() {
		return booksConfigRoot;
	}

	@Override
	public SchemaConfigFactory getSchemaConfigFactory() {
		return schemaConfigFactory;
	}

	@Override
	public ModelConfigFactory getModelConfigFactory() {
		return modelConfigFactory;
	}

	@Override
	public TaskConfigFactory getTaskConfigFactory() {
		return taskConfigFactory;
	}

	@Override
	public BookConfigFactory getBookConfigFactory() {
		return bookConfigFactory;
	}

	@Override
	public SpreadsheetFileFactory getSpreadsheetFileFactory() {
		return spreadsheetFileFactory;
	}

	@Override
	public SchemaFactory getSchemaFactory() {
		return schemaFactory;
	}

	@Override
	public ModelDefFactory getModelDefFactory() {
		return modelDefFactory;
	}

	@Override
	public ModelInstFactory getModelInstFactory() {
		return modelInstFactory;
	}

	@Override
	public BookFactory getBookFactory() {
		return bookFactory;
	}

	@Override
	public SchemaValidatorFactory getSchemaValidatorFactory() {
		return schemaValidatorFactory;
	}

	@Override
	public TaskFactory getTaskFactory() {
		return taskFactory;
	}

	@Override
	public SchemaConfig getSchemaConfig(Addr addr) {
		return schemaConfigFactory.getSchemaConfig(addr);
	}

	@Override
	public boolean isSchemaConfig(Addr addr) {
		return schemaConfigFactory.isSchemaConfig(addr);
	}

	@Override
	public List<SchemaConfig> getAllSchemaConfigs(Map<Addr, Exception> errors) {
		return schemaConfigFactory.getAllSchemaConfigs(errors);
	}

	@Override
	public ModelConfig getModelConfig(Addr addr) {
		return modelConfigFactory.getModelConfig(addr);
	}

	@Override
	public boolean isModelConfig(Addr addr) {
		return modelConfigFactory.isModelConfig(addr);
	}

	@Override
	public List<ModelConfig> getAllModelConfigs(Map<Addr, Exception> errors) {
		return modelConfigFactory.getAllModelConfigs(errors);
	}

	@Override
	public BookConfig getBookConfig(Addr addr) {
		return bookConfigFactory.getBookConfig(addr);
	}

	@Override
	public boolean isBookConfig(Addr addr) {
		return bookConfigFactory.isBookConfig(addr);
	}

	@Override
	public List<BookConfig> getAllBookConfigs(Map<Addr, Exception> errors) {
		return bookConfigFactory.getAllBookConfigs(errors);
	}

	@Override
	public String getTargetBookFileExtension(Addr bookAddr) {
		BookConfig bookConfig = bookConfigFactory.getBookConfig(bookAddr);
		return getTargetBookFileExtension(bookConfig);
	}
	
	private String getTargetBookFileExtension(BookConfig bookConfig) {
		String extension = bookConfig.getBookTemplateFileExtension();
		return extension == null ? "xlsx" : extension;
	}

	@Override
	public void createBook(Addr bookAddr, Path targetPath, 
			Path basedOnBookPath, boolean overwriteExisting) {

		Book basedOnBook = loadBasedOnBookIfExists(basedOnBookPath); 
		BookConfig bookConfig = bookConfigFactory.getBookConfig(bookAddr);
		verifyTargetPathExtension(bookConfig, targetPath);
		SpreadsheetFile newBookFile = createNewSpreadsheetFile(bookConfig);
		writeToSpreadsheetBookFile(bookConfig, newBookFile, basedOnBook);
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
		SpreadsheetFile spreadsheetFile = 
				spreadsheetFileFactory.createReadOnlySpreadsheetFileFromPath(basedOnBookPath);
		Book basedOnBook = bookFactory.getBook(spreadsheetFile);
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
		Path templateFile = bookConfig.getBookTemplateFile();
		SpreadsheetFile newSpreadsheetFile = 
				templateFile == null ?
				spreadsheetFileFactory.createNewSpreadsheetFile() :
				spreadsheetFileFactory.createEditableSpreadsheetFileFromPath(templateFile);
		return newSpreadsheetFile;
	}

	private void writeToSpreadsheetBookFile(
			BookConfig bookConfig, SpreadsheetFile bookFile, Book basedOnBook) {
		
		BookFileWriter bookFileWriter =  new BookFileWriter(bookConfig, bookFile, modelInstFactory);
		if (basedOnBook == null) {
			bookFileWriter.write();
		} 
		else {
			bookFileWriter.writeWithTestDataFromExistingBook(basedOnBook);
		}
		bookFileWriter.deleteDefaultSheet();
	}

	@Override
	public Book loadAndProcessBook(Path bookPath, boolean schemaValidate, boolean processTasks) {
		return loadAndProcessBookWithLogOutput(bookPath, schemaValidate, processTasks, null, false);
	}

	@Override
	public Book loadAndProcessBookWithLogOutput(Path bookPath, boolean schemaValidate, boolean processTasks,
			Path targetPath, boolean overwriteExisting) {

		SpreadsheetFile spreadsheetFile = 
				targetPath == null ? 
				spreadsheetFileFactory.createReadOnlySpreadsheetFileFromPath(bookPath) : 
				spreadsheetFileFactory.createEditableSpreadsheetFileFromPath(bookPath); 

		Book book = bookFactory.getBook(spreadsheetFile);
		
		BookTestDataLoader loader = new BookTestDataLoader(book, spreadsheetFile);
		loader.loadTestData();
		
		if (schemaValidate) {
			BookSchemaValidator schemaValidator = new BookSchemaValidator(book, schemaValidatorFactory);
			schemaValidator.validate();
		}
		
		if (processTasks) {
			TaskProcessor taskProcessor = new TaskProcessor.Builder(taskFactory, book).build();
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

	public static class Builder {
		private final Path schemasConfigRoot;
		private final Path booksConfigRoot;

		private SchemaConfigFactory schemaConfigFactory;
		private ModelConfigFactory modelConfigFactory;
		private TaskConfigFactory taskConfigFactory;
		private BookConfigFactory bookConfigFactory;
		private SpreadsheetFileFactory spreadsheetFileFactory;
		private SchemaFactory schemaFactory;
		private ModelDefFactory modelDefFactory;
		private ModelInstFactory modelInstFactory;
		private BookFactory bookFactory;
		private SchemaValidatorFactory schemaValidatorFactory; 
		private TaskFactory taskFactory;
		
		public Builder(Path schemasConfigRoot, Path booksConfigRoot) {
			this.schemasConfigRoot = schemasConfigRoot;
			this.booksConfigRoot = booksConfigRoot;
		}
		
		public Builder defaultFactories() {
			schemaConfigFactory = new SchemaConfigFactoryImpl(schemasConfigRoot);
			modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);
			taskConfigFactory = new TaskConfigFactoryImpl();
			bookConfigFactory = new BookConfigFactoryImpl(
					booksConfigRoot, modelConfigFactory, taskConfigFactory);
			spreadsheetFileFactory = new ExcelSpreadsheetFileFactory();
			schemaFactory = new SchemaFactoryImpl();
			modelDefFactory = new ModelDefFactoryImpl(schemaFactory, spreadsheetFileFactory);
			modelInstFactory = new ModelInstFactoryImpl(modelDefFactory);
			bookFactory = new BookFactoryImpl(bookConfigFactory, modelInstFactory);
			schemaValidatorFactory = new SchemaValidatorFactoryImpl();
			taskFactory = new TaskFactoryImpl();
			return this;
		}
		
		public Builder setSchemaConfigFactory(SchemaConfigFactory schemaConfigFactory) {
			this.schemaConfigFactory = schemaConfigFactory;
			return this;
		}

		public Builder setModelConfigFactory(ModelConfigFactory modelConfigFactory) {
			this.modelConfigFactory = modelConfigFactory;
			return this;
		}

		public Builder setTaskConfigFactory(TaskConfigFactory taskConfigFactory) {
			this.taskConfigFactory = taskConfigFactory;
			return this;
		}

		public Builder setBookConfigFactory(BookConfigFactory bookConfigFactory) {
			this.bookConfigFactory = bookConfigFactory;
			return this;
		}

		public Builder setSpreadsheetFileFactory(SpreadsheetFileFactory spreadsheetFileFactory) {
			this.spreadsheetFileFactory = spreadsheetFileFactory;
			return this;
		}

		public Builder setSchemaFactory(SchemaFactory schemaFactory) {
			this.schemaFactory = schemaFactory;
			return this;
		}

		public Builder setModelDefFactory(ModelDefFactory modelDefFactory) {
			this.modelDefFactory = modelDefFactory;
			return this;
		}

		public Builder setModelInstFactory(ModelInstFactory modelInstFactory) {
			this.modelInstFactory = modelInstFactory;
			return this;
		}

		public Builder setBookFactory(BookFactory bookFactory) {
			this.bookFactory = bookFactory;
			return this;
		}

		public Builder setSchemaValidatorFactory(SchemaValidatorFactory schemaValidatorFactory) {
			this.schemaValidatorFactory = schemaValidatorFactory;
			return this;
		}

		public Builder setTaskFactory(TaskFactory taskFactory) {
			this.taskFactory = taskFactory;
			return this;
		}

		public Processor build() {
			return new ProcessorImpl(this);
		}
	}
}
