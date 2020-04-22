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
package tf.helper;

import tf.helper.doundo.AbstractDoUndoAction;
import tf.helper.doundo.IDoUndoAction;

/**
 *
 * @author thomas
 */
public class TestActions {
    private final static TestActions INSTANCE = new TestActions();
    
    private TestActions() {
    }

    public static TestActions getInstance() {
        return INSTANCE;
    }
    
    public class NumberedAction extends AbstractDoUndoAction {
        private final int myCount;
        
        public NumberedAction(final int count) {
            myCount = count;
        }

        @Override
        public boolean undoHook() {
            System.out.println("Undo for " + getDescription());
            return true;
        }

        @Override
        public boolean doHook() {
            System.out.println("Do for " + getDescription());
            return true;
        }

        @Override
        public String getDescription() {
            return "Action #" + myCount;
        }
    }
    
    public class NoCanUndoAction extends NumberedAction {
        public NoCanUndoAction(int count) {
            super(count);
        }

        @Override
        public boolean canUndo() {
            return false;
        }

        @Override
        public boolean undoHook() {
            System.out.println("No Undo for " + getDescription());
            return false;
        }
    }
    
    public class NoUndoAction extends NumberedAction {
        public NoUndoAction(int count) {
            super(count);
        }

        @Override
        public boolean undoHook() {
            System.out.println("No Undo for " + getDescription());
            return false;
        }
    }
    
    public class NoUndoOnlyOnceAction extends NumberedAction {
        public NoUndoOnlyOnceAction(int count) {
            super(count);
        }

        @Override
        public boolean canDo() {
            return (doneCount() == undoneCount());
        }

        @Override
        public boolean undoHook() {
            System.out.println("No Undo for " + getDescription());
            return false;
        }
    }
    
    public class NoUndoStuckAction extends NumberedAction {
        public NoUndoStuckAction(int count) {
            super(count);
        }

        @Override
        public boolean undoHook() {
            System.out.println("No Undo for " + getDescription());
            return false;
        }
        
        @Override
        public IDoUndoAction.State getStateForFailedUndo() {
            return IDoUndoAction.State.STUCK_IN_UNDO;
        } 
    }
    
    public class NoCanDoAction extends NumberedAction {
        public NoCanDoAction(int count) {
            super(count);
        }

        @Override
        public boolean canDo() {
            return false;
        }

        @Override
        public boolean doHook() {
            System.out.println("No Do for " + getDescription());
            return false;
        }
    }
    
    public class NoDoAction extends NumberedAction {
        public NoDoAction(int count) {
            super(count);
        }

        @Override
        public boolean doHook() {
            System.out.println("No Do for " + getDescription());
            return false;
        }
    }
    
    public class NoDoUndoAction extends NumberedAction {
        public NoDoUndoAction(int count) {
            super(count);
        }

        @Override
        public boolean undoHook() {
            System.out.println("No Undo for " + getDescription());
            return false;
        }

        @Override
        public boolean doHook() {
            System.out.println("No Do for " + getDescription());
            return false;
        }
    }
    
    public NumberedAction getNumberedAction(final int number) {
        return new NumberedAction(number);
    }
    
    public NoCanUndoAction getNoCanUndoAction(final int number) {
        return new NoCanUndoAction(number);
    }
    
    public NoUndoAction getNoUndoAction(final int number) {
        return new NoUndoAction(number);
    }
    
    public NoUndoOnlyOnceAction getNoUndoOnlyOnceAction(final int number) {
        return new NoUndoOnlyOnceAction(number);
    }
    
    public NoUndoStuckAction getNoUndoStuckAction(final int number) {
        return new NoUndoStuckAction(number);
    }
    
    public NoCanDoAction getNoCanDoAction(final int number) {
        return new NoCanDoAction(number);
    }
    
    public NoDoAction getNoDoAction(final int number) {
        return new NoDoAction(number);
    }
    
    public NoDoUndoAction getNoDoUndoAction(final int number) {
        return new NoDoUndoAction(number);
    }
}
