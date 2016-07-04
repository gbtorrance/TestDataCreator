package org.tdc.rest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.tdc.config.book.BookConfig;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.rest.resource.ConfigBooksResource;
import org.tdc.rest.resource.ConfigModelsResource;
import org.tdc.rest.resource.ConfigSchemasResource;

/**
 * REST application for providing TDC services.
 */
public class TDCApplication extends Application {
	public final static String ATTRIB_SCHEMA_CONFIGS = "tdc.schemaConfigs";
	public final static String ATTRIB_MODEL_CONFIGS = "tdc.modelConfigs";
	public final static String ATTRIB_BOOK_CONFIGS = "tdc.bookConfigs";
	
	private final Set<Object> singletons = new HashSet<>();
	private final ServletContext servletContext;
	
	public TDCApplication(@Context ServletContext servletContext) {
		this.servletContext = servletContext;
		
		@SuppressWarnings("unchecked")
		List<SchemaConfig> schemaConfigs = (List<SchemaConfig>)servletContext.getAttribute(ATTRIB_SCHEMA_CONFIGS);
		@SuppressWarnings("unchecked")
		List<ModelConfig> modelConfigs = (List<ModelConfig>)servletContext.getAttribute(ATTRIB_MODEL_CONFIGS);
		@SuppressWarnings("unchecked")
		List<BookConfig> bookConfigs = (List<BookConfig>)servletContext.getAttribute(ATTRIB_BOOK_CONFIGS);

		singletons.add(new ConfigSchemasResource(schemaConfigs));
		singletons.add(new ConfigModelsResource(modelConfigs));
		singletons.add(new ConfigBooksResource(bookConfigs));
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
