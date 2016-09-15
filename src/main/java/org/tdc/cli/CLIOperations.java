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

import org.tdc.config.book.BookConfig;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.process.Processor;
import org.tdc.process.ProcessorImpl;
import org.tdc.util.Addr;
import org.tdc.util.Util;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Handles processing of command-line operations. 
 */
public class CLIOperations {
	private static final String OP_H = "h";
	private static final String OP_HELP = "help";
	private static final String OP_QUESTION = "?";
	private static final String OP_S = "s";
	private static final String OP_SCHEMAS_CONFIG_ROOT = "schemas-config-root";
	private static final String OP_B = "b";
	private static final String OP_BOOKS_CONFIG_ROOT = "books-config-root";
	private static final String OP_L = "l";
	private static final String OP_LIST = "list";
	private static final String OP_C = "c";
	private static final String OP_CREATE_BOOK = "create-book";
	private static final String OP_TARGET = "target";
	private static final String OP_BASED_ON_BOOK = "based-on-book";
	private static final int INDENT_SPACES = 3;
	
	private final OptionParser parser = new OptionParser();
	private final boolean admin;

	private Path schemasConfigRoot;
	private Path booksConfigRoot;
	private Processor processor;

	public CLIOperations(boolean admin) {
		this.admin = admin;
		initParser();
	}
	
	private void initParser() {
		parser.acceptsAll(Arrays.asList(
				OP_H, OP_HELP, OP_QUESTION), "help")
				.forHelp();
		parser.acceptsAll(Arrays.asList(
				OP_S, OP_SCHEMAS_CONFIG_ROOT), "schemas config root dir")
				.withRequiredArg();
		parser.acceptsAll(Arrays.asList(
				OP_B, OP_BOOKS_CONFIG_ROOT), "books config root dir")
				.withRequiredArg();
		parser.acceptsAll(Arrays.asList(
				OP_L, OP_LIST), "list config schema|model|book")
				.withRequiredArg()
				.ofType(CLIArgsConfigType.class);
		parser.acceptsAll(Arrays.asList(
				OP_C, OP_CREATE_BOOK), "create book")
				.withRequiredArg()
				.ofType(Addr.class)
				.describedAs("Book address");
		parser.accepts(OP_TARGET, "target file for book creation")
				.availableIf(OP_CREATE_BOOK)
				.withRequiredArg()
				.ofType(String.class);
		parser.accepts(OP_BASED_ON_BOOK, "book file with test data for book creation")
		.availableIf(OP_CREATE_BOOK)
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
		if (!options.hasOptions() || options.has(OP_HELP)) {
			executeHelp();
			System.exit(0);
		}

		initConfigDirs(options);
		initProcessor();
		
		
		if (options.has(OP_LIST)) {
			CLIArgsConfigType configType = (CLIArgsConfigType)options.valueOf(OP_LIST);
			executeListConfig(configType);
		}
		else if (options.has(OP_CREATE_BOOK)) {
			Addr bookAddr = (Addr)options.valueOf(OP_CREATE_BOOK);
			Path targetFile = null;
			Path basedOnBookPath = null;
			if (options.has(OP_TARGET)) {
				String fileStr = (String)options.valueOf(OP_TARGET);
				targetFile = Paths.get(fileStr);
			}
			if (options.has(OP_BASED_ON_BOOK)) {
				String fileStr = (String)options.valueOf(OP_BASED_ON_BOOK);
				basedOnBookPath = Paths.get(fileStr);
			}
			executeCreateBook(bookAddr, targetFile, basedOnBookPath);
		}
		else {
			outputAndEnd("No commands specified");
		}
	}

	private void initProcessor() {
		processor = new ProcessorImpl
				.Builder(schemasConfigRoot, booksConfigRoot)
				.defaultFactories()
				.build();
	}

	private void initConfigDirs(OptionSet options) {
		schemasConfigRoot = options.has(OP_SCHEMAS_CONFIG_ROOT) ?  
				Paths.get("" + options.valueOf(OP_SCHEMAS_CONFIG_ROOT)) : null;
		if (schemasConfigRoot == null || !Files.isDirectory(schemasConfigRoot)) {
			outputAndEnd("A valid Schemas Config Root dir must be specified with option -" + 
					OP_S + " or --" + OP_SCHEMAS_CONFIG_ROOT);
		}
		booksConfigRoot = options.has(OP_BOOKS_CONFIG_ROOT) ? 
				Paths.get("" + options.valueOf(OP_BOOKS_CONFIG_ROOT)) : null;
		if (booksConfigRoot == null || !Files.isDirectory(booksConfigRoot)) {
			outputAndEnd("A valid Books Config Root dir must be specified with option -" +
					OP_B + " or --" + OP_BOOKS_CONFIG_ROOT);
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

	private void executeCreateBook(Addr bookAddr, Path targetFile, Path basedOnBookPath) {
		verifyBookAddr(bookAddr);
		String bookName = processor.getBookConfig(bookAddr).getBookName();
		verifyTargetOrProvideDefault(targetFile, bookName);
		verifyBasedOnBookIfExists(basedOnBookPath);
		processor.createBook(bookAddr, targetFile, basedOnBookPath, false);
	}

	private void verifyBookAddr(Addr bookAddr) {
		if (!processor.isBookConfig(bookAddr)) {
			outputAndEnd(bookAddr + " does not represent a Book Config address");
		}
	}

	private Path verifyTargetOrProvideDefault(Path target, String bookName) {
		if (target == null) {
			String defaultName = Util.legalizeName(bookName) + ".xlsx";
			target = Paths.get(defaultName);
		}
		if (!target.toString().endsWith(".xlsx")) {
			outputAndEnd("Target file " + target + " must end with .xlsx extension");
		}
		if (Files.exists(target)) {
			outputAndEnd("Target file " + target + 
					" already exists; delete first or specify an alternative file name with --target option");
		}
		target = target.toAbsolutePath();
		if (!Files.isDirectory(target.getParent())) {
			outputAndEnd("Target directory " + target.getParent() + " does not exist");
		}
		return target;
	}

	private void verifyBasedOnBookIfExists(Path basedOnBookPath) {
		if (Files.notExists(basedOnBookPath)) {
			outputAndEnd("Based-on book " + basedOnBookPath + " does not exists");
		}
	}

	private void outputSchemaConfigsList() {
		Map<Addr, Exception> errors = new HashMap<>();
		List<SchemaConfig> schemaConfigs = processor.getAllSchemaConfigs(errors);
		output("Schema Config addresses:");
		for (SchemaConfig schemaConfig : schemaConfigs) {
			String addr = schemaConfig.getAddr().toString();
			output(1, addr);
		}
		outputErrors(errors);
	}

	private void outputModelConfigsList() {
		Map<Addr, Exception> errors = new HashMap<>();
		List<ModelConfig> modelConfigs = processor.getAllModelConfigs(errors);
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
		List<BookConfig> bookConfigs = processor.getAllBookConfigs(errors);
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

	private void output(String message) {
		output(0, message);
	}

	private void output(int indent, String message) {
		System.out.println(Util.spaces(indent * INDENT_SPACES) + message);
	}

	private void outputAndEnd(String message) {
		output(message);
		output("");
		executeHelp();
		System.exit(1);
	}
}
