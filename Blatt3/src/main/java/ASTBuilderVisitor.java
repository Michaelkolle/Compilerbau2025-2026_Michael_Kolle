import ast.*;
import java.util.*;

public class ASTBuilderVisitor extends Aufgabe3BaseVisitor<AstNode> {

  @Override
  public AstNode visitProgram(Aufgabe3Parser.ProgramContext ctx) {
    ProgramNode program = new ProgramNode();
    for (var stmtCtx : ctx.statement()) {
      program.statements.add(visit(stmtCtx));
    }
    return program;
  }

  @Override
  public AstNode visitAssignment(Aufgabe3Parser.AssignmentContext ctx) {
    String name = ctx.ID().getText();
    ExprNode expr = (ExprNode) visit(ctx.expr());
    return new AssignmentNode(name, expr);
  }

  @Override
  public AstNode visitIfRule(Aufgabe3Parser.IfRuleContext ctx) {
    ExprNode condition = (ExprNode) visitCompare((Aufgabe3Parser.CompareContext) ctx.condition());
    List<AstNode> thenBranch = new ArrayList<>();
    List<AstNode> elseBranch = new ArrayList<>();

    int total = ctx.statement().size();
    if (total == 0) return new IfNode(condition, thenBranch, elseBranch);

    int elseTokenIndex = -1;
    for (int i = 0; i < ctx.getChildCount(); i++) {
      var child = ctx.getChild(i);
      if (child instanceof org.antlr.v4.runtime.tree.TerminalNode
          && "else".equals(child.getText())) {
        elseTokenIndex =
            ((org.antlr.v4.runtime.tree.TerminalNode) child).getSymbol().getTokenIndex();
        break;
      }
    }

    if (elseTokenIndex < 0) {
      
      for (Aufgabe3Parser.StatementContext stmtCtx : ctx.statement()) {
        thenBranch.add(visitStatement(stmtCtx));
      }
    } else {
     
      for (Aufgabe3Parser.StatementContext stmtCtx : ctx.statement()) {
        if (stmtCtx.getStart().getTokenIndex() < elseTokenIndex)
          thenBranch.add(visitStatement(stmtCtx));
        else elseBranch.add(visitStatement(stmtCtx));
      }
    }

    return new IfNode(condition, thenBranch, elseBranch);
  }

  @Override
  public AstNode visitWhileRule(Aufgabe3Parser.WhileRuleContext ctx) {
    ExprNode cond = (ExprNode) visitCompare((Aufgabe3Parser.CompareContext) ctx.condition());
    List<AstNode> body = new ArrayList<>();
    for (Aufgabe3Parser.StatementContext stmt : ctx.statement()) {
      body.add(visitStatement(stmt));
    }
    return new WhileNode(cond, body);
  }

  @Override
  public AstNode visitCompare(Aufgabe3Parser.CompareContext ctx) {
    ExprNode left = (ExprNode) visit(ctx.expr(0));
    ExprNode right = (ExprNode) visit(ctx.expr(1));
    return new BinaryOpNode(left, ctx.VGLOP().getText(), right);
  }

  @Override
  public AstNode visitAddition(Aufgabe3Parser.AdditionContext ctx) {
    ExprNode left = (ExprNode) visit(ctx.multiplication(0));
    for (int i = 1; i < ctx.multiplication().size(); i++) {
      String op = ctx.getChild(2 * i - 1).getText();
      ExprNode right = (ExprNode) visit(ctx.multiplication(i));
      left = new BinaryOpNode(left, op, right);
    }
    return left;
  }

  @Override
  public AstNode visitMultiplication(Aufgabe3Parser.MultiplicationContext ctx) {
    ExprNode left = (ExprNode) visit(ctx.atom(0));
    for (int i = 1; i < ctx.atom().size(); i++) {
      String op = ctx.getChild(2 * i - 1).getText();
      ExprNode right = (ExprNode) visit(ctx.atom(i));
      left = new BinaryOpNode(left, op, right);
    }
    return left;
  }

  @Override
  public AstNode visitAtom(Aufgabe3Parser.AtomContext ctx) {
    if (ctx.NUMBER() != null) return new LiteralNode(ctx.NUMBER().getText());
    if (ctx.STRING() != null) return new LiteralNode(ctx.STRING().getText());
    if (ctx.ID() != null) return new VariableNode(ctx.ID().getText());
    if (ctx.expr() != null) return visit(ctx.expr());
    return null;
  }
}
