package lang;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import lang.ast.Expression;

/**
 * Tests for AST printing (dumpTree).
 * This is a parameterized test: one test case is generated for each input
 * file found in TEST_DIRECTORY. Input files should have the ".in" extension.
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
@RunWith(Parameterized.class)
public class TypeCheckTests {
  /** Directory where the test input files are stored. */
  private static final File TEST_DIRECTORY = new File("testfiles/typechecking");

  private final String filename;
  public TypeCheckTests(String testFile) {
    filename = testFile;
  }

  @Test public void runTest() throws Exception {
    Expression expression = (Expression) Util.parse(new File(TEST_DIRECTORY, filename));
    String resolvedType = expression.type().toString();
    ArrayList<String> typeErrors = expression.errors();
    String actual = resolvedType + "\n\n" + String.join("\n", typeErrors);
    Util.compareOutput(actual,
        new File(TEST_DIRECTORY, Util.changeExtension(filename, ".out")),
        new File(TEST_DIRECTORY, Util.changeExtension(filename, ".expected")));
  }

  @Parameters(name = "{0}")
  public static Iterable<Object[]> getTests() {
    return Util.getTestParameters(TEST_DIRECTORY, ".in");
  }
}
