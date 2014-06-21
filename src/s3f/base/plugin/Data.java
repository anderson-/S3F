/**
 * Data.java
 *
 * Copyright (C) 2014
 *
 * Anderson de Oliveira Antunes <anderson.utf@gmail.com>
 *
 * This file is part of S3F.
 *
 * S3F is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * S3F is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * S3F. If not, see http://www.gnu.org/licenses/.
 */
package s3f.base.plugin;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class Data {

    public static final String _EMPTY_FIELD = "nil";
    public static final String DEPENDENCIES = "dependencies";
    public static final String SINGLETON = "singleton";
    public static final String FACTORY = "factory";

    private HashMap<String, Object> properties = null;
    private ArrayList<DataListener> listeners = null;
    private final String fullName;
    private boolean useAsFactory = false;
    private Data parent = null;
    private String name;
    private String path;
    private ArrayList<Data> children = null;
    private Plugabble reference = null;

    public Data(String name, String path, String fullName) {
        this.name = name;
        this.path = path;
        this.fullName = fullName;
    }

    public Data(String name, String path, String fullName, Plugabble reference) {
        this.name = name;
        this.path = path;
        this.fullName = fullName;
        this.reference = reference;
    }

    public String getFullName() {
        return fullName;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String calcPath() {
        String path = buildPathString(new StringBuilder()).toString();
        return path.substring(0, path.length() - 1);
    }

    private StringBuilder buildPathString(StringBuilder sb) {
        if (parent != null) {
            parent.buildPathString(sb);
        }
        sb.append(name).append('.');
        return sb;
    }

    public String getDependencies() {
        return getProperty(Data.DEPENDENCIES);
    }

    public void setReference(Plugabble reference) {
        this.reference = reference;
    }

    public Plugabble getReference() {
        return reference;
    }

    public Data getParent() {
        return parent;
    }

    public Data addChild(Data child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        return child;
    }

    public List<Data> getChildren() {
        return children;
    }

    public boolean delete() {
        if (parent != null) {
            return parent.children.remove(this);
        }
        return false;
    }

    public void addProperty(String propertyName) {
        setProperty(propertyName, _EMPTY_FIELD);
    }

    public void setProperty(String propertyName, Object value) {
        if (properties == null) {
            properties = new HashMap<>();
        }
        properties.put(propertyName, value);
    }

    public <T> T getProperty(String propertyName) {
        if (properties != null) {
            return (T) properties.get(propertyName);
        } else {
            return null;
        }
    }

    public void addListener(DataListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    public void removeListener(DataListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    private void notifyListeners(String change) {
        //TODO
    }

    public void printTree(PrintStream out) {
        printTree("", true, out);
    }

    private void printTree(String prefix, boolean isTail, PrintStream out) {
        out.println(prefix + (isTail ? " '--> " : " +--> ") + name + " - \"" + fullName + "\"");
        if (children != null) {
            for (int i = 0; i < children.size() - 1; i++) {
                children.get(i).printTree(prefix + (isTail ? "      " : " :    "), false, out);
            }
            if (children.size() >= 1) {
                children.get(children.size() - 1).printTree(prefix + (isTail ? "      " : " :    "), true, out);
            }
        }
    }

    public static List<Data> search(String[] path, List<Data> list, Data root) {
        return search(path, 0, list, root);
    }

    /**
     *
     * 
     *
     * @param path
     * @param i
     * @param list
     * @param root
     * @return
     */
    private static List<Data> search(String[] path, int i, List<Data> list, Data root) {
        if (i >= path.length) {
            return list;
        } else if (path[i].equals(root.name)) {
            if (i == path.length - 1) {
                list.add(root);
            } else if (i < path.length - 1) {
                if (path[i + 1].equals("*")) {
                    list.addAll(root.children);
                } else {
                    if (root.children != null) {
                        for (Data c : root.children) {
                            search(path, i + 1, list, c);
                        }
                    }
                }
            }
        }
        return list;
    }

    public static Data addBranch(String[] path, int i, Data root) {
        if (i >= path.length) {
            return root;
        } else if (path[i].equals(root.name)) {
            return addBranch(path, i + 1, root);

        } else if (root.children != null) {
            for (Data c : root.children) {
                if (c.name.equals(path[i])) {
                    return addBranch(path, i + 1, c);
                }
            }
        }

        root = root.addChild(new Data(path[i], "", "empty"));
        return addBranch(path, i + 1, root);
    }

    @Override
    public String toString() {
        return "Data{" + "fullName=" + fullName + ", name=" + name + ", children=" + ((children == null) ? -1 : children.size()) + '}';
    }

}
