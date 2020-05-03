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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base class for actions. Implements do / undo counter and their update logic.
 * @author thomas
 */
public abstract class AbstractDoUndoAction implements IDoUndoAction {
    private State state = State.NOT_DONE;
    private int doneCount = 0;
    private int undoneCount = 0;

    @Override
    public State getState() { 
        return state ; 
    }
    
    // methods required by concrete actions
    
    // do the action and return result
    public abstract boolean doHook();
    // undo the action and return result
    public abstract boolean undoHook();
    
    @Override
    public final boolean doAction() {
        if (!canDo()) {
            return false;
        }
        
        boolean result;
        try {
            result = doHook();
        } catch (Exception ex) {
            Logger.getLogger(AbstractDoUndoAction.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        }
        
        if (result) {
            doneCount++;
            setStateFromCounter();
        } else {
            state = getStateForFailedDo();
        }
        
        return result;
    }

    @Override
    public final boolean undoAction() {
        if (!canUndo()) {
            return false;
        }
        
        boolean result;
        try {
            result = undoHook();
        } catch (Exception ex) {
            Logger.getLogger(AbstractDoUndoAction.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        }
        
        if (result) {
            undoneCount++;
            setStateFromCounter();
        } else {
            state = getStateForFailedUndo();
        }
        
        return result;
    }
    
    private void setStateFromCounter() {
        state = getStateFromCounter();
    }
    
    public State getStateFromCounter() {
        if (doneCount > undoneCount) {
            return State.DONE;
        } else {
            return State.NOT_DONE;
        }
    }

    @Override
    public int doneCount() {
        return doneCount;
    }
    
    @Override
    public int undoneCount() {
        return undoneCount;
    }

    @Override
    public State getStateForFailedDo() {
        // we don't hold a grunge after a failed do() and are not STUCK_IN_DO
        return getStateFromCounter();
    }

    @Override
    public State getStateForFailedUndo() {
        // we don't hold a grunge after a failed undo() and are not STUCK_IN_UNDO
        return getStateFromCounter();
    }
}
