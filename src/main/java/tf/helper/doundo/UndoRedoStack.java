/*
 * Copyright (c) 2014ff Thomas Feuster
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tf.helper.doundo;

import java.util.Stack;

/**
 * Class to hold stacks of undo & redo operations.
 * @author thomas
 */
public class UndoRedoStack<E> {
    private final Stack<E> undoStack;
    private final Stack<E> redoStack;

    // post: constructs an empty UndoRedoStack
    public UndoRedoStack() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

//    // post: pushes and returns the given value on top of the stack
//    @Override
//    public E push(E value) {
//        super.push(value);
//        undoStack.push("push");
//        redoStack.clear();
//        return value;
//    }
//
//    // post: pops and returns the value at the top of the stack
//    @Override
//    public E pop() {
//        E value = super.pop();
//        undoStack.push(value);
//        undoStack.push("pop");
//        redoStack.clear();
//        return value;
//    }
//
//    // post: returns whether or not an undo can be done
//    public boolean canUndo() {
//        return !undoStack.isEmpty();
//    }
//
//    // pre : canUndo() (throws IllegalStateException if not)
//    // post: undoes the last stack push or pop command
//    public void undo() {
//        if (!canUndo()) {
//            throw new IllegalStateException();
//        }
//        Object action = undoStack.pop();
//        if (action.equals("push")) {
//            E value = super.pop();
//            redoStack.push(value);
//            redoStack.push("push");
//        } else {
//            E value = (E) undoStack.pop();         
//            super.push(value);
//            redoStack.push("pop");
//        }
//    }
//
//    // post: returns whether or not a redo can be done
//    public boolean canRedo() {
//        return !redoStack.isEmpty();
//    }
//
//    // pre : canRedo() (throws IllegalStateException if not)
//    // post: redoes the last undone operation
//    public void redo() {
//        if (!canRedo()) {
//            throw new IllegalStateException();
//        }
//        Object action = redoStack.pop();
//        if (action.equals("push")) {
//            E value = (E) redoStack.pop();
//            super.push(value);
//            undoStack.push("push");
//        } else {
//            E value = super.pop();
//            undoStack.push(value);
//            undoStack.push("pop");
//        }
//    }
}
