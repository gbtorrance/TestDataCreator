package org.tdc.book;

import java.util.List;

/**
 * Defines core functionality for a Test Set.
 * 
 * <p>A TestSet can have one or more {@link TestCase}s,
 * which each, in turn, can have one or more {@link TestDoc}s.
 */
public interface TestSet {
	String getSetName();
	List<TestCase> getTestCases();
}
