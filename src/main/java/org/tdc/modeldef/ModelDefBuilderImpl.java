package org.tdc.modeldef;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.model.MPathIndex;
import org.tdc.schema.Schema;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

public class ModelDefBuilderImpl implements ModelDefBuilder {

	private static final Logger log = LoggerFactory.getLogger(ModelDefBuilderImpl.class);
	
	private ModelConfig config;
	private Schema schema;
	private DOMImplementationRegistry domRegistry;
	
	public ModelDefBuilderImpl(ModelConfig config, Schema schema) {
		this.config = config;
		this.schema = schema;
		initDOMRegistry();
	}
	
	@Override
	public ModelDef build() {
		// TODO possibly building from zipped schemas?

		Path rootSchemaFile = config.getSchemaRootFileFullPath();
		if (!Files.isReadable(rootSchemaFile)) {
			throw new IllegalStateException("Unable to read root schema file: " + rootSchemaFile.toString());
		}
		XSModel xsModel = buildXSModelFromSchemas(rootSchemaFile);
		return buildModelDefFromXSModel(xsModel, config.getSchemaRootElementName(), config.getSchemaRootElementNamespace());
	}
	
	private XSModel buildXSModelFromSchemas(Path rootSchemaFile) {
        XSImplementation xsImpl = (XSImplementation)domRegistry.getDOMImplementation("XS-Loader");
		XSLoader schemaLoader = xsImpl.createXSLoader(null);
		DOMErrorHandler errorHandler = new ModelDefDOMErrorHandler(
				config.isFailOnParserWarning(), config.isFailOnParserNonFatalError());
		schemaLoader.getConfig().setParameter("error-handler", errorHandler);
		XSModel xsModel = schemaLoader.loadURI(rootSchemaFile.toUri().toString());
		return xsModel;
	}
	
	private ModelDef buildModelDefFromXSModel(XSModel xsModel, String rootElementName, String rootElementNamespace) {
		MPathIndex<NodeDef> mpathIndex = new MPathIndex<>();
		ModelDefSchemaParser modelDefSchemaParser = new ModelDefSchemaParser(xsModel, mpathIndex);
		
		log.debug("Start building ModelDef tree");
		ElementNodeDef rootElementDef = modelDefSchemaParser.buildModelDefTreeFromSchema(rootElementName, rootElementNamespace);
		log.debug("Finish building ModelDef tree: rootElementDef: {}", rootElementDef.getName());
		
		return new ModelDefImpl(config, schema, rootElementDef, mpathIndex);
	}

	private void initDOMRegistry() {
        System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMXSImplementationSourceImpl");
		try {
			domRegistry = DOMImplementationRegistry.newInstance();
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException ex) {
			// intentionally converting mostly checked exceptions to unchecked, as any exception
			// is not something the system can be expected to resolve; indicates a coding or configuration problem
			throw new RuntimeException("Unable to instantiate DOMImplementationRegistry", ex);
		}
	}
}
