package ast;

import java.util.*;

public class WhileNode extends AstNode {
  public final ExprNode condition;
  public final List<AstNode> body;

  public WhileNode(ExprNode condition, List<AstNode> body) {
    this.condition = condition;
    this.body = body;
  }
}
