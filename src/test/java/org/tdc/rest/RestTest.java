package org.tdc.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.rest.dto.BookConfigDTO;
import org.tdc.rest.dto.ModelConfigDTO;
import org.tdc.rest.dto.SchemaConfigDTO;

/**
 * Unit tests for REST services.
 * 
 * <p>Note: It would probably be unsafe to have multiple test classes for REST services
 * without first restructuring the test approach. Multiple test cases could run
 * concurrently, and if they were in separate classes (with each starting and stopping 
 * an embedded Jetty instance), it could cause all sorts of chaos. 
 * For now keep everything in one test class (but maybe consider a different approach for future).
 */
public class RestTest {
	
	private static TDCServer server;
	private static Client client;
	private static String urlPrefix;
	
	@BeforeClass
	public static void setup() throws Exception {
		Path schemasConfigRoot = Paths.get("testfiles/TDCFiles/Schemas");
		Path booksConfigRoot = Paths.get("testfiles/TDCFiles/Books");
		int port = 8080;
		server = new TDCServer(port, schemasConfigRoot, booksConfigRoot);
		server.start();
		client = ClientBuilder.newClient();
		urlPrefix = "http://localhost:" + port;
	}
	
	@AfterClass
	public static void shutdown() throws Exception {
		server.stop();
		client.close();
	}
	
	@Test
	public void testConfigSchemas() throws IOException {
		String target = urlPrefix + "/tdc/config/schemas";
		Response response = client.target(target).request().get();
		List<SchemaConfigDTO> list = response.readEntity(new GenericType<List<SchemaConfigDTO>>(){});
		int foundCount = 0;
		for (SchemaConfigDTO dto : list) {
			if (dto.getSchemaAddress().equals("ConfigTest/SchemaConfigTest")) {
				foundCount++;
			}
			if (dto.getSchemaAddress().equals("Tax/efile1040x_2012v3.0")) {
				foundCount++;
			}
		}
		assertThat(foundCount).isEqualTo(2);
	}

	@Test
	public void testConfigModels() throws IOException {
		String target = urlPrefix + "/tdc/config/models";
		Response response = client.target(target).request().get();
		List<ModelConfigDTO> list = response.readEntity(new GenericType<List<ModelConfigDTO>>(){});
		int foundCount = 0;
		for (ModelConfigDTO dto : list) {
			if (dto.getModelAddress().equals("ConfigTest/SchemaConfigTest/ModelConfigTest")) {
				foundCount++;
			}
			if (dto.getModelAddress().equals("Tax/efile1040x_2012v3.0/Model_1040EZ")) {
				foundCount++;
			}
		}
		assertThat(foundCount).isEqualTo(2);
	}

	@Test
	public void testConfigBooks() throws IOException {
		String target = urlPrefix + "/tdc/config/books";
		Response response = client.target(target).request().get();
		List<BookConfigDTO> list = response.readEntity(new GenericType<List<BookConfigDTO>>(){});
		int foundCount = 0;
		for (BookConfigDTO dto : list) {
			if (dto.getBookAddress().equals("ConfigTest/BookConfigTest")) {
				foundCount++;
			}
			if (dto.getBookAddress().equals("Tax/IndividualIncome2012v1")) {
				foundCount++;
			}
		}
		assertThat(foundCount).isEqualTo(2);
	}
}
