package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Category {

    private int id;
    private String name;
    private Category parent;
    private List<Category> children;

    public Category(int id, String name) {
        if (id < 0) {
            throw new IllegalArgumentException("ID must be non-negative");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        this.id = id;
        this.name = name;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public Category(int id, String name, Category parent) {
        if (id < 0) {
            throw new IllegalArgumentException("ID must be non-negative");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

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
        if (child == null) {
            throw new IllegalArgumentException("Child category cannot be null");
        }
        if (!children.contains(child)) {
            children.add(child);
        }
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
        return new ArrayList<>(children);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID:      ").append(id).append("\n");
        sb.append("Name:    ").append(name).append("\n");
        sb.append("Path:    ").append(getFullPath()).append("\n");
        sb.append("Is Leaf: ").append(isLeaf()).append("\n");

        if (parent != null) {
            sb.append("Parent:  ").append(parent.getFullPath()).append("\n"); 
        }else {
            sb.append("Parent:  None (root category)\n");
        }

        if (!children.isEmpty()) {
            sb.append("Children:\n");
            for (Category child : children) {
                sb.append("  - ").append(child.getName()).append("\n");
            }
        } else {
            sb.append("Children: None (leaf)\n");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getFullPath() {
        return getCategoryPath().stream()
                .map(Category::getName)
                .collect(Collectors.joining(" > "));
    }
}
