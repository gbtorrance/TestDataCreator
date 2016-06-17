package org.tdc.config.book;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.XMLConfigWrapper;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.util.Addr;

/**
 * A {@link BookConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class BookConfigImpl implements BookConfig {
	
	private static final Logger log = LoggerFactory.getLogger(BookConfigImpl.class);
	private static final String CONFIG_SHEET_NAME = "_Config";
	private static final int CONFIG_SHEET_BOOK_ADDR_ROW = 1;
	private static final int CONFIG_SHEET_BOOK_ADDR_COL = 1;
	private static final int NODE_ROW_START = 2;

	private final Path booksConfigRoot;
	private final Addr addr;
	private final Path bookConfigRoot;
	private final Map<String, DocTypeConfig> docTypeConfigs;
	private final Map<String, PageConfig> pageConfigs;
	private final CellStyle defaultNodeStyle;
	private final CellStyle parentNodeStyle;
	private final CellStyle attribNodeStyle;
	private final CellStyle compositorNodeStyle;
	private final CellStyle choiceMarkerStyle;
	private final CellStyle occurMarkerStyle;
	private final int treeStructureColumnCount;
	private final int treeStructureColumnWidth;
	
	private BookConfigImpl(BookConfigBuilder builder) {
		this.booksConfigRoot = builder.booksConfigRoot;
		this.addr = builder.addr;
		this.bookConfigRoot = builder.bookConfigRoot;
		this.docTypeConfigs = Collections.unmodifiableMap(builder.docTypeConfigs); // unmodifiable
		this.pageConfigs = Collections.unmodifiableMap(builder.pageConfigs); // unmodifiable
		this.defaultNodeStyle = builder.defaultNodeStyle;
		this.parentNodeStyle = builder.parentNodeStyle;
		this.attribNodeStyle = builder.attribNodeStyle;
		this.compositorNodeStyle = builder.compositorNodeStyle;
		this.choiceMarkerStyle = builder.choiceMarkerStyle;
		this.occurMarkerStyle = builder.occurMarkerStyle;
		this.treeStructureColumnCount = builder.treeStructureColumnCount;
		this.treeStructureColumnWidth = builder.treeStructureColumnWidth;
	}
	
	@Override
	public Path getBooksConfigRoot() {
		return booksConfigRoot;
	}

	@Override
	public Addr getAddr() {
		return addr;
	}

	@Override
	public Path getBookConfigRoot() {
		return bookConfigRoot;
	}
	
	@Override
	public Map<String, DocTypeConfig> getDocTypeConfigs() {
		return docTypeConfigs;
	}

	@Override
	public Map<String, PageConfig> getPageConfigs() {
		return pageConfigs;
	}
	
	@Override
	public String getConfigSheetName() {
		return CONFIG_SHEET_NAME;
	}
	
	@Override
	public int getConfigSheetBookAddrRow() {
		return CONFIG_SHEET_BOOK_ADDR_ROW;
	}

	@Override
	public int getConfigSheetBookAddrCol() {
		return CONFIG_SHEET_BOOK_ADDR_COL;
	}
	
	@Override
	public CellStyle getDefaultNodeStyle() {
		return defaultNodeStyle;
	}
	
	@Override
	public CellStyle getParentNodeStyle() {
		return parentNodeStyle;
	}
	
	@Override
	public CellStyle getAttribNodeStyle() {
		return attribNodeStyle;
	}
	
	@Override
	public CellStyle getCompositorNodeStyle() {
		return compositorNodeStyle;
	}
	
	@Override
	public CellStyle getChoiceMarkerStyle() {
		return choiceMarkerStyle;
	}

	@Override
	public CellStyle getOccurMarkerStyle() {
		return occurMarkerStyle;
	}

	@Override
	public int getTreeStructureColumnCount() {
		return treeStructureColumnCount;
	}

	@Override
	public int getTreeStructureColumnWidth() {
		return treeStructureColumnWidth;
	}

	@Override
	public int getNodeRowStart() {
		return NODE_ROW_START;
	}

	@Override
	public int getDataColStart() {
		return treeStructureColumnCount + 1;
	}
	
	public static class BookConfigBuilder {
		private static final String CONFIG_FILE = "TDCBookConfig.xml";
		
		private final XMLConfigWrapper config;
		private final ModelConfigFactory modelConfigFactory;
		private final Path booksConfigRoot;
		private final Addr addr;
		private final Path bookConfigRoot;
		
		private Map<String, DocTypeConfig> docTypeConfigs;
		private Map<String, PageConfig> pageConfigs;
		private CellStyle defaultNodeStyle;
		private CellStyle parentNodeStyle;
		private CellStyle attribNodeStyle;
		private CellStyle compositorNodeStyle;
		private CellStyle choiceMarkerStyle;
		private CellStyle occurMarkerStyle;
		private int treeStructureColumnCount;
		private int treeStructureColumnWidth;
		
		public BookConfigBuilder(Path booksConfigRoot, Addr addr, ModelConfigFactory modelConfigFactory) {
			log.info("Creating BookConfig: {}", addr);
			this.booksConfigRoot = booksConfigRoot;
			this.addr = addr;
			this.bookConfigRoot = booksConfigRoot.resolve(addr.getPath());
			if (!Files.isDirectory(bookConfigRoot)) {
				throw new IllegalStateException("BookConfig root dir does not exist: " + bookConfigRoot.toString());
			}
			Path bookConfigFile = bookConfigRoot.resolve(CONFIG_FILE);
			this.config = new XMLConfigWrapper(bookConfigFile);
			this.modelConfigFactory = modelConfigFactory;
		}

		public BookConfig build() {
			docTypeConfigs = new DocTypeConfigImpl.DocTypeConfigBuilder(config).buildAll();
			pageConfigs = new PageConfigImpl.PageConfigBuilder(config, docTypeConfigs, modelConfigFactory).buildAll();
			defaultNodeStyle = config.getCellStyle("DefaultNodeStyle", null, true);
			parentNodeStyle = config.getCellStyle("ParentNodeStyle", defaultNodeStyle, false);
			attribNodeStyle = config.getCellStyle("AttribNodeStyle", defaultNodeStyle, false);
			compositorNodeStyle = config.getCellStyle("CompositorNodeStyle", defaultNodeStyle, false);
			choiceMarkerStyle = config.getCellStyle("ChoiceMarkerStyle", defaultNodeStyle, false);
			occurMarkerStyle = config.getCellStyle("OccurMarkerStyle", defaultNodeStyle, false);
			treeStructureColumnCount = config.getInt("TreeStructureColumnCount", 0, true);
			treeStructureColumnWidth = config.getInt("TreeStructureColumnWidth", 0, true);
			return new BookConfigImpl(this);
		}
	}
}