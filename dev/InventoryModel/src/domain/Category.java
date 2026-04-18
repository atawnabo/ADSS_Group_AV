
import java.util.ArrayList;
import java.util.List;

public class Category {
    private int id;
    private String name;
    private Category parent;
    private List<Category> children;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    // child category (has parent)
    public Category(int id, String name, Category parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public List<Category> getCategoryPath() {
    List<Category> path = new ArrayList<>();
    if (parent != null) {
        path.addAll(parent.getCategoryPath()); 
    }
    path.add(this);
    return path;
    }
 
    public boolean isLeaf() {
        return children.isEmpty();
    }

    public void addChild(Category child) {
        children.add(child);
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getParent() {
        return parent;
    }

    public List<Category> getChildren() {
        return children;
    }
}