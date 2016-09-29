package org.tdc.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.process.Processor;
import org.tdc.process.ProcessorImpl;
import org.tdc.util.Util;

/**
 * Unit tests for reading a {@link Book} from spreadsheet file, 
 * loading its contained {@link TestDoc}s/{@link TestCase}s/{@link TestSet}s, 
 * and then performing validation.
 */
public class BookReaderTest {

	private static Processor processor;

	@BeforeClass
	public static void setup() {
		Path schemasConfigRoot = Paths.get("testfiles/TDCFiles/Schemas");
		Path booksConfigRoot = Paths.get("testfiles/TDCFiles/Books");
		processor = new ProcessorImpl
				.Builder(schemasConfigRoot, booksConfigRoot)
				.defaultFactories()
				.build();
	}
	
	@Test
	public void testReadBookAndValidate() throws IOException {
		Path bookPath = Paths.get("testfiles/SampleFiles/TestBook.xlsx");
		Path targetPath = Paths.get("testfiles/Temp/TestBookWithLog.xlsx");
		Util.purgeDirectory(Paths.get("testfiles/Temp/ExportRoot"));
		Files.deleteIfExists(targetPath);
		
		processor.loadAndProcessBookWithLogOutput(
				bookPath, true, true, null, null, targetPath, true);

		assertThat(Files.exists(targetPath)).isEqualTo(true);
	}
}
