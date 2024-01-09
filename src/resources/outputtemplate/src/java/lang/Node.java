package lang.ast;
import java.util.List;
import java.util.LinkedList;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

class Node {
  String name;
  List<ASTNode> children;

  public Node(String name, lang.ast.List<ASTNode> children){
    this.name = name;
    this.children = new LinkedList<ASTNode>();
    for (ASTNode n : children)
      this.children.add(n);
  }

  public Node(String name){
    this.name = name;
    this.children = new LinkedList<ASTNode>();
  }

  public ASTNode toASTNode(){
    try {
      Class c = Class.forName("lang.ast." + name);
      for (Constructor constr : c.getConstructors()) {
        Class[] parTypes  = constr.getParameterTypes();
        if(parTypes.length == children.size()) {
          boolean matching = true;
          for (int i = 0; i < parTypes.length; i++) {
            if (!parTypes[i].isAssignableFrom(children.get(i).getClass())) {
              matching=false;
            }
          }
          if (matching) {
            return (ASTNode) constr.newInstance(children.toArray(new Object[0]));
          }
        }
      }
    } catch (Exception x) {
        x.printStackTrace();
    }

    throw new lang.ast.LangParser.SyntaxError("No constructor found: " + name + "(" + children +")");
  }
}
