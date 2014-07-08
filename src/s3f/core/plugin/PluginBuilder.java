/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.plugin;

/**
 *
 * @author antunes
 */
public abstract class PluginBuilder implements Plugabble {

    public final Data data;
    protected PluginManager pm;

    public PluginBuilder(String pluginName) {
        data = new Data(pluginName, "s3f.pluginbuilder", pluginName);
    }

    public void setPluginManager(PluginManager pm) {
        this.pm = pm;
    }

    /**
     * Inicializar e registrar componentes do plugin aqui.
     */
    @Override
    public abstract void init();

    @Override
    public Data getData() {
        data.setReference(this);
        return data;
    }

    @Override
    public Plugabble createInstance() {
        return null;
    }
}
