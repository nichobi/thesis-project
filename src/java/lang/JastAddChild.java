package lang;

import org.jastadd.JastAdd;
import org.jastadd.Configuration;
import org.jastadd.ast.AST.Grammar;
import org.jastadd.Problem;

import java.lang.reflect.Method;
import java.util.Collection;

public class JastAddChild extends JastAdd {

  Configuration configuration;

  public JastAddChild(Configuration config) {
    super(config);
    configuration = config;



      //if (checkErrors(readAstFiles(grammar, configuration.getFiles()), System.err)) {
      //  throw new RuntimeException("");
      //  //return 1;
      //}
  }

  public Grammar buildGrammar() {
      Grammar grammar = configuration.buildRoot();

      if (configuration.generateImplicits()) {
        grammar.addImplicitNodeTypes();
      }

      try {
        Method method = JastAdd.class.getDeclaredMethod("readAstFiles", Grammar.class, Collection.class);
        method.setAccessible(true);
        Collection<Problem> problems = (Collection<Problem>) method.invoke(this, grammar, configuration.getFiles());
        for (Problem p : problems) {
          System.err.println(p);
        }
      } catch(Throwable t) {
        throw new RuntimeException(t);
      }

      return grammar;
  }
}
