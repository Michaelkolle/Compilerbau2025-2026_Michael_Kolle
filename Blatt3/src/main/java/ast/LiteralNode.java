package ast;

public class LiteralNode extends ExprNode {
  public final String value;

  public LiteralNode(String value) {
    this.value = value;
  }
}
