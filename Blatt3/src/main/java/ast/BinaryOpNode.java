package ast;

public class BinaryOpNode extends ExprNode {
  public final ExprNode left;
  public final String op;
  public final ExprNode right;

  public BinaryOpNode(ExprNode left, String op, ExprNode right) {
    this.left = left;
    this.op = op;
    this.right = right;
  }
}
