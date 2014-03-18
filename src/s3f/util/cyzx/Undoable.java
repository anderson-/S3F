/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.util.cyzx;

/**
 *
 * @author antunes2
 */
public interface Undoable<T> {

    public T copy();

    public void setState(T state);

}
