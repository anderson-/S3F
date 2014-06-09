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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import s3f.base.ui.GUIBuilder;
import s3f.util.fommil.jni.JniNamer;
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

    public static ResourceBundle getbundle() {
        return getPluginManager().defaultBundle;
    }

    public static ResourceBundle getbundle(String pluginShortName) {
        ResourceBundle bundle = getPluginManager().bundleMap.get(pluginShortName);
        if (bundle != null) {
            return bundle;
        } else {
            return getbundle();
        }
    }

    public static String getText(String key) {
        return getbundle().getString(key);
    }

    public static String getText(String pluginShortName, String key) {
        ResourceBundle bundle = getPluginManager().bundleMap.get(pluginShortName);
        if (bundle != null) {
            return bundle.getString(key);
        }
        return "#" + pluginShortName + "#" + key + "#";
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
            //PluginManager.getPluginManager().loadSoftPlugin("s3f/base/plugin.cfg");
        }
        System.gc();
        runUserScripts();
    }

    private static void runUserScripts() {
        String classRunningPath = PluginManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        ArrayList<String> scripts = new ArrayList<>();

        if (classRunningPath.contains(".jar")) {
            int i = classRunningPath.lastIndexOf('/');
            classRunningPath = classRunningPath.substring(0, i);

            File scriptsDir = new File(classRunningPath + "/userjs");

            if (scriptsDir.isDirectory()) {
                File[] directoryListing = scriptsDir.listFiles();
                if (directoryListing != null) {
                    for (File child : directoryListing) {
                        if (child.getName().contains(".js")) {
                            InputStream is = null;
                            try {
                                is = new FileInputStream(child);
                                if (is != null) {
                                    String script = convertInputStreamToString(is);
                                    scripts.add(script);
                                }
                            } catch (FileNotFoundException ex) {
                            } finally {
                                try {
                                    is.close();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                } else {
                    // Handle the case where dir is not really a directory.
                    // Checking dir.isDirectory() above would not be sufficient
                    // to avoid race conditions with another process that deletes
                    // directories.
                }
            }
        }

        for (String script : scripts) {

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
    private final HashMap<String, ResourceBundle> bundleMap;
    private ResourceBundle defaultBundle;

    private PluginManager() {
        factoryTreeRoot = new Data("s3f", "", "Factory Tree Root");
        entityTreeRoot = new Data("root", "", "Entity Tree Root");
        pluginList = new ArrayList<>();
        bundleMap = new HashMap<>();
        defaultBundle = ResourceBundle.getBundle("s3f.lang.lang", new Locale("pt", "BR"), this.getClass().getClassLoader());
//        Locale l = new Locale("pt", "BR");
//        defaultBundle = null;
//        try {
//            URL resource = PluginManager.class.getClassLoader().getResource(l.toString() + ".lang");
//            if (resource == null){
//                resource = PluginManager.class.getClassLoader().getResource("default.lang");
//                if (resource == null){
//                    System.out.println("ERROR D:");
//                }
//            }
//            defaultBundle = new PropertyResourceBundle(resource.openStream());
//        } catch (IOException ex) {
//            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
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
    public Plugabble registerRootInstance(Plugabble newRootInstance, String path) {
        addNode(path, newRootInstance.getData(), entityTreeRoot);
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

    public static String convertInputStreamToString(InputStream is) {
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
        return sb.toString();
    }

    private void load(InputStream is, ClassLoader loader) {
        if (is == null) {
            return;
        }
        load(convertInputStreamToString(is), loader);
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
            addNode(p.getData().getPath(), p.getData(), factoryTreeRoot);
        }
    }

    /**
     * Sets the java library path to the specified path
     *
     * @param path the new library path
     * @throws Exception
     */
    private static void setLibraryPath(String path) throws Exception {
        System.setProperty("java.library.path", path);
        //set sys_paths to null
        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);
    }

    /**
     * Carrega uma biblioteca nativa no caminho especificado.
     *
     * @param libName
     * @param path
     * @return
     */
    private static boolean loadNativeLib(String libName, String path) {

        String defaultPath = System.getProperty("java.library.path");

        try {
            String newPath = path;
            /* Make sure the library is on the java lib path.
             * Make sure you're using System.loadLibrary() correctly. 
             * If your library is called "libSample.so", 
             * the call should be System.loadLibrary("Sample").
             * Consider that there may be an issue with the library under OpenJDK, 
             * and that's the Java VM you're using. 
             * Run the command java -version and if part of the response is 
             * something like OpenJDK Runtime Environment (build 1.6.0_0-b11), 
             * try installing the official Sun JDK and see if that works. */
            setLibraryPath(newPath);
            System.loadLibrary("rxtxSerial");
            return true;
        } catch (Error | Exception e) {
            try {
                setLibraryPath(defaultPath);
                return loadNativeLib(libName);
            } catch (Exception e2) {
                e2.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Carrega uma biblioteca nativa do sistema.
     *
     * @param libName
     * @return
     */
    private static boolean loadNativeLib(String libName) {
        try {
            System.loadLibrary(libName);
            return true;
        } catch (Error | Exception e) {
            return false;
        }
    }

    public void registerPlugin(PluginPOJO cfg, ClassLoader loader)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        //adiciona o plugin na lista
        pluginList.add(cfg);
        //internacionalização
        if (cfg.langFolder != null) {
            bundleMap.put(cfg.name, ResourceBundle.getBundle(cfg.langFolder + ".lang", new Locale("pt", "BR"), loader));
        }

        if (cfg.nativeLibs != null) {
            String path = PluginManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            path = path.substring(0, path.lastIndexOf('/') + 1);
            path += "natives/" + JniNamer.os() + "/" + JniNamer.arch();

            for (String nativeLib : cfg.nativeLibs) {
                loadNativeLib(nativeLib, path);
            }
        }

        //expande a arvore de factories
        for (String className : cfg.content) {
            registerClass(className, loader);
        }

        //define a plataforma
        if (cfg.platform != null && cfg.platform == true) {
            if (factoryTreeRoot.getProperty("platform_name") == null) {
                factoryTreeRoot.setProperty("platform_name", cfg.name);
                factoryTreeRoot.setProperty("platform_version", cfg.version);
            } else {
                System.err.println("Somente uma plataforma pode ser carregada!");
                System.exit(0);
            }
        }

        //expande o ramo construtor da gui
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
            /*  
             Proximas linhas ao serem executadas pelo netbeans geram:
             - java.lang.ClassNotFoundException
             - java.lang.NoClassDefFoundError
             */
//            if (loader instanceof URLClassLoader) {
//                try {
//                    URLClassLoader urlClassLoader = (URLClassLoader) loader;
//                    urlClassLoader.close();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
        }
    }

    private static void addNode(String path, Data node, Data tree) {
        addNode(path.split("\\."), node, tree);
    }

    private static void addNode(String[] path, Data node, Data tree) {
        Data d = Data.addBranch(path, 0, tree);
        if (d != null) {
            d.addChild(node);
        }
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

//    public static String search(String path, Data tree, List<Data> result) {
//        if (path == null || tree == null || result == null) {
//            return null;
//        }
//        List<Data> children = tree.getChildren();
//        if (path.startsWith(tree.getPath())) {
//            if (path.equals(tree.getPath())) {
//                result.add(tree);
//                return null;
//            } else if (children != null) {
//                if (path.equals(tree.getPath() + ".*")) {
//                    for (Data c : children) {
//                        result.add(c);
//                    }
//                    return null;
//                } else {
//                    for (Data c : children) {
//                        if (path.startsWith(c.getPath())) {
//                            if (path.isEmpty()) {
//                                result.add(c);
//                                return null;
//                            } else {
//                                search(path, c, result);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return (result.isEmpty()) ? tree.getPath() : null;
//    }
    public Data getFactoryData() {
        return factoryTreeRoot;
    }

    public Data getFactoryData(String path) {
        ArrayList<Data> result = new ArrayList<>();
        Data.search(path.split("\\."), result, factoryTreeRoot);
        if (result.isEmpty()) {
            System.out.println("IS NULL");
            return null;
        } else {
            return result.get(0);
        }
    }

    public Object getFactoryProperty(String path, String field) {
        ArrayList<Data> result = new ArrayList<>();
        Data.search(path.split("\\."), result, factoryTreeRoot);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0).getProperty(field);
        }
    }

    public Data[] getFactoriesData(String path) {
        ArrayList<Data> result = new ArrayList<>();
        Data.search(path.split("\\."), result, factoryTreeRoot);
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
        Data.search(path.split("\\."), result, factoryTreeRoot);
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
        Data.search(path.split("\\."), result, factoryTreeRoot);
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
        return registerRootInstance(p.createInstance(), path);
    }

    private void addListener(Extensible listener) {

    }

    private void removeListener(Extensible listener) {

    }

    private void notifyListeners() {

    }

}
