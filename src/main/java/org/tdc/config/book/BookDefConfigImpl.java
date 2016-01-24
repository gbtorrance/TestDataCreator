package org.tdc.config.book;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.util.Addr;

public class BookDefConfigImpl implements BookDefConfig {
	
	private static final Logger log = LoggerFactory.getLogger(BookDefConfigImpl.class);

	private Addr addr;
	List<PageDefConfig> pageDefConfigList = new ArrayList<>();
	
	public BookDefConfigImpl(Addr addr) {
		this.addr = addr;
		
		 // TODO -- temporary hack
		pageDefConfigList.add(new PageDefConfig());
		
		log.debug("Creating BookDefConfigImpl: {}", addr);
	}
	
	public Addr getAddr() {
		return addr; 
	}
	
	public List<PageDefConfig> getPageDefConfigs() {
		return pageDefConfigList;
	}

}
