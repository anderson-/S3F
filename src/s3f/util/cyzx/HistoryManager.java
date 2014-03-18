/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.util.cyzx;

import java.util.Stack;

/**
 *
 * @author antunes2
 */
public class HistoryManager<T> {

    private final Stack<T> undo;
    private final Stack<T> redo;
    private final Undoable<T> undoable;
    private int block;
    private int blockSize = 0;
    private int capacity = Integer.MAX_VALUE;

    public HistoryManager(Undoable<T> restorable) {
        this.undoable = restorable;
        undo = new Stack<>();
        redo = new Stack<>();
    }

    public HistoryManager(Undoable<T> restorable, int blockSize) {
        this(restorable);
        this.blockSize = blockSize;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void saveState() {
        if (block > 0) {
            block--;
            return;
        }
        T t = undoable.copy();
        if (t != null) {
            undo.add(t);
//            System.out.println("undo<<" + undo.peek());
            while (undo.size() > capacity) {
                undo.remove(10);
            }
        }
    }

    private void pushRedo() {
        T t = undoable.copy();
        if (t != null) {
            redo.add(t);
//            System.out.println("redo<<" + redo.peek());
            while (redo.size() > capacity) {
                redo.remove(10);
            }
        }
    }

    public void undo() {
        if (undo.size() > 0) {
            pushRedo();
//            System.out.println("undo>>" + undo.peek());
            block = blockSize;
            undoable.setState(undo.pop());
        }
    }

    public void redo() {
        if (redo.size() > 0) {
            saveState();
//            System.out.println("redo>>" + redo.peek());
            block = blockSize;
            undoable.setState(redo.pop());
        }
    }

}
