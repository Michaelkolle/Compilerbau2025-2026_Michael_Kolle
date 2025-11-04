import ast.*;
import java.util.*;

public class AstPrettyPrinter {
  private int indent = 0;
  private static final String IND = "    ";

  private String i() {
    return IND.repeat(indent);
  }

  public String print(AstNode node) {
    if (node instanceof ProgramNode p) {
      StringBuilder sb = new StringBuilder();
      for (AstNode s : p.statements) sb.append(print(s));
      return sb.toString();
    }
    if (node instanceof AssignmentNode a) {
      return i() + a.name + " := " + print(a.value) + "\n";
    }
    if (node instanceof IfNode iff) {
      StringBuilder sb = new StringBuilder();
      sb.append(i()).append("if ").append(print(iff.condition)).append(" do\n");
      indent++;
      for (AstNode s : iff.thenBranch) sb.append(print(s));
      indent--;
      if (!iff.elseBranch.isEmpty()) {
        sb.append(i()).append("else do\n");
        indent++;
        for (AstNode s : iff.elseBranch) sb.append(print(s));
        indent--;
      }
      sb.append(i()).append("end\n");
      return sb.toString();
    }
    if (node instanceof WhileNode w) {
      StringBuilder sb = new StringBuilder();
      sb.append(i()).append("while ").append(print(w.condition)).append(" do\n");
      indent++;
      for (AstNode s : w.body) sb.append(print(s));
      indent--;
      sb.append(i()).append("end\n");
      return sb.toString();
    }
    if (node instanceof BinaryOpNode b) {
      return print(b.left) + " " + b.op + " " + print(b.right);
    }
    if (node instanceof VariableNode v) {
      return v.name;
    }
    if (node instanceof LiteralNode l) {
      return l.value;
    }
    return "";
  }
}
