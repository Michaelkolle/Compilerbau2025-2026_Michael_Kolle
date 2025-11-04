import java.util.*;

// import my.pkg.*;
public class PrettyPrinterVisitor extends Aufgabe3BaseVisitor<String> {
  private int indentLevel = 0;
  private static final String INDENT = "    "; // 4 Leerzeichen

  private String indent() {
    return INDENT.repeat(indentLevel);
  }

  // The rest of the methods remain unchanged

  @Override
  public String visitAssignment(Aufgabe3Parser.AssignmentContext ctx) {
    return indent() + ctx.ID().getText() + " := " + visit(ctx.expr()) + "\n";
  }

  @Override
  public String visitIfRule(Aufgabe3Parser.IfRuleContext ctx) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent()).append("if ").append(visit(ctx.condition())).append(" do\n");
    indentLevel++;

    int stmtCount = ctx.statement().size();
    boolean hasElse = ctx.getChild(ctx.getChildCount() - 2).getText().equals("else");

    if (hasElse) {
      int split = stmtCount / 2; // Annahme: Hälfte if, Hälfte else
      for (int i = 0; i < split; i++) sb.append(visit(ctx.statement(i)));
      indentLevel--;
      sb.append(indent()).append("else do\n");
      indentLevel++;
      for (int i = split; i < stmtCount; i++) sb.append(visit(ctx.statement(i)));
    } else {
      for (var stmt : ctx.statement()) sb.append(visit(stmt));
    }

    indentLevel--;
    sb.append(indent()).append("end\n");
    return sb.toString();
  }

  @Override
  public String visitWhileRule(Aufgabe3Parser.WhileRuleContext ctx) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent()).append("while ").append(visit(ctx.condition())).append(" do\n");
    indentLevel++;
    for (var stmt : ctx.statement()) {
      sb.append(visit(stmt));
    }
    indentLevel--;
    sb.append(indent()).append("end\n");
    return sb.toString();
  }

  @Override
  public String visitCompare(Aufgabe3Parser.CompareContext ctx) {
    return visit(ctx.expr(0)) + " " + ctx.VGLOP().getText() + " " + visit(ctx.expr(1));
  }

  @Override
  public String visitAtom(Aufgabe3Parser.AtomContext ctx) {
    if (ctx.ID() != null) return ctx.ID().getText();
    if (ctx.NUMBER() != null) return ctx.NUMBER().getText();
    if (ctx.STRING() != null) return ctx.STRING().getText();
    if (ctx.expr() != null) return "(" + visit(ctx.expr()) + ")";
    return "";
  }
}
