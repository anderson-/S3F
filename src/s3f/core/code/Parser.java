/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.code;

/**
 * Codifica A -> B e decodifica B -> A
 * @author antunes
 * @param <A>
 * @param <B>
 */
public interface Parser<A, B> {

    /**
     * 
     * @param a
     * @param b
     * @return 
     */
    public A encode(A a, B b);

    /**
     * 
     * @param b
     * @param a
     * @return 
     */
    public B decode(B b, A a);

}
