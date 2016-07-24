package org.tdc.schemavalidate;

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.book.TestDoc;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A {@link SchemaValidator} implementation for validating {@link TestDoc} DOM {@link Document}s.
 */
public class SchemaValidatorImpl implements SchemaValidator {
	
	private static final Logger log = LoggerFactory.getLogger(SchemaValidatorImpl.class);
	
	private final Schema schema;
	
	private SchemaValidatorImpl(Builder builder) {
		// Schema objects are safe to use in a multi-threaded environment
		this.schema = builder.schema;
	}
	
	@Override
	public void validate(TestDoc testDoc) {
		Document domDocument = testDoc.getDOMDocument();
		
		Validator validator = schema.newValidator();
		SchemaValidatorErrorHandler errorHandler = 
				new SchemaValidatorErrorHandler(validator, testDoc);
		validator.setErrorHandler(errorHandler);
		try {
			validator.validate(new DOMSource(domDocument));
		}
		catch (SAXException | IOException e) {
			throw new RuntimeException("Unable to validate document", e);
		}
	}
	
	public static class Builder {
		
		private final Path schemaRootFile;
		
		private Schema schema;
		
		public Builder(Path schemaRootFile) {
			this.schemaRootFile = schemaRootFile;
		}
		
		public SchemaValidator build() {
			StreamSource streamSource = new StreamSource(schemaRootFile.toFile());
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				schema = schemaFactory.newSchema(streamSource);
			}
			catch (SAXException e) {
				throw new RuntimeException(
						"Unable to create Schema for SchemaValidator from root schema file: " + schemaRootFile);
			}
			 return new SchemaValidatorImpl(this);
		}
	}
}
