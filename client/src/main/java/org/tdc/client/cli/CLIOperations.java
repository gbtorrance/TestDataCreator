package org.tdc.client.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tdc.client.config.ClientConfig;
import org.tdc.client.config.ClientConfigImpl;
import org.tdc.client.config.ProfileConfig;
import org.tdc.client.profile.Profile;
import org.tdc.client.profile.ProfileImpl;
import org.tdc.shared.dto.BookConfigDTO;
import org.tdc.shared.dto.ModelConfigDTO;
import org.tdc.shared.dto.SchemaConfigDTO;
import org.tdc.shared.util.SharedUtil;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.KeyValuePair;
import joptsimple.util.PathConverter;

/**
 * Handles processing of command-line operations. 
 */
public class CLIOperations {
	private static final String OP_H = "h";
	private static final String OP_HELP = "help";
	private static final String OP_QUESTION = "?";
	
	private static final String OP_CONFIG_ROOT = "config-root";
	
	private static final String OP_PROFILE_NAME = "profile-name";
	
	private static final String OP_I = "i";
	private static final String OP_INFO = "info";
	
	private static final String OP_L = "l";
	private static final String OP_LIST = "list";
	
	private static final String OP_P = "p";
	private static final String OP_PROCESS_BOOK = "process-book";
	private static final String OP_NO_LOG = "no-log";
	private static final String OP_SCHEMA_VALIDATE = "schema-validate";
	private static final String OP_PROCESS_TASKS = "process-tasks";
	private static final String OP_TASK_PARAM = "task-param";
	
	private static final String OP_TARGET = "target"; // applies to "z", "c", and "p"

	private static final int INDENT_SPACES = 3;
	private static final int MAX_BACKUPS = 3;
	
	private final OptionParser parser = new OptionParser();

	private OptionSpec<?> opHelp;
	private OptionSpec<Path> opConfigRoot;
	private OptionSpec<String> opProfileName;
	private OptionSpec<?> opInfo;
	private OptionSpec<CLIArgsConfigType> opList;
	private OptionSpec<Path> opProcessBook;
	private OptionSpec<?> opNoLog;
	private OptionSpec<?> opSchemaValidate;
	private OptionSpec<String> opProcessTasks;
	private OptionSpec<KeyValuePair> opTaskParam;
	private OptionSpec<Path> opTarget;

	private Path configRoot;
	private ClientConfig config;
	private Profile profile;

	public CLIOperations() {
		initParser();
	}
	
	private void initParser() {
		initParserHelp();
		initParserConfigRoot();
		initParserProfileName();
		initParserInfo();
		initParserList();
		initParserProcessBook();
		initParserProcessTarget();
	}
	
	private void initParserHelp() {
		opHelp = parser.acceptsAll(Arrays.asList(
				OP_H, OP_HELP, OP_QUESTION), "help")
				.forHelp();
	}
	
	private void initParserConfigRoot() {
		opConfigRoot = parser.acceptsAll(Arrays.asList(
				OP_CONFIG_ROOT), "config root dir")
				.withRequiredArg()
				.withValuesConvertedBy(new PathConverter())
				.required();
	}
	
	private void initParserProfileName() {
		opProfileName = parser.acceptsAll(Arrays.asList(
				OP_PROFILE_NAME), "profile name")
				.withRequiredArg()
				.ofType(String.class)
				.required();
	}
	
	private void initParserInfo() {
		opInfo = parser.acceptsAll(Arrays.asList(
				OP_I, OP_INFO), "info");
	}
	
	private void initParserList() {
		opList = parser.acceptsAll(Arrays.asList(
				OP_L, OP_LIST), "list config schema|model|book")
				.withRequiredArg()
				.ofType(CLIArgsConfigType.class);
	}
	
	private void initParserProcessBook() {
		opProcessBook = parser.acceptsAll(Arrays.asList(
				OP_P, OP_PROCESS_BOOK), "process book")
				.withRequiredArg()
				.withValuesConvertedBy(new PathConverter())
				.describedAs("book file to process");
		opNoLog = parser.accepts(
				OP_NO_LOG, "do not write out log info")
				.availableIf(opProcessBook);
		opSchemaValidate = parser.accepts(
				OP_SCHEMA_VALIDATE, "schema validate")
				.availableIf(opProcessBook);
		opProcessTasks = parser.accepts(
				OP_PROCESS_TASKS, "process tasks")
				.availableIf(opProcessBook)
				.withOptionalArg()
				.ofType(String.class);
		opTaskParam = parser.accepts(
				OP_TASK_PARAM, "task parameter")
				.availableIf(opProcessTasks)
				.withRequiredArg()
				.ofType(KeyValuePair.class);
	}
	
	private void initParserProcessTarget() {
		// used by customizer creation, book creation, and book processing)
		opTarget = parser.accepts(
				OP_TARGET, "target file for book/customizer creation")
				.availableIf(opProcessBook)
				.withRequiredArg()
				.withValuesConvertedBy(new PathConverter())
				.describedAs("target file name");
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
		if (!options.hasOptions() || options.has(opHelp)) {
			executeHelp();
			System.exit(0);
		}

		initConfig(options);
		initProfile(options);
		ensureProfileActive();

		if (options.has(opInfo)) {
			executeInfo();
		}
		else if (options.has(opList)) {
			CLIArgsConfigType configType = (CLIArgsConfigType)options.valueOf(OP_LIST);
			executeListConfig(configType);
		}
		else if (options.has(opProcessBook)) {
			Path bookPath = options.valueOf(opProcessBook);
			Path targetPath = options.valueOf(opTarget);
			boolean noLog = options.has(opNoLog);
			boolean schemaValidate = options.has(opSchemaValidate);
			boolean processTasks = options.has(opProcessTasks);
			List<String> taskIDsToProcess = options.hasArgument(opProcessTasks) ? 
					options.valuesOf(opProcessTasks) : null;
			List<KeyValuePair> keyValues = options.hasArgument(opTaskParam) ? 
					options.valuesOf(opTaskParam) : null;
			Map<String, String> taskParams = null;
			if (keyValues != null) {
				taskParams = new HashMap<>();
				for (KeyValuePair keyValue : keyValues) {
					taskParams.put(keyValue.key, keyValue.value);
				}
			}
			executeProcessBook(bookPath, schemaValidate, processTasks, 
					taskIDsToProcess, taskParams, targetPath, noLog);
		}
		else {
			outputAndEnd("No commands specified");
		}
		
		shutdownProfile();
	}

	private void initConfig(OptionSet options) {
		configRoot = options.valueOf(opConfigRoot);
		if (configRoot == null || !Files.isDirectory(configRoot)) {
			outputAndEnd("A valid Config Root dir must be specified with option --" + OP_CONFIG_ROOT);
		}
		config = new ClientConfigImpl.Builder(configRoot).build();
	}

	private void initProfile(OptionSet options) {
		String profileName = options.valueOf(opProfileName);
		if (profileName == null || 
				profileName.equals("") ||
				!config.getProfileConfigs().containsKey(profileName)) {
			outputAndEnd("A valid Profile Name must be specified with option --" + OP_PROFILE_NAME);
		}
		ProfileConfig profileConfig = config.getProfileConfigs().get(profileName);
		profile = new ProfileImpl.Builder(profileConfig).build();
	}

	private void ensureProfileActive() {
		if (!profile.isActive()) {
			outputAndEnd("Profile '" + profile.getConfig().getProfileName() + "' is not active.");
		}
	}

	private void shutdownProfile() {
		profile.shutdown();
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
	
	private void executeInfo() {
		String msg = "Server profile started: " + profile.getServerInfo().getServerStartTime();
		output(msg);
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

	private void outputSchemaConfigsList() {
		Map<String, Exception> errors = new HashMap<>();
		List<SchemaConfigDTO> schemaConfigDTOs = 
				profile.getAllSchemaConfigs(errors);
		output("Schema Config addresses:");
		for (SchemaConfigDTO schemaConfigDTO : schemaConfigDTOs) {
			String addr = schemaConfigDTO.getSchemaAddress();
			output(1, addr);
		}
		outputErrors(errors);
	}

	private void outputModelConfigsList() {
		Map<String, Exception> errors = new HashMap<>();
		List<ModelConfigDTO> modelConfigDTOs = 
				profile.getAllModelConfigs(errors);
		output("Model Config addresses:");
		for (ModelConfigDTO modelConfigDTO : modelConfigDTOs) {
			String addr = modelConfigDTO.getModelAddress();
			String name = modelConfigDTO.getModelName();
			String desc = modelConfigDTO.getModelDescription();
			String nameDesc = name + (desc.length() == 0 ? "" : ": " + desc);
			output(1, addr + " (" + nameDesc + ")");
		}
		outputErrors(errors);
	}

	private void outputBookConfigsList() {
		Map<String, Exception> errors = new HashMap<>();
		List<BookConfigDTO> bookConfigDTOs = 
				profile.getAllBookConfigs(errors);
		output("Book Config addresses:");
		for (BookConfigDTO bookConfigDTO : bookConfigDTOs) {
			String addr = bookConfigDTO.getBookAddress();
			String name = bookConfigDTO.getBookName();
			String desc = bookConfigDTO.getBookDescription();
			String nameDesc = name + (desc.length() == 0 ? "" : ": " + desc);
			output(1, addr + " (" + nameDesc + ")");
		}
		outputErrors(errors);
	}

	private void executeProcessBook(
			Path bookPath, boolean schemaValidate, boolean processTasks, 
			List<String> taskIDsToProcess, Map<String, String> taskParams, 
			Path targetPath, boolean noLog) {

		/*
		if (noLog) {
			bookProcessor.loadAndProcessBook(
					bookPath, schemaValidate, processTasks, 
					taskIDsToProcess, taskParams, null, false);
		}
		else {
			if (targetPath != null) {
				String bookExtension = Util.getExtension(bookPath);
				verifyTargetOrProvideDefault(targetPath, null, bookExtension);
				bookProcessor.loadAndProcessBook(
						bookPath, schemaValidate, processTasks, 
						taskIDsToProcess, taskParams, targetPath, false);
			}
			else {
				ensureFileNotCurrentlyInUse(bookPath);
				Path tempPath = createTempCopy(bookPath);
				bookProcessor.loadAndProcessBook(
						tempPath, schemaValidate, processTasks, 
						taskIDsToProcess, taskParams, tempPath, true);
				backupAndRenameFiles(bookPath, tempPath);
			}
		}
		*/
	}
	
	/*
	private Path verifyTargetOrProvideDefault(Path targetPath, String name, String targetExtension) {
		if (targetPath == null) {
			String defaultName = Util.legalizeName(name) + "." + targetExtension;
			targetPath = Paths.get(defaultName);
		}
		if (!targetPath.toString().endsWith("." + targetExtension)) {
			outputAndEnd("Target file " + targetPath + " must end with ." + targetExtension + " extension");
		}
		if (Files.exists(targetPath)) {
			outputAndEnd("Target file " + targetPath + 
					" already exists; delete first or specify an alternative file name with --target option");
		}
		targetPath = targetPath.toAbsolutePath();
		if (!Files.isDirectory(targetPath.getParent())) {
			outputAndEnd("Target directory " + targetPath.getParent() + " does not exist");
		}
		return targetPath;
	}
	
	private void ensureFileNotCurrentlyInUse(Path bookPath) {
		Path parentPath = bookPath.toAbsolutePath().getParent();
		String fileName = bookPath.getFileName().toString();
		Path excelBackup = parentPath.resolve("~$" + fileName);
		Path libreOfficeBackup = parentPath.resolve(".~lock." + fileName + "#");
		// do initial test for existence of Excel files; 
		// if backup file can be deleted, that means it is not locked, 
		//		and the backup file is likely an orphaned file;
		// if backup file cannot be deleted (i.e. exception), that means
		// 		it is locked, and we definitely want to output an error;
		// after the delete attempt, we'll test for existence again;
		// note: don't attempt to delete Libre Office backup files, as these
		// will more than likely be on a Linux file system, so there will be
		// no locking, and the delete will almost always succeed;
		// testing for 'in use' files is quite a tricky task; 
		// this is not ideal, but it's better than nothing
		try {
			Files.deleteIfExists(excelBackup);
		}
		catch (Exception e) {
			// intentionally do nothing
		}
		boolean found = Files.exists(excelBackup) || Files.exists(libreOfficeBackup);
		if (found) {
			outputAndEnd("It appears this Book file is in use. Please close then try again.");
		}
	}

	private Path createTempCopy(Path bookPath) {
		Path parentPath = bookPath.toAbsolutePath().getParent();
		String fileName = bookPath.getFileName().toString();
		Path tempPath = parentPath.resolve(fileName + ".tmp");
		try {
			Files.copy(bookPath, tempPath, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to create temporary file: " + tempPath, e);
		}
		return tempPath;
	}

	private void backupAndRenameFiles(Path bookPath, Path tempPath) {
		Path parentPath = bookPath.toAbsolutePath().getParent();
		String fileName = bookPath.getFileName().toString();
		String timestamp = LocalDateTime.now().format(SharedConst.DATE_TIME_FORMATTER);
		Path backupPath = parentPath.resolve(fileName + "." + timestamp + ".backup");
		try {
			Files.move(bookPath, backupPath);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to rename original file to backup: " + backupPath);
		}
		try {
			Files.move(tempPath, bookPath);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to rename temp file to original : " + bookPath);
		}
		deleteOldBackups(parentPath, fileName + ".", ".backup");
	}

	private void deleteOldBackups(Path parentPath, String matchPrefix, String matchSuffix) {
		try {
			List<Path> backupFiles = new ArrayList<>();
			Files.find(parentPath, 1, 
					(path, attrib) -> 
							path.getFileName().toString().startsWith(matchPrefix) &&
							path.getFileName().toString().endsWith(matchSuffix))
					.sorted()
					.forEach(path -> backupFiles.add(path));
			for (int i = 0; i < backupFiles.size() - MAX_BACKUPS; i++) {
				Files.delete(backupFiles.get(i));
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to delete old backup files", e);
		}
	}
	*/

	private void outputErrors(Map<String, Exception> errors) {
		if (errors.size() > 0) {
			output("Errors:");
			for (Entry<String, Exception> e : errors.entrySet()) {
				output(1, e.getKey() + " (" + e.getValue().getMessage() + ")");
			}
		}
	}

	private void output(String message) {
		output(0, message);
	}

	private void output(int indent, String message) {
		System.out.println(SharedUtil.spaces(indent * INDENT_SPACES) + message);
	}

	private void outputAndEnd(String message) {
		output(message);
		output("");
		executeHelp();
		System.exit(1);
	}
}
