package org.tdc.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tdc.book.BookFileWriter;
import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.book.BookConfigFactoryImpl;
import org.tdc.config.book.TaskConfigFactory;
import org.tdc.config.book.TaskConfigFactoryImpl;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modeldef.ModelDefFactoryImpl;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.modelinst.ModelInstFactoryImpl;
import org.tdc.schema.SchemaFactory;
import org.tdc.schema.SchemaFactoryImpl;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;
import org.tdc.util.Addr;
import org.tdc.util.Util;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Handles processing of command-line operations. 
 */
public class CLIOperations {
	private final OptionParser parser = new OptionParser();
	private final boolean admin;
	
	private Path schemasConfigRoot;
	private Path booksConfigRoot;
	private SchemaConfigFactory schemaConfigFactory;
	private ModelConfigFactory modelConfigFactory;
	private TaskConfigFactory taskConfigFactory;
	private BookConfigFactory bookConfigFactory;
	private SpreadsheetFileFactory spreadsheetFileFactory;
	private SchemaFactory schemaFactory;
	private ModelDefFactory modelDefFactory;
	private ModelInstFactory modelInstFactory;
	
	
	public CLIOperations(boolean admin) {
		this.admin = admin;
		initParser();
	}
	
	private void initParser() {
		parser.acceptsAll(Arrays.asList(
				"h", "help", "?"), "help")
				.forHelp();
		parser.acceptsAll(Arrays.asList(
				"s", "schemas-config-root"), "schemas config root dir")
				.withRequiredArg();
		parser.acceptsAll(Arrays.asList(
				"b", "books-config-root"), "books config root dir")
				.withRequiredArg();
		parser.acceptsAll(Arrays.asList(
				"l", "list"), "list config schema|model|book")
				.withRequiredArg()
				.ofType(CLIArgsConfigType.class);
		parser.acceptsAll(Arrays.asList(
				"c", "create-book"), "create book")
				.withRequiredArg()
				.ofType(Addr.class)
				.describedAs("Book address");
		parser.accepts("target", "target file for book creation")
				.availableIf("c")
				.withRequiredArg()
				.ofType(String.class);
	}

	public void execute(String[] args) {
		try {
			OptionSet options = parser.parse(args);
			executeOptions(options);
		}
		catch (OptionException e) {
			outputAndEnd(e.getMessage());
		}
	}
	
	private void executeOptions(OptionSet options) {
		if (!options.hasOptions() || options.has("h")) {
			executeHelp();
			System.exit(0);
		}

		initConfigDirs(options);
		
		if (options.has("l")) {
			CLIArgsConfigType configType = (CLIArgsConfigType)options.valueOf("l");
			executeListConfig(configType);
		}
		else if (options.has("c")) {
			Addr bookAddr = (Addr)options.valueOf("c");
			Path target = null;
			if (options.has("target")) {
				String fileStr = (String)options.valueOf("target");
				target = Paths.get(fileStr);
			}
			executeCreateBook(bookAddr, target);
		}
		else {
			outputAndEnd("No commands specified");
		}
	}

	private void initConfigDirs(OptionSet options) {
		schemasConfigRoot = options.has("s") ?  Paths.get("" + options.valueOf("s")) : null;
		if (schemasConfigRoot == null || !Files.isDirectory(schemasConfigRoot)) {
			outputAndEnd("A valid Schemas Config Root dir must be specified with option -s or --schemas-config-root");
		}
		booksConfigRoot = options.has("b") ? Paths.get("" + options.valueOf("b")) : null;
		if (booksConfigRoot == null || !Files.isDirectory(booksConfigRoot)) {
			outputAndEnd("A valid Books Config Root dir must be specified with option -b or --books-config-root");
		}
	}

	private void executeHelp() {
		try {
			// TODO improve output format; include header info
			parser.printHelpOn(System.out);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void executeListConfig(CLIArgsConfigType configType) {
		if (configType == CLIArgsConfigType.schema) {
			outputSchemaConfigsList();
		}
		else if (configType == CLIArgsConfigType.model) {
			outputModelConfigsList();
		}
		else if (configType == CLIArgsConfigType.book) {
			outputBookConfigsList();
		}
	}

	private void executeCreateBook(Addr bookAddr, Path target) {
		verifyBookAddr(bookAddr);
		BookConfig bookConfig = getBookConfigFactory().getBookConfig(bookAddr);
		target = verifyTargetOrProvideDefault(target, bookConfig);
		SpreadsheetFile bookFile = getSpreadsheetFileFactory().createNewSpreadsheetFile();
		BookFileWriter bookFileWriter =  new BookFileWriter(bookConfig, bookFile, getModelInstFactory());
		bookFileWriter.write();
		bookFile.saveAsNew(target);
	}

	private void verifyBookAddr(Addr bookAddr) {
		if (!getBookConfigFactory().isBookConfig(bookAddr)) {
			outputAndEnd(bookAddr + " does not represent a Book Config address");
		}
	}

	private Path verifyTargetOrProvideDefault(Path target, BookConfig bookConfig) {
		if (target == null) {
			String defaultName = Util.legalizeName(bookConfig.getBookName()) + ".xlsx";
			target = Paths.get(defaultName);
		}
		if (!target.toString().endsWith(".xlsx")) {
			outputAndEnd("Target file " + target + " must end with .xlsx extension");
		}
		if (Files.exists(target)) {
			outputAndEnd("Target file " + target + 
					" already exists; delete first or specify an alternative file name with --target option");
		}
		if (!Files.isDirectory(target.getParent())) {
			outputAndEnd("Target directory " + target.getParent() + " does not exist");
		}
		return target;
	}

	private void outputSchemaConfigsList() {
		Map<Addr, Exception> errors = new HashMap<>();
		List<SchemaConfig> schemaConfigs = getSchemaConfigFactory().getAllSchemaConfigs(errors);
		output("Schema Config addresses:");
		for (SchemaConfig schemaConfig : schemaConfigs) {
			String addr = schemaConfig.getAddr().toString();
			output(1, addr);
		}
		outputErrors(errors);
	}

	private void outputModelConfigsList() {
		Map<Addr, Exception> errors = new HashMap<>();
		List<ModelConfig> modelConfigs = getModelConfigFactory().getAllModelConfigs(errors);
		output("Model Config addresses:");
		for (ModelConfig modelConfig : modelConfigs) {
			String addr = modelConfig.getAddr().toString();
			String name = modelConfig.getModelName();
			String desc = modelConfig.getModelDescription();
			String nameDesc = name + (desc.length() == 0 ? "" : ": " + desc);
			output(1, addr + " (" + nameDesc + ")");
		}
		outputErrors(errors);
	}

	private void outputBookConfigsList() {
		Map<Addr, Exception> errors = new HashMap<>();
		List<BookConfig> bookConfigs = getBookConfigFactory().getAllBookConfigs(errors);
		output("Book Config addresses:");
		for (BookConfig bookConfig : bookConfigs) {
			String addr = bookConfig.getAddr().toString();
			String name = bookConfig.getBookName();
			String desc = bookConfig.getBookDescription();
			String nameDesc = name + (desc.length() == 0 ? "" : ": " + desc);
			output(1, addr + " (" + nameDesc + ")");
		}
		outputErrors(errors);
	}
	
	private void outputErrors(Map<Addr, Exception> errors) {
		if (errors.size() > 0) {
			output("Errors:");
			for (Entry<Addr, Exception> e : errors.entrySet()) {
				output(1, e.getKey() + " (" + e.getValue().getMessage() + ")");
			}
		}
	}

	private SchemaConfigFactory getSchemaConfigFactory() {
		if (schemaConfigFactory == null) {
			schemaConfigFactory = new SchemaConfigFactoryImpl(schemasConfigRoot); 
		}
		return schemaConfigFactory;
	}

	private ModelConfigFactory getModelConfigFactory() {
		if (modelConfigFactory == null) {
			modelConfigFactory = new ModelConfigFactoryImpl(getSchemaConfigFactory());
		}
		return modelConfigFactory;
	}
	
	private BookConfigFactory getBookConfigFactory() {
		if (bookConfigFactory == null) {
			bookConfigFactory = new BookConfigFactoryImpl(
					booksConfigRoot, getModelConfigFactory(), getTaskConfigFactory());
		}
		return bookConfigFactory;
	}

	private TaskConfigFactory getTaskConfigFactory() {
		if (taskConfigFactory == null) {
			taskConfigFactory = new TaskConfigFactoryImpl();
		}
		return taskConfigFactory;
	}

	private SchemaFactory getSchemaFactory() {
		if (schemaFactory == null) {
			schemaFactory = new SchemaFactoryImpl();
		}
		return schemaFactory;
	}

	private ModelDefFactory getModelDefFactory() {
		if (modelDefFactory == null) {
			modelDefFactory = new ModelDefFactoryImpl(
					getSchemaFactory(), getSpreadsheetFileFactory());
		}
		return modelDefFactory;
	}

	private ModelInstFactory getModelInstFactory() {
		if (modelInstFactory == null) {
			modelInstFactory = new ModelInstFactoryImpl(getModelDefFactory());
		}
		return modelInstFactory;
	}

	private SpreadsheetFileFactory getSpreadsheetFileFactory() {
		if (spreadsheetFileFactory == null) {
			spreadsheetFileFactory = new ExcelSpreadsheetFileFactory();
		}
		return spreadsheetFileFactory;
	}

	private void output(String message) {
		output(0, message);
	}

	private void output(int indent, String message) {
		System.out.println(Util.spaces(indent * 3) + message);
	}

	private void outputAndEnd(String message) {
		output(message);
		output("");
		executeHelp();
		System.exit(1);
	}
}
