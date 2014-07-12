/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project;

/**
 *
 * @author antunes
 */
public class Resource {

    private ExtensibleElement primary;
    private Element secondary;

    public Resource(ExtensibleElement primary, Element secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public ExtensibleElement getPrimary() {
        return primary;
    }

    public Element getSecondary() {
        return secondary;
    }

    @Override
    public String toString() {
        return secondary.getName();
    }
}
