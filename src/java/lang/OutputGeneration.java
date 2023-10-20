package lang;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import lang.ast.*;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

class OutputGeneration {
  private Path outputDir;

  public OutputGeneration(Path outputDir) {
    this.outputDir = outputDir;
  }

  public void generateTypeChecker(RuleSet ruleSet) {
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
      BufferedWriter writer = new BufferedWriter(new FileWriter(outputDir + "/src/jastadd/typingrules.jrag"));
      writer.write(typeCheckDef.toString());
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public void prepareOutputDir() {
    try {
      copyFromJar("outputtemplate/", outputDir);
    } catch (URISyntaxException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void copyASTDef(String path) {
    try {
      Files.copy(Paths.get(path), outputDir.resolve("src/jastadd/lang.ast"), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  // https://stackoverflow.com/a/24316335
  private static void copyFromJar(String source, final Path target) throws URISyntaxException, IOException {
    URI resource = OutputGeneration.class.getResource("").toURI();
    FileSystem fileSystem = FileSystems.newFileSystem(
      resource,
      Collections.<String, String>emptyMap()
    );
    final Path jarPath = fileSystem.getPath(source);
    Files.walkFileTree(jarPath, new SimpleFileVisitor<Path>() {
      private Path currentTarget;
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        currentTarget = target.resolve(jarPath.relativize(dir).toString());
        Files.createDirectories(currentTarget);
        return FileVisitResult.CONTINUE;
      }
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.copy(file, target.resolve(jarPath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
      }
    });
  }
}

