package org.tdc.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.server.ServerConfig;
import org.tdc.config.server.ServerConfigImpl;
import org.tdc.rest.dto.BookConfigDTO;
import org.tdc.rest.dto.BookDTO;
import org.tdc.rest.dto.ModelConfigDTO;
import org.tdc.rest.dto.SchemaConfigDTO;
import org.tdc.rest.dto.TestCaseDTO;
import org.tdc.rest.dto.TestDocDTO;
import org.tdc.rest.dto.TestSetDTO;
import org.tdc.util.Util;

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

	private static final Logger log = LoggerFactory.getLogger(RestTest.class);

	private static ServerConfig serverConfig;
	private static TDCServer server;
	private static Client client;
	private static String urlPrefix;
	
	@BeforeClass
	public static void setup() throws Exception {
		Path serverConfigRoot = Paths.get("testfiles/TDCServer");
		serverConfig = new ServerConfigImpl.Builder(serverConfigRoot).build();
		server = new TDCServer(serverConfig);
		server.start();
		client = ClientBuilder.newClient();
		urlPrefix = "http://localhost:" + serverConfig.getServerPort();
	}
	
	@AfterClass
	public static void shutdown() throws Exception {
		server.stop();
		client.close();
		Util.purgeDirectory(serverConfig.getBooksWorkingRoot());
	}
	
	@Test
	public void testConfigSchemas() throws IOException {
		String target = urlPrefix + "/tdc/config/schemas";
		Response response = client.target(target).request().get();
		List<SchemaConfigDTO> list = response.readEntity(new GenericType<List<SchemaConfigDTO>>(){});
		response.close();
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
		response.close();
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
		response.close();
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
	
	@Test
	public void testBookUpload() throws IOException {
		// upload book
		String target = urlPrefix + "/tdc/books";
		MultipartFormDataOutput mdo = new MultipartFormDataOutput();
		Response response;
		try (FileInputStream fis = new FileInputStream("testfiles/SampleFiles/TestBook.xlsx")) {
			mdo.addFormData("file", fis, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			GenericEntity<MultipartFormDataOutput> entity = 
					new GenericEntity<MultipartFormDataOutput>(mdo) {};
			response = client.target(target).request().post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA));
		}
		int uploadStatus = response.getStatus();
		String bookLocation = response.getLocation().toString();
		response.close();
		assertThat(uploadStatus).isEqualTo(Response.Status.CREATED.getStatusCode());
		assertThat(bookLocation).startsWith(urlPrefix + "/tdc/books/");
		
		// get book info after upload
		System.out.println("************************");
		System.out.println("************************");
		System.out.println("************************");
		System.out.println("************************");
		Response response2 = client.target(bookLocation).request().get();
//		String json = response2.readEntity(String.class);
//		log.debug("Book Info: status = {} / {}" + response2.getStatus(), json);
		BookDTO bookDTO = response2.readEntity(BookDTO.class);
		assertThat(bookDTO.getBookAddress()).isEqualTo("Tax/IndividualIncome2012v1");
		outputBookDTO(bookDTO);
	}
	
	private void outputBookDTO(BookDTO book) {
		log.debug("Address: {}",  book.getBookAddress());
		for (TestSetDTO testSet : book.getTestSets()) {
			log.debug("  SetName: {}", testSet.getSetName());
			for (TestCaseDTO testCase : testSet.getTestCases()) {
				log.debug("    CaseNum: {}", testCase.getCaseNum());
				for (TestDocDTO testDoc : testCase.getTestDocs()) {
					log.debug("      ColNum: {}", testDoc.getColNum());
					int testLoadCount = testDoc.getResults().getTestLoadResult() == null ? 
							0 : testDoc.getResults().getTestLoadResult().getMessages().size();
					int schemaValidateCount = testDoc.getResults().getSchemaValidateResult() == null ? 
							0 : testDoc.getResults().getSchemaValidateResult().getMessages().size();
					log.debug("      Test load count: {}", testLoadCount);
					log.debug("      Schema validate count: {}", schemaValidateCount);
				}
			}
		}
	}
}
