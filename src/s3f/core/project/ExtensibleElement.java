/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project;

import java.util.List;

/**
 *
 * @author antunes
 */
public interface ExtensibleElement extends Element {

    public void addResource(Resource resource);

    public void removeResource(Resource resource);

    public List<Resource> getResources();
    
    public List<String> getCompatibleCategories();
    
}
