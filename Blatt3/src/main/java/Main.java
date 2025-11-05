import ast.*;
import java.nio.file.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
  public static void main(String[] args) throws Exception {
    String input = Files.readString(Path.of("example.txt"));

    Aufgabe3Lexer lexer = new Aufgabe3Lexer(CharStreams.fromString(input));
    Aufgabe3Parser parser = new Aufgabe3Parser(new CommonTokenStream(lexer));

    ParseTree tree = parser.program();

    
    ASTBuilderVisitor builder = new ASTBuilderVisitor();
    ProgramNode ast = (ProgramNode) builder.visit(tree);
    
    AstPrettyPrinter printer = new AstPrettyPrinter();
    System.out.println(printer.print(ast));
  }
}
