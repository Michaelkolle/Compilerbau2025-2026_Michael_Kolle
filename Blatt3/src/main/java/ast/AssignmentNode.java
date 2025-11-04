package ast;

public class AssignmentNode extends AstNode {
  public final String name;
  public final ExprNode value;

  public AssignmentNode(String name, ExprNode value) {
    this.name = name;
    this.value = value;
  }
}
