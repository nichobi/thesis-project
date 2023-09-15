package lang;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import lang.ast.*;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

class OutputGeneration {

  public static void generateTypeChecker(RuleSet ruleSet) {
    Map<String, List<Rule>> ruleMap = new HashMap();

    for (Rule r : ruleSet.getRules()) {
      String target = r.target().codeString();
      ruleMap.putIfAbsent(target, new LinkedList());
      ruleMap.get(target).add(r);
    }
    for (Map.Entry<String, List<Rule>> entry : ruleMap.entrySet()) {
      System.out.println(entry.getKey() + ":" + entry.getValue().toString());
    }

    StringBuilder typeCheckDef = new StringBuilder();
    typeCheckDef.append("aspect TypeRules {\n\n");
    for (String k : ruleMap.keySet()) {
      typeCheckDef.append("syn Type " + k + ".type() {\n");
      for (Rule r: ruleMap.get(k)) {
        switch(r.getNumPremises()) {
          case 0:
            typeCheckDef.append(
              r.getConclusion().generateConclusion()
            );
            break;
          default:
            typeCheckDef.append(r.getConclusion().generateDeclarations());
            typeCheckDef.append("if(");
            List<String> premiseStrings = new LinkedList<String>();
            for (Formula p : r.getPremisesList()) {
              premiseStrings.add(p.generatePremise());
            }
            typeCheckDef.append(String.join(" && ", premiseStrings));
            typeCheckDef.append(") {\n");
            typeCheckDef.append(
              r.getConclusion().generateConclusion()
            );
            typeCheckDef.append("\n");
            typeCheckDef.append("}\n");
            typeCheckDef.append("throw new RuntimeException(\"Typechecking failed\");\n");
        }
        typeCheckDef.append("}\n");
      }
    }

    typeCheckDef.append("}");

    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("output/src/jastadd/typechecking.jrag"));
      writer.write(typeCheckDef.toString());
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}

