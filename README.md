# Typing Rule Compiler

A compiler for typing rules developed for my [thesis](https://github.com/nichobi/thesis).
This readme contains only a quick description of use, please read the thesis for details.

## Usage
First, build the compiler:
```sh
./gradlew build
```

This creates the compiler jar file, which can be run with `java -jar compiler` and takes two arguments.
* A file containing typing rule definitions
* A JastAdd AST definition of the language

Sample files can be found in the [testfiles](testfiles/) directory, and can be run like so:

```sh
java -jar compiler.jar testfiles/ast/typevar.in testfiles/ast/typevar.ast
```

Or using shell expansion:
```sh
java -jar compiler.jar testfiles/ast/typevar.{in,ast}
```

This will compile the typing rules and generate an example project in a `out/` directory.
To build it the typechecker project, run:

```sh
cd out
./gradlew build
```

and then try it on some example files:

```sh
java -jar compiler.jar ../testfiles/typechecker/typevar/typevar.in
java -jar compiler.jar ../testfiles/typechecker/typevar/typevarmismatch.in
```

(Confirmed to work on Java 11, mileage may vary with other versions)

