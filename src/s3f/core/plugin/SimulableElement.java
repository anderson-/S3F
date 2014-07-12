/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s3f.core.plugin;

import s3f.core.project.Element;

/**
 *
 * @author antunes
 */
public interface SimulableElement extends Element {
    
    public s3f.core.simulation.System getSystem();
    
}
