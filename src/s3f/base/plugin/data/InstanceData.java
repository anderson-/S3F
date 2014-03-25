/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s3f.base.plugin.data;

import s3f.base.plugin.AbstractData;
import s3f.base.plugin.Plugabble;

/**
 *
 * @author anderson
 */
public class InstanceData extends AbstractData {

    public static final String FACTORY = "factory";
    
    public InstanceData(String path, String name, String dependencies, Plugabble factory) {
        super(path, name, dependencies);
        setProperty(FACTORY, factory);
    }
}
