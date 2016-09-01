package org.tdc.export;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.config.book.TaskConfig;

/**
 * Configuration for {@link DefaultExportTask}.
 */
public class DefaultExportTaskConfig implements TaskConfig {
	private static final Logger log = LoggerFactory.getLogger(DefaultExportTaskConfig.class);
	
	private final String taskID;
	private final Path exportRoot;
	private final String batchDirPrefix;

	public DefaultExportTaskConfig(String taskID, Path exportRoot, String batchDirPrefix) {
		this.taskID = taskID;
		this.exportRoot = exportRoot;
		this.batchDirPrefix = batchDirPrefix;
	}

	@Override
	public String getTaskID() {
		return taskID;
	}

	@Override
	public String getTaskClassName() {
		return DefaultExportTask.class.getName();
	}
	
	public Path getExportRoot() {
		return exportRoot;
	}
	
	public String getBatchDirPrefix() {
		return batchDirPrefix;
	}

	public static TaskConfig build(XMLConfigWrapper config, String key) {
		String taskID = config.getString("[@id]", "export", false);
		String exportRootStr = config.getString(key + ".ExportRoot", null, true);
		String batchDirPrefix = config.getString(key + ".BatchDirPrefix", null, true);
		Path exportRoot = getExportRootPath(exportRootStr);
		return new DefaultExportTaskConfig(taskID, exportRoot, batchDirPrefix);
	}
	
	private static Path getExportRootPath(String exportRootStr) {
		Path exportRoot = Paths.get(exportRootStr);
		if (!Files.exists(exportRoot)) {
			throw new IllegalStateException(
					"Exporter root dir does not exist: " + exportRoot.toString());
		}
		return exportRoot;
	}
}
