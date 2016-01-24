package org.tdc.model;

import java.util.HashMap;
import java.util.Map;

public class MPathIndex<T extends AbstractNode> {
	
	private Map<String, T> map = new HashMap<>();
	
	public void addMPath(String mpath, T node) {
		map.put(mpath,  node);
	}
	
	public T getNode(String mpath) {
		return map.get(mpath);
	}
	
	public boolean containsKey(String mpath) {
		return map.containsKey(mpath);
	}
}
