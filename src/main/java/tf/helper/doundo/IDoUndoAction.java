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

/**
 * See also http://sourcephile.blogspot.com/2011/04/how-to-do-undo-and-redo.html
 * @author thomas
 */
public interface IDoUndoAction {
    public enum State {
        DONE, 
        NOT_DONE, 
        STUCK_IN_DO,
        STUCK_IN_UNDO;
        
        public boolean isStuck() {
            return STUCK_IN_DO.equals(this) || STUCK_IN_UNDO.equals(this);
        }
    } ;
    
    abstract public State getState();
    
    default public boolean canDo() {
        // thats the basic "definition": if you're stuck you can't do anything
        // a lot of options here, e.g. 
        // must be NOT_DONE => action, that can be done only once
        // doneCount() == undoneCount() => multiple do / undo's possible as long as same amount => net "nothing" has been done
        return !getState().isStuck();
    }
    default public boolean canUndo() {
        // thats the basic "definition": if you're stuck you can't do anything
        // a lot of options here, e.g. 
        // must be DONE => action where undo without previous do is not meaningful
        // doneCount() > undoneCount() => multiple do / undo's possible as long as one more do has happened
        return !getState().isStuck();
    }

    abstract public boolean doAction();
    abstract public boolean undoAction();
    
    abstract public int doneCount();
    abstract public int undoneCount();

    // what is your state if do fails? might not be "STUCK", e.g. in case of network error could simply be done later on
    public abstract State getStateForFailedDo();
    // what is your state if undo fails?
    public abstract State getStateForFailedUndo();
    
    abstract public String getDescription();
}
