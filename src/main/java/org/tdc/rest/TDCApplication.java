package org.tdc.rest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

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
import org.tdc.config.server.ServerConfig;
import org.tdc.rest.process.BooksProcessor;
import org.tdc.rest.process.BooksProcessorImpl;
import org.tdc.rest.resource.BooksResource;
import org.tdc.rest.resource.ConfigBooksResource;
import org.tdc.rest.resource.ConfigModelsResource;
import org.tdc.rest.resource.ConfigSchemasResource;

/**
 * REST application for providing TDC services.
 */
public class TDCApplication extends Application {
	public final static String ATTRIB_SERVER_CONFIG = "tdc.serverConfig";
	
	private final Set<Object> singletons = new HashSet<>();
	private final ServletContext servletContext;
	
	public TDCApplication(@Context ServletContext servletContext) {
		this.servletContext = servletContext;
		
		@SuppressWarnings("unchecked")
		ServerConfig serverConfig = (ServerConfig)servletContext.getAttribute(ATTRIB_SERVER_CONFIG);
		
		SchemaConfigFactory schemaConfigFactory = new SchemaConfigFactoryImpl(
				serverConfig.getSchemasConfigRoot());
		ModelConfigFactory modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);
		TaskConfigFactory taskConfigFactory = new TaskConfigFactoryImpl();
		BookConfigFactory bookConfigFactory = new BookConfigFactoryImpl(
				serverConfig.getBooksConfigRoot(), 
				modelConfigFactory, 
				taskConfigFactory);
		List<SchemaConfig> schemaConfigs = schemaConfigFactory.getAllSchemaConfigs();
		List<ModelConfig> modelConfigs = modelConfigFactory.getAllModelConfigs();
		List<BookConfig> bookConfigs = bookConfigFactory.getAllBookConfigs();
		
		BooksProcessor booksProcessor = new BooksProcessorImpl(serverConfig);

		singletons.add(new ConfigSchemasResource(schemaConfigs));
		singletons.add(new ConfigModelsResource(modelConfigs));
		singletons.add(new ConfigBooksResource(bookConfigs));
		singletons.add(new BooksResource(booksProcessor));
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
