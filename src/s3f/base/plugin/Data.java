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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Data {

    public static final String EMPTY_FIELD = "nil";
    public static final String FACTORY_NAME = "factoryName";
    public static final String OBJECT_NAME = "objectName";
    public static final String DEPENDENCIES = "dependencies";

    private HashMap<String, Object> properties = null;
    private ArrayList<DataListener> listeners = null;
    private boolean useAsFactory = true;
    private Data parent = null;
    private String path;
    private ArrayList<Data> children = null;
    private Plugabble reference = null;

    public Data(String path) {
        this.path = path;
    }

    public void addProperty(String propertyName) {
        setProperty(propertyName, EMPTY_FIELD);
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
            throw new Error("Propriedade não existe!");
        }
    }

    public String getPath() {
        return path;
    }

    public List<Data> getChildren() {
        return children;
    }

    public void addChild(Data child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public void printTree() {
        printTree("", true);
    }

    private void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + path + " : " + properties.get(FACTORY_NAME));
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).printTree(prefix + (isTail ? "    " : "│   "), false);
        }
        if (children.size() >= 1) {
            children.get(children.size() - 1).printTree(prefix + (isTail ? "    " : "│   "), true);
        }
    }
}
