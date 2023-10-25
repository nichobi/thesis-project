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
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(outputDir + "/src/jastadd/typingrules.jrag"));
      writer.write(ruleSet.generateTypeChecker());
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

