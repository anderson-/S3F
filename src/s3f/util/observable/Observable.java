/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.util.observable;

/**
 *
 * @author anderson
 */
public interface Observable <E,V> {
    
    public void attach (Observer <E,V> observer);
    
    public void detach (Observer <E,V> observer);
    
}
