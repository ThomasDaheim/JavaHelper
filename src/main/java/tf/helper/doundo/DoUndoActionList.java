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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action that consists of a list of actions.
 * Handles incomplete do / undo by trying to "roll-back" the actions that have run successfully.
 * If not possible, an DoUndoException is thrown.
 * @author thomas
 */
public class DoUndoActionList extends AbstractDoUndoAction {
    private final List<IDoUndoAction> myActions = new ArrayList<>();
    
    private boolean undoInReverseOrder = true;
    
    public DoUndoActionList() {
        super();
    }
    
    public DoUndoActionList(final boolean order) {
        undoInReverseOrder = order;
    }
    
    public DoUndoActionList(final boolean order, final IDoUndoAction action) {
        undoInReverseOrder = order;
        myActions.add(action);
    }
    
    public DoUndoActionList(final boolean order, final List<IDoUndoAction> actions) {
        undoInReverseOrder = order;
        myActions.addAll(actions);
    }

    public void addAction(final IDoUndoAction action) {
        myActions.add(action);
    }
    
    public void addActions(final List<IDoUndoAction> actions) {
        myActions.addAll(actions);
    }
    
    public boolean undoInReverseOrder() {
        return undoInReverseOrder;
    }
    
    public void setUndoInReverseOrder(final boolean order) {
        undoInReverseOrder = order;
    }
    
    @Override
    public boolean canDo() {
        // I'm only the sum of my actions...
        boolean result = true;
        
        for (IDoUndoAction action : myActions) {
            if (!action.canDo()) {
                result = false;
                break;
            }
        }
        
        return result;
    }
    
    @Override
    public boolean canUndo() {
        // I'm only the sum of my actions...
        boolean result = true;
        
        for (IDoUndoAction action : myActions) {
            if (!action.canUndo()) {
                result = false;
                break;
            }
        }
        
        return result;
    }
    
    @Override
    public boolean undoHook() throws DoUndoException {
        boolean result = true;
        
        final List<IDoUndoAction> undoActions = new ArrayList<>(myActions);
        if (undoInReverseOrder) {
            Collections.reverse(undoActions);
        }
        
        final int undoCount = internalUndo(undoActions);
        if (undoCount < undoActions.size()) {
            result = false;
            
            // restore meaningful state - redo the previous ones
            final List<IDoUndoAction> redoActions = undoActions.subList(0, undoCount+1);
            if (undoInReverseOrder) {
                Collections.reverse(redoActions);
            }

            final int redoCount = internalDo(redoActions);
            if (redoCount < redoActions.size()) {
                // redo has also failed - where screwed
                throw new DoUndoException("Partial redo for undo failed", undoActions, undoCount, redoCount);
            }
        }
        
        return result;
    }
    // do the undos and count the number of successful ones - allows caller to do a partial redo in case of errors
    private int internalUndo(final List<IDoUndoAction> actions) {
        int result = 0;
        
        try {
            for (IDoUndoAction action : actions) {
                if (action.undoAction()) {
                    result++;
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DoUndoActionList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    @Override
    public boolean doHook() throws DoUndoException {
        boolean result = true;
        
        final int doCount = internalDo(myActions);
        if (doCount < myActions.size()) {
            result = false;

            // restore meaningful state - undo the previous ones
            final List<IDoUndoAction> undoActions = myActions.subList(0, doCount+1);
            if (undoInReverseOrder) {
                Collections.reverse(undoActions);
            }

            final int undoCount = internalUndo(undoActions);
            if (undoCount < undoActions.size()) {
                // redo has also failed - where screwed
                throw new DoUndoException("Partial undo for do failed", myActions, doCount, undoCount);
            }
        }
        
        return result;
    }
    // do the redos and count the number of successful ones - allows caller to do a partial undo in case of errors
    private int internalDo(final List<IDoUndoAction> actions) {
        int result = 0;
        
        try {
            for (IDoUndoAction action : actions) {
                if (action.doAction()) {
                    result++;
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DoUndoActionList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

    @Override
    public String getDescription() {
        // TODO: concat descriptions of actions
        return "NOT YET IMPLEMENTED";
    }
}
