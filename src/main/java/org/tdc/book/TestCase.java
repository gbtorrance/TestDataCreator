package org.tdc.book;

import java.util.List;

/**
 * Defines core functionality for a Test Case.
 * 
 * <p>A TestCase contains one or more {@link TestDoc}s.
 * 
 * <P>A TestCase will always have a Case Number and may have
 * an optional Set Name. (An empty string for Set Name is the default.)
 */
public interface TestCase {
	int getCaseNum();
	String getSetName();
	List<TestDoc> getTestDocs();
}
