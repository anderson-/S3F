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
package s3f.core.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.TreeMap;
import s3f.core.project.FileCreator;
import s3f.core.script.ScriptManager;
import s3f.util.fommil.jni.JniNamer;
import s3f.util.toml.impl.Toml;

public class PluginManager {

    private static PluginManager PLUGIN_MANAGER = null;

    public static PluginManager getInstance() {
        if (PLUGIN_MANAGER == null) {
            PLUGIN_MANAGER = new PluginManager();
            PLUGIN_MANAGER.init();
        }
        return PLUGIN_MANAGER;
    }

    public static ResourceBundle getbundle() {
        return getInstance().defaultBundle;
    }

    public static ResourceBundle getbundle(String pluginShortName) {
        ResourceBundle bundle = getInstance().bundleMap.get(pluginShortName);
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
        ResourceBundle bundle = getInstance().bundleMap.get(pluginShortName);
        if (bundle != null) {
            return bundle.getString(key);
        }
        return "#" + pluginShortName + "#" + key + "#";
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
    private final EntityManager factoryManager;
    private final EntityManager entityManager;

    private PluginManager() {
        factoryTreeRoot = new Data("s3f", "", "Factory Tree Root");
        entityTreeRoot = new Data("root", "", "Entity Tree Root");
        factoryManager = new EntityManager(factoryTreeRoot);
        entityManager = new EntityManager(entityTreeRoot);
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

    private void init() {
        loadPlugins();
        initPlugins();
        loadConfigurationFiles();
        runUserScripts();
    }

    /**
     * Inicializa a árvore do gerenciador de plugins.
     *
     * Este método é responsável por fazer o carregamento, ativação e
     * configuração dos plugins.
     */
    private void loadPlugins() {
        String classRunningPath = PluginManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        ArrayList<String> pluginPaths = new ArrayList<>();

        if (classRunningPath.endsWith(".jar")) {
            int i = classRunningPath.lastIndexOf('/');
            classRunningPath = classRunningPath.substring(0, i);

            File pluginsDir = new File(classRunningPath + "/plugins");

//            ClassLoader p = loadPlugin(classRunningPath + "/plugins/Magenta.jar", this.getClass().getClassLoader());
//            System.out.println(p);
//            p = loadPlugin(classRunningPath + "/plugins/JIFI.jar", p);
//            System.out.println(p);
            if (pluginsDir.isDirectory()) {
                File[] directoryListing = pluginsDir.listFiles();
                if (directoryListing != null) {
                    for (File child : directoryListing) {
                        if (child.getName().endsWith(".jar")) {
                            pluginPaths.add(child.getAbsolutePath());
                        }
                    }
                } else {
                    // Handle the case where dir is not really a directory.
                    // Checking dir.isDirectory() above would not be sufficient
                    // to avoid race conditions with another process that deletes
                    // directories.
                }
            }

            boolean load = true;
            ClassLoader classLoader = this.getClass().getClassLoader();
            while (!pluginPaths.isEmpty() && load) {
                load = false;
                for (Iterator<String> it = pluginPaths.iterator(); it.hasNext();) {
                    String path = it.next();
                    int size = pluginList.size();
                    classLoader = loadPlugin(path, classLoader);
                    if (size > pluginList.size()) {
                        load = true;
                        it.remove();
                    }
                }
            }

            File dataDir = new File(classRunningPath + "/data");

        }
        
        loadSoftPlugin("s3f/plugin.cfg");
        
        System.gc();
    }

    private void initPlugins() {
        List<Data> factoriesData = factoryManager.getAllData("s3f.pluginbuilder.*");

        if (factoriesData != null) {
            for (Data d : factoriesData) {
                if (d != null && d.getReference() instanceof PluginBuilder) {
                    PluginBuilder pb = (PluginBuilder) d.getReference();
                    pb.setPluginManager(this);
                    System.out.println(pb.data.getName());
                    pb.init();
                }
            }
        }
    }

    private void loadConfigurationFiles() {
        String classRunningPath = PluginManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        if (classRunningPath.endsWith(".jar")) {
            int i = classRunningPath.lastIndexOf('/');
            classRunningPath = classRunningPath.substring(0, i);

            File scriptsDir = new File(classRunningPath + "/data");

            if (scriptsDir.isDirectory()) {
                File[] directoryListing = scriptsDir.listFiles();
                if (directoryListing != null) {
                    for (File child : directoryListing) {
                        if (child.getName().endsWith(".cfg")) {
                            InputStream is = null;
                            try {
                                is = new FileInputStream(child);
                                if (is != null) {
                                    Data data = factoryManager.getData(child.getName().substring(0, child.getName().lastIndexOf('.')));
                                    if (data != null) {
                                        String cfg = FileCreator.convertInputStreamToString(is);
                                        Toml parser = Toml.parse(cfg);
                                        Map<String, Object> map = parser.getMap("config");
                                        if (map != null) {
                                            for (Entry<String, Object> e : map.entrySet()) {
                                                data.setProperty(e.getKey(), e.getValue());
                                            }
                                        }
                                    }
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
    }

    private void runUserScripts() {
        String classRunningPath = PluginManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        TreeMap<String, String> scripts = new TreeMap<>();
        List<String> suportedExtensions = ScriptManager.getSuportedExtensions();

        if (classRunningPath.endsWith(".jar")) {
            int i = classRunningPath.lastIndexOf('/');
            classRunningPath = classRunningPath.substring(0, i);

            File scriptsDir = new File(classRunningPath + "/myScripts");

            if (scriptsDir.isDirectory()) {
                File[] directoryListing = scriptsDir.listFiles();
                if (directoryListing != null) {
                    for (File child : directoryListing) {
                        for (String ext : suportedExtensions) {
                            if (child.getName().endsWith(ext)) {
                                InputStream is = null;
                                try {
                                    is = new FileInputStream(child);
                                    String script = FileCreator.convertInputStreamToString(is);
                                    scripts.put(child.getName(), script);
                                } catch (FileNotFoundException ex) {
                                } finally {
                                    try {
                                        if (is != null) {
                                            is.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                continue;
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

        for (Entry<String, String> e : scripts.entrySet()) {
//            try {
//                Invocable runScript = ScriptManager.runScript(e.getValue(), e.getKey().substring(e.getKey().lastIndexOf('.') + 1), null);
//                ScriptManager.createDrawingFrame(runScript, 10);
//            } catch (ScriptException ex) {
//                ex.printStackTrace();
//            }
        }
        System.gc();
    }

    /**
     * Exibe as arvores
     *
     * @param out
     */
    public void printTree(PrintStream out) {
        out.println("Factory Tree:");
        factoryTreeRoot.printTree(out);
        out.println("Entity Tree:");
        entityTreeRoot.printTree(out);
    }

    /**
     * Registra instancias de base.
     *
     * Uma instância é de base quando <code>parent = null</code>.
     *
     * @param newRootInstance
     * @param path
     * @return
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
    public ClassLoader loadPlugin(String pathToJar, ClassLoader parent) {
        try {
            File jar = new File(pathToJar);
            //ClassLoader loader = URLClassLoader.newInstance(new URL[]{jar.toURI().toURL()});
            ClassLoader loader = new ParentLastURLClassLoader(new URL[]{jar.toURI().toURL()}, parent);
            load(loader.getResourceAsStream("plugin.cfg"), loader);
            return loader;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return null;
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
        load(FileCreator.convertInputStreamToString(is), loader);
    }

    private boolean validatePlugin(PluginPOJO cfg) {
        boolean value = cfg.name != null
                && cfg.content != null
                //&& cfg.dependencies != null
                //&& cfg.mainClass != null
                && cfg.version != null;
        if (value) {
            for (int i = 0; i < pluginList.size(); i++) {
                if (pluginList.get(i).name.equals(cfg.name)) {
                    System.out.println("already loaded");
                    return false;
                }
            }

            if (cfg.dependencies != null) {
                for (String d : cfg.dependencies) {
                    boolean ok = false;
                    for (PluginPOJO p : pluginList) {
                        if (p.name.equals(d)) {
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        System.out.println("unsatisfied dependencies");
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
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
            System.loadLibrary(libName);//"rxtxSerial"
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

        if (cfg.builder != null) {
            registerClass(cfg.builder, loader);
        }

        //expande o ramo construtor da gui
        if (cfg.guibuilder != null) {
            System.out.println("register class gui");
            registerClass(cfg.guibuilder, loader);
        }
    }

    private void load(String pojo, ClassLoader loader) {
        try {
            Toml parser = Toml.parse(pojo);
            PluginPOJO cfg = parser.getAs("plugin", PluginPOJO.class);
            if (validatePlugin(cfg)) {
                System.out.println("reg plugin: " + cfg.fullName);
                registerPlugin(cfg, loader);
            } else {
                System.out.println("invalid plugin: " + cfg.fullName);
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

    /**
     * Cria um gerenciador de instancias para um determinado modulo extensivel.
     * O modulo é registrado e recebe alterações na arvore especificada, ele
     * também pode utilizar a instancia de EntityManager para iterar sobre um
     * determinado conjunto de elementos.
     *
     * @param user
     * @return
     */
    public EntityManager createEntityManager(Extensible user) {
        if (user != null) {
            entityManager.addListener(user);
            user.loadModulesFrom(entityManager);
        }
        return entityManager;
    }

    public EntityManager createFactoryManager(Extensible user) {
        if (user != null) {
            factoryManager.addListener(user);
            user.loadModulesFrom(factoryManager);
        }
        return factoryManager;
    }

    public Plugabble createRootInstanceOf(String path) {
        Data d = factoryManager.getData(path);
        if (d == null) {
            return null;
        }
        if (d.getReference() instanceof Plugabble) {
            Plugabble plugabble = (Plugabble) d.getReference();
            return registerRootInstance(plugabble.createInstance(), path);
        }
        return null;
    }

    public void registerFactory(Configurable plugabble) {
        addNode(plugabble.getData().getPath(), plugabble.getData(), factoryTreeRoot);
    }

    public void registerEntiry(Configurable plugabble) {
        addNode(plugabble.getData().getPath(), plugabble.getData(), entityTreeRoot);
    }

}
