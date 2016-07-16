package org.tdc.config.book;

import java.util.List;

import org.tdc.config.model.ModelConfig;

/**
 * Defines getters for configuration items applicable to {@link org.tdc.book.Page Pages}.
 */
public interface PageConfig {
	String getPageName();
	ModelConfig getModelConfig();
	DocTypeConfig getDocTypeConfig();
	List<PageNodeDetailColumnConfig> getNodeDetailColumns();
	List<DocIDRowConfig> getDocIDRows();
	int getDocIDRowStart();
	int getDocIDRowLabelCol();
	int getHeaderRowStart();
	int getNodeRowStart();
	int getNodeColStart();
	int getNodeDetailColStart();
	int getTestDocColStart();
}
