package lang;

import java.io.File;

import org.junit.Test;

public class ParseTests {
	/** Directory where the test input files are stored. */
	private static final File TEST_DIRECTORY = new File("testfiles/parser");

	@Test
	public void bools() {
		Util.testValidSyntax(TEST_DIRECTORY, "bools.in");
	}

}
