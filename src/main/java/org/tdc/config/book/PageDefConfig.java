package org.tdc.config.book;

import org.tdc.util.Addr;

public class PageDefConfig {
	
	public String getPageName() {
		return "TempPageName";
	}
	
	public Addr getModelInstAddr() {
//		return new Addr("MyOtherSchema/MyOtherModelDef/MyOtherModelInst");
		return new Addr("MySchema/MyModelDef/MyOtherModelInst");
	}
}
