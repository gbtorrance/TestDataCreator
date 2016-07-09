package org.tdc.rest;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.book.BookConfigFactoryImpl;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
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
	public final static String ATTRIB_SCHEMAS_CONFIG_ROOT = "tdc.schemasConfigRoot";
	public final static String ATTRIB_BOOKS_CONFIG_ROOT = "tdc.booksConfigRoot";
	public final static String ATTRIB_WORKING_ROOT = "tdc.workingRoot";
	
	private final Set<Object> singletons = new HashSet<>();
	private final ServletContext servletContext;
	
	public TDCApplication(@Context ServletContext servletContext) {
		this.servletContext = servletContext;
		
		@SuppressWarnings("unchecked")
		Path schemasConfigRoot = (Path)servletContext.getAttribute(ATTRIB_SCHEMAS_CONFIG_ROOT);
		@SuppressWarnings("unchecked")
		Path booksConfigRoot = (Path)servletContext.getAttribute(ATTRIB_BOOKS_CONFIG_ROOT);
		@SuppressWarnings("unchecked")
		Path workingRoot = (Path)servletContext.getAttribute(ATTRIB_WORKING_ROOT);
		
		SchemaConfigFactory schemaConfigFactory = new SchemaConfigFactoryImpl(schemasConfigRoot);
		ModelConfigFactory modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);
		BookConfigFactory bookConfigFactory = new BookConfigFactoryImpl(booksConfigRoot, modelConfigFactory);
		List<SchemaConfig> schemaConfigs = schemaConfigFactory.getAllSchemaConfigs();
		List<ModelConfig> modelConfigs = modelConfigFactory.getAllModelConfigs();
		List<BookConfig> bookConfigs = bookConfigFactory.getAllBookConfigs();
		
		BooksProcessor booksProcessor = new BooksProcessorImpl(workingRoot);

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
