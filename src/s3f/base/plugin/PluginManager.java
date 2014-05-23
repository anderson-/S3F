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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import s3f.base.ui.GUIBuilder;
import s3f.util.toml.impl.Toml;

public class PluginManager {

    private static PluginManager PLUGIN_MANAGER = null;

    public static PluginManager getPluginManager() {
        if (PLUGIN_MANAGER == null) {
            PLUGIN_MANAGER = new PluginManager();
            loadPlugins();
        }
        return PLUGIN_MANAGER;
    }

    /**
     * Inicializa a árvore do gerenciador de plugins.
     *
     * Este método é responsável por fazer o carregamento, ativação e
     * configuração dos plugins.
     */
    private static void loadPlugins() {
        String classRunningPath = PluginManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        if (classRunningPath.contains(".jar")) {
            int i = classRunningPath.lastIndexOf('/');
            classRunningPath = classRunningPath.substring(0, i);

            File pluginsDir = new File(classRunningPath + "/plugins");

            if (pluginsDir.isDirectory()) {
                File[] directoryListing = pluginsDir.listFiles();
                if (directoryListing != null) {
                    for (File child : directoryListing) {
                        if (child.getName().contains(".jar")) {
                            PluginManager.getPluginManager().loadPlugin(child.getAbsolutePath());
                        }
                    }
                } else {
                    // Handle the case where dir is not really a directory.
                    // Checking dir.isDirectory() above would not be sufficient
                    // to avoid race conditions with another process that deletes
                    // directories.
                }
            }

            File dataDir = new File(classRunningPath + "/data");

        } else {
            PluginManager.getPluginManager().loadSoftPlugin("s3f/base/plugin.cfg");
        }
        System.gc();
    }

    /**
     * Árvore de factories.
     *
     * Recebe novos nós apenas quando um plugin é carregado ou ativado.
     */
    private final Data factoryTreeRoot;
    /**
     * Árvore de entidades.
     *
     * Recebe filhos quando novas instancias de base são registradas.
     */
    private final Data entityTreeRoot;
    private final ArrayList<PluginPOJO> pluginList;

    private PluginManager() {
        factoryTreeRoot = new Data("s3f", "Factory Tree Root", "");
        entityTreeRoot = new Data("root", "Entity Tree Root", "");
        pluginList = new ArrayList<>();
    }

    @Deprecated
    public void PRINT_TEST() {
        System.out.println("factoryTreeRoot:");
        factoryTreeRoot.printTree();
        System.out.println("entityTreeRoot:");
        entityTreeRoot.printTree();
    }

    /**
     * Registra instancias de base.
     *
     * Uma instância é de base quando <code>parent = null</code>.
     *
     * @param newRootInstance
     */
    public Plugabble registerRootInstance(Plugabble newRootInstance) {
        addNode(newRootInstance.getData(), entityTreeRoot);
        return newRootInstance;
    }

    /**
     * Carrega um novo plugin...
     *
     * @param pathToJar
     */
    public void loadPlugin(String pathToJar) {
        try {
            File jar = new File(pathToJar);
            //ClassLoader loader = URLClassLoader.newInstance(new URL[]{jar.toURI().toURL()});
            ClassLoader loader = new URLClassLoader(new URL[]{jar.toURI().toURL()});
            load(loader.getResourceAsStream("plugin.cfg"), loader);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Torna a especificação de um plugin no atual classpath válida e disponível
     * para uso.
     *
     * @param pathToConfigPOJO
     * @deprecated
     */
    @Deprecated
    public void loadSoftPlugin(String pathToConfigPOJO) {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        //loader.getResources(pathToConfigPOJO); é melhor?
        load(loader.getResourceAsStream(pathToConfigPOJO), loader);
    }

    private void load(InputStream is, ClassLoader loader) {
        if (is == null) {
            return;
        }
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        load(sb.toString(), loader);
    }

    private boolean validatePlugin(PluginPOJO cfg) {
        boolean value = cfg.name != null
                && cfg.content != null
                //&& cfg.dependencies != null
                //&& cfg.mainClass != null
                && cfg.version != null;
        if (value) {
            for (int i = 0; i < pluginList.size(); i++) {
                if (pluginList.get(i).equals(cfg.name)) {
                    value = false;
                    break;
                }
            }
        }
        return value;
    }

    public void registerClass(String className, ClassLoader loader)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class c = loader.loadClass(className);
        if (Plugabble.class.isAssignableFrom(c)) {
            Plugabble p = (Plugabble) c.newInstance();
            addNode(p.getData(), factoryTreeRoot);
        }
    }

    public void registerPlugin(PluginPOJO cfg, ClassLoader loader)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        //adiciona o plugin na lista
        pluginList.add(cfg);
        //expande a arvore de factories
        for (String className : cfg.content) {
            registerClass(className, loader);
        }
        
        if (cfg.platform != null && cfg.platform == true){
            factoryTreeRoot.setProperty("platform_name", cfg.name);
            factoryTreeRoot.setProperty("platform_version", cfg.version);
        }

        if (cfg.guibuilder != null) {
            registerClass(cfg.guibuilder, loader);
        }
    }

    private void load(String pojo, ClassLoader loader) {
        try {
            Toml parser = Toml.parse(pojo);
            PluginPOJO cfg = parser.getAs("plugin", PluginPOJO.class);
            if (validatePlugin(cfg)) {
                registerPlugin(cfg, loader);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NullPointerException ex) {
            ex.printStackTrace();
        } finally {
            if (loader instanceof URLClassLoader) {
                try {
                    URLClassLoader urlClassLoader = (URLClassLoader) loader;
                    urlClassLoader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void addNode(Data node, Data tree) {
        List<Data> children = tree.getChildren();
        //adiciona nó, encontra nó mais proximo ou cria um novo?
        String nodePath = node.getPath();
        String treePath = tree.getPath();
        String pathEnd = nodePath.replaceFirst(treePath, "");
        if (treePath.length() > 30) {
            //não é possivel adicionar um nó que não comece com 's3f'
            throw new Error("Fix me");
        }
        if (nodePath.equals(treePath) && !pathEnd.contains(".")) {
            node.setLeaf();
            tree.addChild(node);
            return;
        } else if (children != null) {
            for (Data c : children) {
                if (nodePath.startsWith(c.getPath())) {
                    addNode(node, c);
                    return;
                }
            }
        }
        int i = pathEnd.substring(1).indexOf('.');
        if (i > 0) {
            pathEnd = pathEnd.substring(0, i + 1);
        }

        Data branch = new Data(treePath + pathEnd, pathEnd.substring(1), "");
        System.out.println(branch);
        tree.addChild(branch);
        addNode(node, branch);

    }

//    public Data search (String path){
//        ClassLoader
//    }
    /**
     * Obtem a lista de nomes dos plugins instalados.
     *
     * @return
     */
    public String[] getPluginList() {
        int size = pluginList.size();
        String[] ans = new String[size];
        for (int i = 0; i < size; i++) {
            ans[i] = pluginList.get(i).name;
        }
        return ans;
    }

    /**
     * Obte
     *
     * @param name
     * @return
     */
    public int getPluginIndex(String name) {
        for (int i = 0; i < pluginList.size(); i++) {
            if (pluginList.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void removePlugin(int index) {
        throw new Error();
    }

    public static String search(String path, Data tree, List<Data> result) {
        if (path == null || tree == null || result == null) {
            return null;
        }
        List<Data> children = tree.getChildren();
        if (path.startsWith(tree.getPath())) {
            if (path.equals(tree.getPath())) {
                result.add(tree);
                return null;
            } else if (children != null) {
                if (path.equals(tree.getPath() + ".*")) {
                    for (Data c : children) {
                        result.add(c);
                    }
                    return null;
                } else {
                    for (Data c : children) {
                        if (path.startsWith(c.getPath())) {
                            if (path.isEmpty()) {
                                result.add(c);
                                return null;
                            } else {
                                search(path, c, result);
                            }
                        }
                    }
                }
            }
        }
        return (result.isEmpty()) ? tree.getPath() : null;
    }

    public Data getFactoryData() {
        return factoryTreeRoot;
    }

    public Data getFactoryData(String path) {
        ArrayList<Data> result = new ArrayList<>();
        search(path, factoryTreeRoot, result);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public Object getFactoryProperty(String path, String field) {
        ArrayList<Data> result = new ArrayList<>();
        search(path, factoryTreeRoot, result);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0).getProperty(field);
        }
    }

    public Data[] getFactoriesData(String path) {
        ArrayList<Data> result = new ArrayList<>();
        search(path, factoryTreeRoot, result);
        if (result.isEmpty()) {
            return null;
        } else {
            Data[] resultArray = new Data[result.size()];
            resultArray = result.toArray(resultArray);
            return resultArray;
        }
    }

    public Data[] getFactoriesData(String path, Class filter) {
        ArrayList<Data> result = new ArrayList<>();
        search(path, factoryTreeRoot, result);
        if (result.isEmpty()) {
            return null;
        } else {
            Data[] resultArray = new Data[result.size()];
            for (int i = 0; i < resultArray.length; i++) {
                Data data = result.get(i);
                resultArray[i] = (filter.isAssignableFrom(data.getReference().getClass())) ? data : null;
            }
            resultArray = result.toArray(resultArray);
            return resultArray;
        }
    }

    public Object[] getFactoriesProperty(String path, String field) {
        ArrayList<Data> result = new ArrayList<>();
        search(path, factoryTreeRoot, result);
        if (result.isEmpty()) {
            return null;
        } else {
            Object[] propertyArray = new Object[result.size()];
            for (int i = 0; i < propertyArray.length; i++) {
                propertyArray[i] = result.get(i).getProperty(path);
            }
            return propertyArray;
        }
    }

    public EntityManager createEntityManager(Extensible user) {
        throw new Error();
    }

    public Plugabble createRootInstanceOf(String path) {
        Data d = getFactoryData(path);
        if (d == null) {
            return null;
        }
        Plugabble p = d.getReference();
        return registerRootInstance(p.createInstance());
    }

    private void addListener(Extensible listener) {

    }

    private void removeListener(Extensible listener) {

    }

    private void notifyListeners() {

    }

}
