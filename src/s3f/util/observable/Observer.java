/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.util.observable;

/**
 *
 * @author anderson
 */
public interface Observer <E,V> {
    
    public void update(final E msg, final V info);
   
}
