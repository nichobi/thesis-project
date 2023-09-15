package lang;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import lang.ast.LangParser.SyntaxError;
import lang.ast.*;
import org.jastadd.Configuration;
import org.jastadd.ast.AST.Grammar;
import org.jastadd.ast.AST.ASTDecl;
import org.jastadd.ast.AST.TypeDecl;
import org.jastadd.ast.AST.Component;
import lang.OutputGeneration;

public class Compiler {
	public static void main(String args[]) {
		if (args.length != 2) {
			System.err.println("You must specify a source file and AST");
			System.exit(1);
		}

		try {
			RuleSet ruleSet = (RuleSet) parse(new FileReader(args[0]));

      Configuration config = new Configuration(new String[]{args[1]}, System.err);

      JastAddChild jac = new JastAddChild(config);
      Grammar grammar = jac.buildGrammar();
      for (ASTDecl root: grammar.roots()) {
        System.out.println(root);
        for(ASTDecl sub : root.parents()) {
          System.out.println("  " + sub);
        }
      }
      System.out.println(grammar.subclassMap());
      System.out.println();
      for (ASTDecl i : grammar.subclassMap().keySet()) {
        System.out.println(i);
        System.out.println("  Subclasses:" + grammar.subclassMap().get(i));
        for (Component comp : i.getComponents()) {
          System.out.println("  " + comp);
          System.out.println("    typeDecl: " + comp.typeDecl());
          System.out.println("    hostClass: " + comp.hostClass());
          System.out.println("    name: " + comp.name());
          System.out.println("    type: " + comp.type());
          System.out.println("    kind: " + comp.kind());
        }
      }
      System.out.println();
      System.out.println(grammar.parentMap());
      System.out.println(grammar.getTypeDeclList());

      String[] builtInNames = {"Opt", "List"};
      List<ASTDecl> builtIns = new ArrayList<ASTDecl>();
      for (ASTDecl i : grammar.subclassMap().keySet())
        for ( String b : builtInNames)
          if (i.toString().equals(b))
            builtIns.add(i);

      List<ASTDecl> terminals = new ArrayList<ASTDecl>();
      for (ASTDecl i : grammar.subclassMap().keySet()) {
        if (grammar.subclassMap().get(i).isEmpty()) {
          terminals.add(i);
        }
      }
      //terminals.removeAll(grammar.roots());
      terminals.removeAll(builtIns);

      for (ASTDecl i : terminals)
        System.out.println("Terminal: " + i);

      OutputGeneration.generateTypeChecker(ruleSet);

//      for (TypeDecl td: grammar.findSubClasses(grammar.roots().get(0))) {
//        System.out.println(td);
//      }



		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}

	private static Object parse(Reader reader) {
		LangScanner scanner = new LangScanner(reader);
		LangParser parser = new LangParser();

		try {
			return parser.parse(scanner);
		} catch (SyntaxError | beaver.Parser.Exception e) {
			System.err.println("Syntax error: " + e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
    return null;
  }
}
