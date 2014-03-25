/**
 * PluginManager.java
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import s3f.util.toml.impl.Toml;

public class PluginManager {

    class MTreeNodeData extends AbstractData {

        MTreeNodeData(String path) {
            super(path, "node", "");
        }

        MTreeNodeData(String path, String name) {
            super(path, name, "");
        }

    }

    public static PluginManager PLUGIN_MANAGER = null;

    public static PluginManager getPluginManager() {
        if (PLUGIN_MANAGER == null) {
            PLUGIN_MANAGER = new PluginManager();
        }
        return PLUGIN_MANAGER;
    }

    static void registerInstance(Plugabble newInstance) {

    }

    private final AbstractData factoryTreeRoot;
    private final AbstractData entityTreeRoot;
    private final ArrayList<PluginPOJO> pluginList;

    private PluginManager() {
        factoryTreeRoot = new MTreeNodeData("s3f", "Factory Tree Root");
        entityTreeRoot = new MTreeNodeData("s3f", "Entity Tree Root");
        pluginList = new ArrayList<>();
    }

    public void loadPlugin(String pathToJar) {
        try {
            File jar = new File(pathToJar);
            ClassLoader loader = URLClassLoader.newInstance(new URL[]{jar.toURI().toURL()});
            load(loader.getResource("plugin.cfg"), loader);
        } catch (MalformedURLException ex) {

        }
    }

    @Deprecated
    public void loadFakePlugin(String pathToConfigPOJO) {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        load(loader.getResource(pathToConfigPOJO), loader);
    }

    private void load(URL url, ClassLoader loader) {
        try {
            Toml parser = Toml.parse(new File(url.toURI()));
            PluginPOJO cfg = parser.getAs("plugin", PluginPOJO.class);
            pluginList.add(cfg);
            for (String className : cfg.content) {
                Class c = loader.loadClass(className);
                if (Plugabble.class.isAssignableFrom(c)) {
                    Plugabble p = (Plugabble) c.newInstance();
                    addNode(p.getData(), factoryTreeRoot);
                }
            }

        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    public void addNode(AbstractData node, AbstractData tree) {
        List<AbstractData> children = tree.getChildren();
        boolean newBranch = true;
        for (AbstractData c : children) {
            if (node.getPath().startsWith(factoryTreeRoot.getPath())) {
                addNode(node, c);
                newBranch = false;
                break;
            }
        }
        if (newBranch) {
            String pathEnd = node.getPath().replaceFirst(tree.getPath() + ".", "");
            if (!pathEnd.contains(".")) {
                tree.addChild(node);
            } else {
                int i = pathEnd.indexOf('.');
                AbstractData branch = new MTreeNodeData(pathEnd.substring(0, i));
                tree.addChild(branch);
                addNode(node, branch);
            }
        }
    }

//    public Data search (String path){
//        
//    }
    public String[] getPluginList() {
        throw new Error();
    }

    public int getPluginIndex(String name) {
        throw new Error();
    }

    public void removePlugin(int index) {
        throw new Error();
    }

    public AbstractData getFactoryData(String path) {
        throw new Error();
    }

    public AbstractData getFactoryData(String path, Class filter) {
        throw new Error();
    }

    public Object getFactoryProperty(String path, String field) {
        throw new Error();
    }

    public AbstractData[] getFactoriesData(String path) {
        throw new Error();
    }

    public AbstractData[] getFactoriesData(String path, Class filter) {
        throw new Error();
    }

    public Object[] getFactoriesProperty(String path, String field) {
        throw new Error();
    }

    public EntityManager createEntityManager(Extensible user) {
        throw new Error();
    }

    private void addListener(Extensible listener) {

    }

    private void removeListener(Extensible listener) {

    }

    private void notifyListeners() {

    }

}
