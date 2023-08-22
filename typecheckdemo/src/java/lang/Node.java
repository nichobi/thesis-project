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
      //System.out.println("Children: " + children.size());
      //System.out.println("Constructors found: " + c.getDeclaredConstructors().length);
      for (Constructor constr : c.getDeclaredConstructors()) {
        //System.out.println("Parameters: " + constr.getParameterTypes().length);
        Class[] parTypes  = constr.getParameterTypes();
        if(parTypes.length == children.size()) {
          boolean matching = true;
          for (int i = 0; i < parTypes.length; i++) {
            //System.out.println("parType: " + parTypes[i] + ", childClass: " + children.get(i).getClass());
            if (!parTypes[i].isAssignableFrom(children.get(i).getClass())) {
              //System.out.println("Failed match");
              matching=false;
            }
          }
          if (matching) {
            //System.out.println("Returning ASTNode: " + name + "(" + children +")");
            return (ASTNode) constr.newInstance(children.toArray(new Object[0]));
          }
        }
      }
    } catch (Exception x) {
        x.printStackTrace();
    }

    throw new RuntimeException("No constructor found: " + name + "(" + children +")");
  }
}
