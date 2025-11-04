package ast;

public class VariableNode extends ExprNode {
  public final String name;

  public VariableNode(String name) {
    this.name = name;
  }
}
