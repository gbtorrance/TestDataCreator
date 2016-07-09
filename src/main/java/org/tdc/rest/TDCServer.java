package org.tdc.rest;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private final Path workingRoot;
	private final Server server;
	private final Servlet servlet;
	private final ServletHolder servletHolder; 
	private final ServletContextHandler servletContextHandler;
	
	public TDCServer(int port, Path schemasConfigRoot, Path booksConfigRoot, Path workingRoot) {
		this.schemasConfigRoot = schemasConfigRoot;
		this.booksConfigRoot = booksConfigRoot;
		this.workingRoot = workingRoot;
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
		servletContextHandler.setAttribute(TDCApplication.ATTRIB_SCHEMAS_CONFIG_ROOT, schemasConfigRoot);
		servletContextHandler.setAttribute(TDCApplication.ATTRIB_BOOKS_CONFIG_ROOT, booksConfigRoot);
		servletContextHandler.setAttribute(TDCApplication.ATTRIB_WORKING_ROOT, workingRoot);
	}
	
	public static void main(String args[]) {
		// TODO parameterize these appropriately
		Path schemasConfigRoot = Paths.get("testfiles/TDCFiles/Schemas");
		Path booksConfigRoot = Paths.get("testfiles/TDCFiles/Books");
		Path workingRoot = Paths.get("testfiles/TDCServer/Working");
		int port = 8080;
		TDCServer server = new TDCServer(port, schemasConfigRoot, booksConfigRoot, workingRoot);
		try {
			server.startAndWait();
		}
		catch(Exception ex) {
			log.error("Exception encountered:", ex);
		}
	}
}
