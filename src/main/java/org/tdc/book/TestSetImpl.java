package org.tdc.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * A {@link TestSet} implementation.
 */
public class TestSetImpl implements TestSet {
	
	private String setName;
	private List<TestCase> testCases;
	
	public TestSetImpl(Builder builder) {
		this.setName = builder.setName;
		this.testCases = Collections.unmodifiableList(builder.testCases); // unmodifiable
	}
	
	@Override
	public String getSetName() {
		return setName;
	}
	
	@Override
	public List<TestCase> getTestCases() {
		return testCases;
	}

	public static class Builder {
		private final Map<String, Page> pages;
		
		private String setName;
		private List<TestCase> testCases;
		
		public Builder(Map<String, Page> pages) {
			this.pages = pages;
		}
		
		public List<TestSet> buildAll() {
			List<TestSet> testSets = new ArrayList<>();
			List<TestDoc> allTestDocs = new ArrayList<>();
			pages.values().forEach(page -> allTestDocs.addAll(page.getTestDocs()));
			Map<String, List<TestDoc>> testSetNameToTestDocMap = allTestDocs
					.stream()
					.collect(Collectors.groupingBy(TestDoc::getSetName));
			for (Entry<String, List<TestDoc>> entry : testSetNameToTestDocMap.entrySet()) {
				TestSet testSet = build(entry.getKey(), entry.getValue());
				testSets.add(testSet);
			}
			return testSets;
		}

		private TestSet build(String setName, List<TestDoc> allTestDocsInSet) {
			this.setName = setName;
			testCases = new TestCaseImpl.Builder(setName, allTestDocsInSet).buildAll();
			return new TestSetImpl(this);
		}
	}
}
