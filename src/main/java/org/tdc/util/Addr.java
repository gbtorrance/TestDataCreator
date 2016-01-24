package org.tdc.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Addr {
	
	private Path addrPath;
	
	public Addr(String addr) {
		this.addrPath = standardizeAddrPath(addr);
	}
	
	public Addr(Path path) {
		this(path.toString());
	}
	
	public Addr getParentAddr() {
		int nameCount = addrPath.getNameCount();
		if (nameCount <= 1) {
			throw new IllegalStateException("Addr has no parent: " + addrPath);
		}
		Path parentAddrPath = addrPath.subpath(0,  nameCount-1);
		return new Addr(parentAddrPath.toString());
	}
	
	public Path getPath() {
		return addrPath;
	}
	
	public String getName() {
		Path name = addrPath.getFileName();
		if (name == null) {
			throw new IllegalStateException("Unable to get name from Addr: " + addrPath);
		}
		return name.toString();
	}
	
	public Addr resolve(String append) {
		Path resolvedPath = addrPath.resolve(append);
		return new Addr(resolvedPath);
	}
	
	public Addr resolve(Addr append) {
		Path resolvedPath = addrPath.resolve(append.getPath());
		return new Addr(resolvedPath);
	}
	
	@Override
	public String toString() {
		return addrPath.toString(); 
	}

	@Override
	public int hashCode() {
		return addrPath.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Addr)) {
			return false;
		}
		Addr compareAddr = (Addr)obj;
		return addrPath.equals(compareAddr.addrPath);
	}
	
	private Path standardizeAddrPath(String addr) {
		// strip any initial slash or back slash, 
		// and split on any subsequent slash or backslash
		String[] addrParts = addr.split("/|[\\\\]");
		return Paths.get("", addrParts);
	}
	
//	public static void main(String[] args) {
//		Addr a = new Addr("/test1/test2");
//		Addr b = new Addr("\\test1\\test2");
//		Addr c = new Addr("test1/test2");
//		Addr d = new Addr("test1\\test2");
//		Addr e = new Addr("test1/test2/");
//		Addr f = new Addr("test1\\test2\\");
//		Addr g = new Addr("test1/test3");
//		
//		System.out.println("equals: " + a.equals(b) + ", " + (a.hashCode() == b.hashCode()));
//		System.out.println("equals: " + a.equals(c) + ", " + (a.hashCode() == c.hashCode()));
//		System.out.println("equals: " + a.equals(d) + ", " + (a.hashCode() == d.hashCode()));
//		System.out.println("equals: " + a.equals(e) + ", " + (a.hashCode() == e.hashCode()));
//		System.out.println("equals: " + a.equals(f) + ", " + (a.hashCode() == f.hashCode()));
//		System.out.println("equals: " + a.equals(g) + ", " + (a.hashCode() == g.hashCode()));
//	}
}
