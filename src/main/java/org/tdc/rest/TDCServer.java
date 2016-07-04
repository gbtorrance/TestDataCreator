package org.tdc.rest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.book.BookConfigFactoryImpl;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;

/**
 * This is the "main" class for the system when running in server mode.
 * 
 * <p>It is responsible for general initialization, as well as starting
 * an embedded Jetty servlet container for hosting REST services.
 */
public class TDCServer {
	private static final Logger log = LoggerFactory.getLogger(TDCServer.class);

	private final Path schemasConfigRoot;
	private final Path booksConfigRoot;
	private final Server server;
	private final Servlet servlet;
	private final ServletHolder servletHolder; 
	private final ServletContextHandler servletContextHandler;
	
	public TDCServer(int port, Path schemasConfigRoot, Path booksConfigRoot) {
		this.schemasConfigRoot = schemasConfigRoot;
		this.booksConfigRoot = booksConfigRoot;
		server = new Server(port);
		servlet = new HttpServletDispatcher();
		servletHolder = new ServletHolder(servlet);
		servletHolder.setInitParameter("javax.ws.rs.Application", TDCApplication.class.getName());
		servletContextHandler = new ServletContextHandler();
		servletContextHandler.addServlet(servletHolder, "/*");
		server.setHandler(servletContextHandler);
		initResourceParams();
	}
	
	public void startAndWait() throws Exception {
		server.start();
		server.join();
	}
	
	public void start() throws Exception {
		server.start();
	}

	public void stop() throws Exception {
		server.stop();
	}
	
	private final void initResourceParams() {
		SchemaConfigFactory schemaConfigFactory = new SchemaConfigFactoryImpl(schemasConfigRoot);
		ModelConfigFactory modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);
		BookConfigFactory bookConfigFactory = new BookConfigFactoryImpl(booksConfigRoot, modelConfigFactory);
		List<SchemaConfig> schemaConfigs = schemaConfigFactory.getAllSchemaConfigs();
		List<ModelConfig> modelConfigs = modelConfigFactory.getAllModelConfigs();
		List<BookConfig> bookConfigs = bookConfigFactory.getAllBookConfigs();
		servletContextHandler.setAttribute(TDCApplication.ATTRIB_SCHEMA_CONFIGS, schemaConfigs);
		servletContextHandler.setAttribute(TDCApplication.ATTRIB_MODEL_CONFIGS, modelConfigs);
		servletContextHandler.setAttribute(TDCApplication.ATTRIB_BOOK_CONFIGS, bookConfigs);
	}
	
	public static void main(String args[]) {
		// TODO parameterize these appropriately
		Path schemasConfigRoot = Paths.get("testfiles/TDCFiles/Schemas");
		Path booksConfigRoot = Paths.get("testfiles/TDCFiles/Books");
		int port = 8080;
		TDCServer server = new TDCServer(port, schemasConfigRoot, booksConfigRoot);
		try {
			server.startAndWait();
		}
		catch(Exception ex) {
			log.error("Exception encountered:", ex);
		}
	}
}
