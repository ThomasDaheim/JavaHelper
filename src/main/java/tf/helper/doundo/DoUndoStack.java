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

import java.util.List;
import java.util.Stack;

/**
 * Stack of IDoUndoAction's that support single do / undo and rollback & rollforward
 * Doesn't intent to revert to previous state in case a single do / undo of an action fails.
 * Instead FALSE is returned to the caller and leaves it at that.
 * @author thomas
 */
public class DoUndoStack implements IDoUndoStack {
    private final Stack<IDoUndoAction> doStack = new Stack<>();
    private final Stack<IDoUndoAction> undoStack = new Stack<>();
    
    public DoUndoStack() {
    }
    
    public DoUndoStack(final IDoUndoAction action) {
        undoStack.add(action);
    }
    
    public DoUndoStack(final List<IDoUndoAction> actions) {
        undoStack.addAll(actions);
    }
    
    @Override
    public boolean addDoneAction(final IDoUndoAction action, String... key) {
        return undoStack.add(action);
    }
    
    @Override
    public boolean addDoneActions(final List<IDoUndoAction> actions, String... key) {
        return undoStack.addAll(actions);
    }
    
    @Override
    public boolean clear(String... key) {
        doStack.clear();
        undoStack.clear();
        
        return true;
    }

    @Override
    public boolean clearAll() {
        return clear();
    }
    
    @Override
    public boolean canDo(String... key) {
        return !doStack.empty();
    }
    
    @Override
    public boolean canUndo(String... key) {
        return !undoStack.empty();
    }
    
    @Override
    public int getDoStackSize(String... key) {
        return doStack.size();
    }
    
    @Override
    public int getUndoStackSize(String... key) {
        return undoStack.size();
    }

    @Override
    public boolean singleUndo(String... key) {
        if (!canUndo()) {
            return false;
        }

        return singleUndoImpl();
    }
    private boolean singleUndoImpl() {
        final IDoUndoAction action = undoStack.peek();
        if (!action.canUndo()) {
            // can't currently undo, so don't change stack
            return false;
        }
        
        undoStack.pop();
        doStack.push(action);
        
        return action.undoAction();
    }

    @Override
    public boolean singleDo(String... key) {
        if (!canDo()) {
            return false;
        }
        
        return singleDoImpl();
    }
    private boolean singleDoImpl() {
        final IDoUndoAction action = doStack.peek();
        if (!action.canDo()) {
            // can't currently do, so don't change stack
            return false;
        }
        
        doStack.pop();
        undoStack.push(action);
        
        return action.doAction();
    }
    
    @Override
    public boolean rollBack(String... key) {
        if (!canUndo()) {
            return false;
        }

        boolean result = true;
        while (!undoStack.empty()) {
            if (!singleUndoImpl()) {
                result = false;
                break;
            }
        }
        
        return result;
    }
    
    @Override
    public boolean rollForward(String... key) {
        if (!canDo()) {
            return false;
        }

        boolean result = true;
        while (!doStack.empty()) {
            if (!singleDoImpl()) {
                result = false;
                break;
            }
        }
        
        return result;
    }
}
