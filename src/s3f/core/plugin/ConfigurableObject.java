/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.plugin;

/**
 * Classe apenas para testes
 * @author antunes
 */
@Deprecated
public class ConfigurableObject implements Configurable {

    public Data data;

    public ConfigurableObject(String path) {
        data = new Data("tmp", path, "etc");
    }

    @Override
    public Data getData() {
        return data;
    }

}
