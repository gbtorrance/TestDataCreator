package org.tdc.rest.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Implementation of {@link BooksProcessor}.
 */
public class BooksProcessorImpl implements BooksProcessor {
	private final Path workingRoot;

	public BooksProcessorImpl(Path workingRoot) {
		this.workingRoot = workingRoot;
	}

	@Override
	public int uploadBookFile(File bookFile) {
		// TODO finish implementation; just the basics for now
		try (FileInputStream bookInput = new FileInputStream(bookFile)) {
			java.nio.file.Path target = workingRoot.resolve("excel.xlsx");
			Files.copy(bookInput, target, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to save uploaded file", e);
		}
		return 123; // TODO remove hardcoded book ID; just temporary
	}
}
