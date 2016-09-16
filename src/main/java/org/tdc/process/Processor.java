package org.tdc.process;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.tdc.book.Book;
import org.tdc.book.BookFactory;
import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.book.TaskConfigFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.schema.SchemaFactory;
import org.tdc.schemavalidate.SchemaValidatorFactory;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.task.TaskFactory;
import org.tdc.util.Addr;

/**
 * General purpose helper interface that brings together the 
 * core aspects of system functionality in a single place.
 * It is essentially an interface between the UI layer and
 * the main system processing layer. 
 */
public interface Processor {
	Path getSchemasConfigRoot();
	Path getBooksConfigRoot();
	
	SchemaConfigFactory getSchemaConfigFactory();
	ModelConfigFactory getModelConfigFactory();
	TaskConfigFactory getTaskConfigFactory();
	BookConfigFactory getBookConfigFactory();
	SpreadsheetFileFactory getSpreadsheetFileFactory();
	SchemaFactory getSchemaFactory();
	ModelDefFactory getModelDefFactory();
	ModelInstFactory getModelInstFactory();
	BookFactory getBookFactory();
	SchemaValidatorFactory getSchemaValidatorFactory();
	TaskFactory getTaskFactory();
	
	SchemaConfig getSchemaConfig(Addr addr);
	boolean isSchemaConfig(Addr addr);
	List<SchemaConfig> getAllSchemaConfigs(Map<Addr, Exception> errors);

	ModelConfig getModelConfig(Addr addr);
	boolean isModelConfig(Addr addr);
	List<ModelConfig> getAllModelConfigs(Map<Addr, Exception> errors);

	BookConfig getBookConfig(Addr addr);
	boolean isBookConfig(Addr addr);
	List<BookConfig> getAllBookConfigs(Map<Addr, Exception> errors);
	
	void createCustomizer(
			Addr modelAddr, Path targetPath,
			Addr basedOnModelAddr, boolean overwriteExisting);
	
	String getTargetBookFileExtension(Addr bookAddr);
	
	void createBook(
			Addr bookAddr, Path targetPath, 
			Path basedOnBookPath, boolean overwriteExisting);
	
	Book loadAndProcessBook(
			Path bookPath, boolean schemaValidate, boolean processTasks);
	Book loadAndProcessBookWithLogOutput(
			Path bookPath, boolean schemaValidate, boolean processTasks, 
			Path targetPath, boolean overwriteExisting);
}
