package org.tdc.rest;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.tdc.book.BookFactory;
import org.tdc.book.BookFactoryImpl;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.book.BookConfigFactoryImpl;
import org.tdc.config.book.TaskConfigFactory;
import org.tdc.config.book.TaskConfigFactoryImpl;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.config.server.ServerConfig;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modeldef.ModelDefFactoryImpl;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.modelinst.ModelInstFactoryImpl;
import org.tdc.rest.process.BooksProcessor;
import org.tdc.rest.process.BooksProcessorImpl;
import org.tdc.rest.process.ConfigsProcessor;
import org.tdc.rest.process.ConfigsProcessorImpl;
import org.tdc.rest.resource.BooksResource;
import org.tdc.rest.resource.ConfigBooksResource;
import org.tdc.rest.resource.ConfigModelsResource;
import org.tdc.rest.resource.ConfigSchemasResource;
import org.tdc.schema.SchemaFactory;
import org.tdc.schema.SchemaFactoryImpl;
import org.tdc.schemavalidate.SchemaValidatorFactory;
import org.tdc.schemavalidate.SchemaValidatorFactoryImpl;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;

/**
 * REST application for providing TDC services.
 */
public class TDCApplication extends Application {
	public final static String ATTRIB_SERVER_CONFIG = "tdc.serverConfig";
	
	private final Set<Object> singletons = new HashSet<>();
	private final ServletContext servletContext;
	private final ServerConfig serverConfig;
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
	private final ConfigsProcessor configsProcessor;
	private final BooksProcessor booksProcessor;
	
	public TDCApplication(@Context ServletContext servletContext) {
		this.servletContext = servletContext;
		
		serverConfig = (ServerConfig)servletContext.getAttribute(ATTRIB_SERVER_CONFIG);
		
		schemaConfigFactory = new SchemaConfigFactoryImpl(
				serverConfig.getSchemasConfigRoot());
		modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);
		taskConfigFactory = new TaskConfigFactoryImpl();
		bookConfigFactory = new BookConfigFactoryImpl(
				serverConfig.getBooksConfigRoot(), 
				modelConfigFactory, 
				taskConfigFactory);
		
		spreadsheetFileFactory = new ExcelSpreadsheetFileFactory();
		
		schemaFactory = new SchemaFactoryImpl();
		modelDefFactory = new ModelDefFactoryImpl(schemaFactory, spreadsheetFileFactory);
		modelInstFactory = new ModelInstFactoryImpl(modelDefFactory);
		bookFactory = new BookFactoryImpl(bookConfigFactory, modelInstFactory);
		
		schemaValidatorFactory = new SchemaValidatorFactoryImpl();
		
		configsProcessor = new ConfigsProcessorImpl.Builder(
				schemaConfigFactory, 
				modelConfigFactory, 
				bookConfigFactory).build();
		
		booksProcessor = new BooksProcessorImpl.Builder(
				serverConfig, 
				bookFactory, 
				spreadsheetFileFactory, 
				schemaValidatorFactory).build();
		
		ConfigSchemasResource configSchemasResource = 
				new ConfigSchemasResource(configsProcessor);
		ConfigModelsResource configModelsResource = 
				new ConfigModelsResource(configsProcessor);
		ConfigBooksResource configBooksResource = 
				new ConfigBooksResource(configsProcessor);
		BooksResource booksResource = 
				new BooksResource(booksProcessor); 

		singletons.add(configSchemasResource);
		singletons.add(configModelsResource);
		singletons.add(configBooksResource);
		singletons.add(booksResource);
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
