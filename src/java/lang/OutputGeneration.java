package lang;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.jastadd.JastAdd;
import org.jastadd.ast.AST.Ast;
import org.jastadd.ast.AST.ASTNode;
import org.jastadd.ast.AST.ASTDecl;
import org.jastadd.Configuration;
import java.util.List;

class OutputGeneration {
  public static void generateScanner(List<ASTDecl> terminals) {
    StringBuilder scanDef = new StringBuilder();
    scanDef.append(String.join("\n",
      "package lang.ast; // The generated scanner will belong to the package lang.ast",
      "",
      "import lang.ast.LangParser.Terminals; // The terminals are implicitly defined in the parser",
      "import lang.ast.LangParser.SyntaxError;",
      "",
      "%%",
      "",
      "// define the signature for the generated scanner",
      "%public",
      "%final",
      "%class LangScanner",
      "%extends beaver.Scanner",
      "",
      "// the interface between the scanner and the parser is the nextToken() method",
      "%type beaver.Symbol",
      "%function nextToken",
      "",
      "// store line and column information in the tokens",
      "%line",
      "%column",
      "",
      "// this code will be inlined in the body of the generated scanner class",
      "%{",
      "  private beaver.Symbol sym(short id) {",
      "    return new beaver.Symbol(id, yyline + 1, yycolumn + 1, yylength(), yytext());",
      "  }",
      "%}",
      "",
      "// macros",
      "WhiteSpace = [ ] | \\t | \\f | \\n | \\r",
      "ID = [a-zA-Z0-9-]+",
      "//Numeral = [0-9]+ \".\" [0-9]+",
      "",
      "%%",
      "",
      "// discard whitespace information",
      "{WhiteSpace}  { }",
      "\n"
    ));

    //String[] terminals = {"True", "False", "Zero", "Or"};
    for(ASTDecl t : terminals)
      scanDef.append("\"" + t + "\"  {return sym(Terminals." + t + "); }\n");

    scanDef.append(String.join("\n",
      "",
      "// token definitions",
      "\"(\"           { return sym(Terminals.LPAREN); }",
      "\")\"           { return sym(Terminals.RPAREN); }",
      "\",\"           { return sym(Terminals.COMMA); }",
      "//{ID}          { return sym(Terminals.ID); }",
      "//{Numeral}     { return sym(Terminals.NUMERAL); }",
      "<<EOF>>       { return sym(Terminals.EOF); }",
      "",
      "/* error fallback */",
      "[^]           { throw new SyntaxError(\"Illegal character <\"+yytext()+\">\"); }",
      "\n"
    ));

    //Files.writeString(, scanDef.toString());
    try {
      new File("output/src/scanner/").mkdirs();
      FileWriter fw = new FileWriter("output/src/scanner/scanner.flex");
      fw.write(scanDef.toString());
      fw.close();
    } catch (IOException e) {
      System.out.println("Cannot write scanner");
      e.printStackTrace();
    }
  }
}



