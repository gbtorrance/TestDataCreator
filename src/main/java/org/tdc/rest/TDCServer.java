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
import org.tdc.config.server.ServerConfig;
import org.tdc.config.server.ServerConfigImpl;

/**
 * This is the "main" class for the system when running in server mode.
 * 
 * <p>It is responsible for general initialization, as well as starting
 * an embedded Jetty servlet container for hosting REST services.
 */
public class TDCServer {
	private static final Logger log = LoggerFactory.getLogger(TDCServer.class);

	private final ServerConfig serverConfig;
	private final Server server;
	private final Servlet servlet;
	private final ServletHolder servletHolder; 
	private final ServletContextHandler servletContextHandler;
	
	public TDCServer(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		server = new Server(serverConfig.getServerPort());
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
		servletContextHandler.setAttribute(TDCApplication.ATTRIB_SERVER_CONFIG, serverConfig);
	}
	
	public static void main(String args[]) {
		try {
			ServerConfig serverConfig = loadServerConfig(args);
			TDCServer server = new TDCServer(serverConfig);
			server.startAndWait();
		}
		catch(Exception ex) {
			log.error("Exception encountered:", ex);
			System.exit(1);
		}
	}

	private static ServerConfig loadServerConfig(String[] args) {
		if (args.length != 1) {
			throw new RuntimeException(
					"TDCServer must be started with single parameter specifying " + 
					"server config root directory path (e.g. TDCServer C:\\TDCServerConfig)");
		}
		String serverConfigRootStr = args[0];
		Path serverConfigRoot = Paths.get(serverConfigRootStr);
		return new ServerConfigImpl.Builder(serverConfigRoot).build();
	}
}
