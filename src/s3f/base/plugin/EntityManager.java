/**
 * EntityManager.java
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
import java.util.Iterator;
import java.util.List;

public class EntityManager {

    private final Data treeRoot;

    EntityManager(Data treeRoot) {
        this.treeRoot = treeRoot;
    }

    EntityManager(Data treeRoot, Extensible listener) {
        this.treeRoot = treeRoot;
        addListener(listener);
    }

    public Data getData() {
        return treeRoot;
    }

    public Data getData(String path) {
        ArrayList<Data> result = new ArrayList<>();
        Data.search(path.split("\\."), result, treeRoot);
        if (result.isEmpty()) {
            System.out.println("IS NULL");
            return null;
        } else {
            return result.get(0);
        }
    }

    public Object getProperty(String path, String field) {
        ArrayList<Data> result = new ArrayList<>();
        Data.search(path.split("\\."), result, treeRoot);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0).getProperty(field);
        }
    }

    public List<Data> getAllData(String path) {
        ArrayList<Data> result = new ArrayList<>();
        Data.search(path.split("\\."), result, treeRoot);
        if (result.isEmpty()) {
            return null;
        } else {
            return result;
        }
    }

    public List<Data> getAllData() {
        return getAllData(treeRoot, new ArrayList<Data>(), false);
    }

    private List<Data> getAllData(Data data, ArrayList<Data> result, boolean add) {
        if (add) {
            result.add(data);
        }
        List<Data> children = data.getChildren();
        if (children != null) {
            for (Data d : children) {
                getAllData(d, result, true);
            }
        }
        return result;
    }

    public List<Data> getAllData(String path, Class filter) {
        ArrayList<Data> result = new ArrayList<>();
        Data.search(path.split("\\."), result, treeRoot);
        if (result.isEmpty()) {
            return null;
        } else {
            for (Iterator<Data> it = result.iterator(); it.hasNext();) {
                Data data = it.next();
                if (!filter.isAssignableFrom(data.getReference().getClass())) {
                    it.remove();
                }
            }
            return result;
        }
    }

    public List<Object> getAllProperties(String path, String field) {
        ArrayList<Data> result = new ArrayList<>();
        Data.search(path.split("\\."), result, treeRoot);
        if (result.isEmpty()) {
            return null;
        } else {
            ArrayList properties = new ArrayList(result.size());
            for (Data data : result) {
                properties.add(data.getProperty(path));
            }
            return properties;
        }
    }

    public <T> List<T> getEntities(String path) {
        List<Data> allData = getAllData(path);
        ArrayList<T> result = new ArrayList<>();
        if (allData != null) {
            for (Data data : allData) {
                try {
                    T t = (T) data.getReference();
                    result.add(t);
                } catch (ClassCastException e) {
                }
            }
        }
        return result;
    }

    public <T> List<T> getAllEntities() {
        List<Data> allData = getAllData();
        ArrayList<T> result = new ArrayList<>();
        if (allData != null) {
            for (Data data : allData) {
                try {
                    T t = (T) data.getReference();
                    result.add(t);
                } catch (ClassCastException e) {
                }
            }
        }
        return result;
    }

    /**
     * Registra um objeto para receber atualiação de novas coisas
     * (Factories/Entities) registradas.
     *
     * @param listener
     */
    public final void addListener(Extensible listener) {

    }

    public final void removeListener(Extensible listener) {

    }

    private void notifyListeners() {

    }

}
