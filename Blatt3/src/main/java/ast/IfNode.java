package ast;

import java.util.*;

public class IfNode extends AstNode {
  public final ExprNode condition;
  public final List<AstNode> thenBranch;
  public final List<AstNode> elseBranch;

  public IfNode(ExprNode condition, List<AstNode> thenBranch, List<AstNode> elseBranch) {
    this.condition = condition;
    this.thenBranch = thenBranch;
    this.elseBranch = elseBranch;
  }
}
