package org.tdc.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.tdc.config.book.DocTypeConfig;

/**
 * A {@link TestSet} implementation.
 */
public class TestSetImpl implements TestSet {
	
	private String setName;
	private List<TestCase> testCases;
	
	private TestSetImpl(Builder builder) {
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
		private final Map<String, DocTypeConfig> docTypeConfigs;
		
		private String setName;
		private List<TestCase> testCases;
		
		public Builder(Map<String, Page> pages, Map<String, DocTypeConfig> docTypeConfigs) {
			this.pages = pages;
			this.docTypeConfigs = docTypeConfigs;
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
			testCases = new TestCaseImpl.Builder(setName, allTestDocsInSet, docTypeConfigs).buildAll();
			return new TestSetImpl(this);
		}
	}
}
