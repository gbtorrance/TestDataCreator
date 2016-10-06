package org.tdc.process;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.tdc.book.Book;
import org.tdc.book.BookFactory;
import org.tdc.book.BookFactoryImpl;
import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.book.BookConfigFactoryImpl;
import org.tdc.config.book.FilterConfigFactory;
import org.tdc.config.book.FilterConfigFactoryImpl;
import org.tdc.config.book.TaskConfigFactory;
import org.tdc.config.book.TaskConfigFactoryImpl;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.config.system.SystemConfig;
import org.tdc.config.system.SystemConfigImpl;
import org.tdc.filter.FilterFactory;
import org.tdc.filter.FilterFactoryImpl;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modeldef.ModelDefFactoryImpl;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.modelinst.ModelInstFactoryImpl;
import org.tdc.schema.SchemaFactory;
import org.tdc.schema.SchemaFactoryImpl;
import org.tdc.schemavalidate.SchemaValidatorFactory;
import org.tdc.schemavalidate.SchemaValidatorFactoryImpl;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;
import org.tdc.task.TaskFactory;
import org.tdc.task.TaskFactoryImpl;
import org.tdc.util.Addr;

/**
 * A {@link Processor} implementation.
 */
public class ProcessorImpl implements Processor {
	private final SystemConfig systemConfig;
	private final SchemaConfigFactory schemaConfigFactory;
	private final ModelConfigFactory modelConfigFactory;
	private final FilterConfigFactory filterConfigFactory;
	private final TaskConfigFactory taskConfigFactory;
	private final BookConfigFactory bookConfigFactory;
	private final SpreadsheetFileFactory spreadsheetFileFactory;
	private final SchemaFactory schemaFactory;
	private final ModelDefFactory modelDefFactory;
	private final ModelInstFactory modelInstFactory;
	private final BookFactory bookFactory;
	private final FilterFactory filterFactory;
	private final SchemaValidatorFactory schemaValidatorFactory;
	private final TaskFactory taskFactory;
	private final ModelProcessor modelProcessor;
	private final BookProcessor bookProcessor;
	
	private ProcessorImpl(Builder builder) {
		this.systemConfig = builder.systemConfig;
		this.schemaConfigFactory = builder.schemaConfigFactory;
		this.modelConfigFactory = builder.modelConfigFactory;
		this.filterConfigFactory = builder.filterConfigFactory;
		this.taskConfigFactory = builder.taskConfigFactory;
		this.bookConfigFactory = builder.bookConfigFactory;
		this.spreadsheetFileFactory = builder.spreadsheetFileFactory;
		this.schemaFactory = builder.schemaFactory;
		this.modelDefFactory = builder.modelDefFactory;
		this.modelInstFactory = builder.modelInstFactory;
		this.bookFactory = builder.bookFactory;
		this.filterFactory = builder.filterFactory;
		this.schemaValidatorFactory = builder.schemaValidatorFactory;
		this.taskFactory = builder.taskFactory;
		this.modelProcessor = builder.modelProcessor;
		this.bookProcessor = builder.bookProcessor;
	}
	
	@Override
	public SystemConfig getSystemConfig() {
		return systemConfig;
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
	public FilterConfigFactory getFilterConfigFactory() {
		return filterConfigFactory;
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
	public FilterFactory getFilterFactory() {
		return filterFactory;
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
	public void createCustomizer(Addr modelAddr, Path targetPath, 
			Addr basedOnModelAddr, boolean overwriteExisting) {

		modelProcessor.createCustomizer(modelAddr, targetPath, basedOnModelAddr, overwriteExisting);
	}
	
	@Override
	public String getTargetBookFileExtension(Addr bookAddr) {
		return bookProcessor.getTargetBookFileExtension(bookAddr);
	}
	
	@Override
	public void createBook(Addr bookAddr, Path targetPath, 
			Path basedOnBookPath, boolean overwriteExisting) {

		bookProcessor.createBook(bookAddr, targetPath, basedOnBookPath, overwriteExisting);
	}

	@Override
	public Book loadAndProcessBook(
			Path bookPath, boolean schemaValidate, boolean processTasks,
			List<String> taskIDsToProcess, Map<String, String> taskParams) {
		
		return bookProcessor.loadAndProcessBook(
				bookPath, schemaValidate, processTasks, 
				taskIDsToProcess, taskParams, null, false);
	}

	@Override
	public Book loadAndProcessBookWithLogOutput(
			Path bookPath, boolean schemaValidate, boolean processTasks,
			List<String> taskIDsToProcess, Map<String, String> taskParams,
			Path targetPath, boolean overwriteExisting) {

		return bookProcessor.loadAndProcessBook(
				bookPath, schemaValidate, processTasks, 
				taskIDsToProcess, taskParams,
				targetPath, overwriteExisting);
	}

	public static class Builder {
		private SystemConfig systemConfig;
		private SchemaConfigFactory schemaConfigFactory;
		private ModelConfigFactory modelConfigFactory;
		private FilterConfigFactory filterConfigFactory;
		private TaskConfigFactory taskConfigFactory;
		private BookConfigFactory bookConfigFactory;
		private SpreadsheetFileFactory spreadsheetFileFactory;
		private SchemaFactory schemaFactory;
		private ModelDefFactory modelDefFactory;
		private ModelInstFactory modelInstFactory;
		private BookFactory bookFactory;
		private FilterFactory filterFactory;
		private SchemaValidatorFactory schemaValidatorFactory; 
		private TaskFactory taskFactory;
		
		private ModelProcessor modelProcessor;
		private BookProcessor bookProcessor;
		
		public Builder defaultFactories(Path systemConfigRoot) {
			systemConfig = new SystemConfigImpl.Builder(systemConfigRoot).build();
			schemaConfigFactory = new SchemaConfigFactoryImpl(systemConfig.getSchemasConfigRoot());
			modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);
			filterConfigFactory = new FilterConfigFactoryImpl();
			taskConfigFactory = new TaskConfigFactoryImpl();
			bookConfigFactory = new BookConfigFactoryImpl(
					systemConfig.getBooksConfigRoot(), modelConfigFactory, 
					filterConfigFactory, taskConfigFactory);
			spreadsheetFileFactory = new ExcelSpreadsheetFileFactory();
			schemaFactory = new SchemaFactoryImpl();
			modelDefFactory = new ModelDefFactoryImpl(schemaFactory, spreadsheetFileFactory);
			modelInstFactory = new ModelInstFactoryImpl(modelDefFactory);
			bookFactory = new BookFactoryImpl(bookConfigFactory, modelInstFactory);
			filterFactory = new FilterFactoryImpl();
			schemaValidatorFactory = new SchemaValidatorFactoryImpl();
			taskFactory = new TaskFactoryImpl();
			return this;
		}
		
		public Builder setSystemConfig(SystemConfig systemConfig) {
			this.systemConfig = systemConfig;
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

		public Builder setFilterConfigFactory(FilterConfigFactory filterConfigFactory) {
			this.filterConfigFactory = filterConfigFactory;
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

		public Builder setFilterFactory(FilterFactory filterFactory) {
			this.filterFactory = filterFactory;
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
			modelProcessor = new ModelProcessor();
			bookProcessor = new BookProcessor();
			Processor processor = new ProcessorImpl(this);
			modelProcessor.setProcessor(processor);
			bookProcessor.setProcessor(processor);
			return processor;
		}
	}
}
