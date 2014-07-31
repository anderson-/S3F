/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author antunes
 */
public abstract class ComplexElement extends SimpleElement implements ExtensibleElement {

    private final ArrayList<String> compatibleCategories = new ArrayList<>();
    private final ArrayList<Resource> resources = new ArrayList<>();
    private final ArrayList<Object> externalResources = new ArrayList<>();

    public ComplexElement(String name, String iconpath, CategoryData category, Class<? extends Editor>[] editors, String... categories) {
        super(name, iconpath, category, editors);
        compatibleCategories.addAll(Arrays.asList(categories));
    }

    @Override
    public void addResource(Resource resource) {
        resources.add(resource);
    }

    @Override
    public void removeResource(Resource resource) {
        resources.remove(resource);
    }

    @Override
    public List<Resource> getResources() {
        return resources;
    }

    @Override
    public List<String> getCompatibleCategories() {
        return compatibleCategories;
    }

    public void addExternalResource(Object o) {
        externalResources.add(o);
    }
    
    public void removeExternalResource(Object o) {
        externalResources.remove(o);
    }

    public List<Object> getExternalResources() {
        return externalResources;
    }

}
