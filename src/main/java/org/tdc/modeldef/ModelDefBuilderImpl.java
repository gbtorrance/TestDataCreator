package org.tdc.modeldef;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.model.MPathIndex;
import org.tdc.modelcustomizer.ModelCustomizerReader;
import org.tdc.schema.Schema;
import org.tdc.schemaparse.ModelDefDOMErrorHandler;
import org.tdc.schemaparse.ModelDefSchemaParser;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

/**
 * A {@link ModelDefBuilder} implementation.
 *
 * <p>Builds an Apache Xerces {@link XSModel} from the Schema on the file system, 
 * starting with the root element and root element namespace defined in the configuration.
 * It then uses {@link ModelDefSchemaParser} to build an in-memory ModelDef object tree
 * from the XSModel, and injects this tree, along with other dependencies, into a new {@link ModelDef} instance, 
 * which is then returned.
 */
public class ModelDefBuilderImpl implements ModelDefBuilder {

	private static final Logger log = LoggerFactory.getLogger(ModelDefBuilderImpl.class);
	
	private final ModelConfig config;
	private final Schema schema;
	private final SpreadsheetFileFactory spreadsheetFileFactory;
	
	private DOMImplementationRegistry domRegistry;
	private MPathIndex<NodeDef> mpathIndex;
	private ModelDefSharedState sharedState;
	
	public ModelDefBuilderImpl(ModelConfig config, Schema schema, SpreadsheetFileFactory spreadsheetFileFactory) {
		this.config = config;
		this.schema = schema;
		this.spreadsheetFileFactory = spreadsheetFileFactory;
		initDOMRegistry();
	}
	
	@Override
	public ModelDef build() {
		// TODO possibly add ability to build from zipped schemas?
		ElementNodeDef rootElement = buildNodeTree();
		if (config.hasModelCustomizerConfig()) {
			customizeModelDefTree(rootElement);
		}
		return new ModelDefImpl(config, schema, rootElement, mpathIndex, sharedState);
	}
	
	@Override
	public ModelDef buildSkipCustomization() {
		ElementNodeDef rootElement = buildNodeTree();
		return new ModelDefImpl(config, schema, rootElement, mpathIndex, sharedState);
	}
	
	public ElementNodeDef buildNodeTree() {
		log.debug("Start building ModelDef tree");
		mpathIndex = new MPathIndex<>();
		sharedState = new ModelDefSharedState();
		Path rootSchemaFile = config.getSchemaRootFileFullPath();
		if (!Files.isReadable(rootSchemaFile)) {
			throw new IllegalStateException("Unable to read root schema file: " + rootSchemaFile.toString());
		}
		XSModel xsModel = buildXSModelFromSchemas(rootSchemaFile);
		ModelDefSchemaParser modelDefSchemaParser = 
				new ModelDefSchemaParser(xsModel, mpathIndex, sharedState, config.getSchemaExtractors());
		ElementNodeDef rootElement = modelDefSchemaParser.buildModelDefTreeFromSchema(
				config.getSchemaRootElementName(), config.getSchemaRootElementNamespace());
		log.debug("Finish building ModelDef tree: rootElementDef: {}", rootElement.getName());
		return rootElement;
	}
	
	private XSModel buildXSModelFromSchemas(Path rootSchemaFile) {
		XSImplementation xsImpl = (XSImplementation)domRegistry.getDOMImplementation("XS-Loader");
		XSLoader schemaLoader = xsImpl.createXSLoader(null);
		DOMErrorHandler errorHandler = new ModelDefDOMErrorHandler(
				config.getFailOnParserWarning(), config.getFailOnParserNonFatalError());
		schemaLoader.getConfig().setParameter("error-handler", errorHandler);
		XSModel xsModel = schemaLoader.loadURI(rootSchemaFile.toUri().toString());
		return xsModel;
	}
	
	private void customizeModelDefTree(ElementNodeDef rootElement) {
		log.debug("Start customizing ModelDef tree");
		ModelCustomizerConfig customizerConfig = config.getModelCustomizerConfig();
		Path path = customizerConfig.getFilePath();
		if (!Files.isRegularFile(path)) {
			throw new IllegalStateException("Unable to locate or read customizer spreadsheet file: " + path.toString());
		}
		SpreadsheetFile spreadsheetFile = spreadsheetFileFactory.getSpreadsheetFileFromPath(path);
		ModelCustomizerReader reader = new ModelCustomizerReader(rootElement, 
				config.getModelCustomizerConfig(), spreadsheetFile);
		reader.readCustomizer();
		log.debug("Finish customizing ModelDef tree: rootElementDef: {}", rootElement.getName());
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
