package org.tdc.config.modelinst;

import java.nio.file.Path;

import org.tdc.config.modeldef.ModelDefConfig;
import org.tdc.util.Addr;

public interface ModelInstConfig {
	ModelDefConfig getModelDefConfig();
	
	Addr getAddr();
	Path getModelInstRoot();
	Path getModelInstConfigRoot();
	
	int getDefaultOccurrenceDepth();
	int getMPathOccurrenceDepth(String mpath);
}
