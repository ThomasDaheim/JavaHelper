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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tf.helper.doundo.AbstractDoUndoAction;
import tf.helper.doundo.DoUndoActionList;
import tf.helper.doundo.DoUndoException;
import tf.helper.doundo.IDoUndoAction;

/**
 *
 * @author thomas
 */
public class TestUndoRedo {
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
        public State getStateForFailedUndo() {
            return State.STUCK_IN_UNDO;
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
    
    private final IDoUndoAction action1 = new NumberedAction(1);
    private final IDoUndoAction action2 = new NumberedAction(2);
    private final IDoUndoAction action3 = new NumberedAction(3);
    private final IDoUndoAction action4 = new NumberedAction(4);
    private final IDoUndoAction noUndoAction = new NoUndoAction(11);
    private final IDoUndoAction noUndoOnlyOnceAction = new NoUndoOnlyOnceAction(21);
    private final IDoUndoAction noUndoStuckAction = new NoUndoStuckAction(31);
    private final IDoUndoAction noDoAction = new NoDoAction(41);
    private final IDoUndoAction noDoUndoAction = new NoDoUndoAction(51);

    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testActions() {
        Assert.assertTrue(action1.doAction());
        Assert.assertTrue(action1.undoAction());
        Assert.assertTrue(action1.doAction());
        Assert.assertEquals(2, action1.doneCount());
        Assert.assertEquals(1, action1.undoneCount());
        
        Assert.assertTrue(noUndoAction.doAction());
        Assert.assertFalse(noUndoAction.undoAction());
        // doesn't fail since it can be run multiple times
        Assert.assertTrue(noUndoAction.doAction());
        Assert.assertEquals(2, noUndoAction.doneCount());
        Assert.assertEquals(0, noUndoAction.undoneCount());
        
        Assert.assertTrue(noUndoOnlyOnceAction.doAction());
        Assert.assertFalse(noUndoOnlyOnceAction.undoAction());
        // fails since it can be run only once
        Assert.assertFalse(noUndoOnlyOnceAction.doAction());
        Assert.assertEquals(1, noUndoOnlyOnceAction.doneCount());
        Assert.assertEquals(0, noUndoOnlyOnceAction.undoneCount());
        
        Assert.assertTrue(noUndoStuckAction.doAction());
        Assert.assertFalse(noUndoStuckAction.undoAction());
        // fails since its in STUCK state
        Assert.assertFalse(noUndoStuckAction.doAction());
        Assert.assertEquals(1, noUndoStuckAction.doneCount());
        Assert.assertEquals(0, noUndoStuckAction.undoneCount());
        
        Assert.assertFalse(noDoAction.doAction());
        // undo works since default canUndo()
        Assert.assertTrue(noDoAction.undoAction());
        Assert.assertFalse(noDoAction.doAction());
        Assert.assertEquals(0, noDoAction.doneCount());
        Assert.assertEquals(1, noDoAction.undoneCount());
        
        Assert.assertFalse(noDoUndoAction.doAction());
        // undo fails since no undo
        Assert.assertFalse(noDoUndoAction.undoAction());
        Assert.assertEquals(0, noDoUndoAction.doneCount());
        Assert.assertEquals(0, noDoUndoAction.undoneCount());
    }
    
    @Test
    public void testNormalActionList() {
        final DoUndoActionList actionList = new DoUndoActionList();
        
        actionList.addAction(action1);
        actionList.addAction(action2);
        actionList.addAction(action3);
        actionList.addAction(action4);
        
        boolean hasException = false;
        boolean result = true;
        
        Assert.assertTrue(actionList.undoInReverseOrder());

        try {
            result = actionList.doAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertTrue(result);
        Assert.assertFalse(hasException);
        
        
        try {
            result = actionList.undoAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertTrue(result);
        Assert.assertFalse(hasException);

        // and now reverse the reverse
        actionList.setUndoInReverseOrder(false);
        Assert.assertFalse(actionList.undoInReverseOrder());
        
        try {
            result = actionList.doAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertTrue(result);
        Assert.assertFalse(hasException);
        
        
        try {
            result = actionList.undoAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertTrue(result);
        Assert.assertFalse(hasException);
    }
    
    @Test
    public void testNoUndoActionList() {
        final DoUndoActionList actionList = new DoUndoActionList();

        actionList.addAction(action1);
        actionList.addAction(action2);
        actionList.addAction(action3);
        actionList.addAction(noUndoAction);

        boolean hasException = false;
        boolean result = true;
        
        Assert.assertTrue(actionList.undoInReverseOrder());

        try {
            result = actionList.doAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertTrue(result);
        Assert.assertFalse(hasException);
        
        
        try {
            result = actionList.undoAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertFalse(result);
        Assert.assertFalse(hasException);
    }
    
    @Test
    public void testNoDoActionList() {
        final DoUndoActionList actionList = new DoUndoActionList();

        actionList.addAction(action1);
        actionList.addAction(action2);
        actionList.addAction(action3);
        actionList.addAction(noDoAction);

        boolean hasException = false;
        boolean result = true;
        
        Assert.assertTrue(actionList.undoInReverseOrder());

        try {
            result = actionList.doAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertFalse(result);
        Assert.assertFalse(hasException);
        
        
        try {
            result = actionList.undoAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        // nothing to undo since not done
        Assert.assertTrue(result);
        Assert.assertFalse(hasException);
    }
    
    @Test
    public void testNoDoUndoActionList() {
        final DoUndoActionList actionList = new DoUndoActionList();

        actionList.addAction(action1);
        actionList.addAction(action2);
        actionList.addAction(action3);
        actionList.addAction(noDoUndoAction);

        boolean hasException = false;
        boolean result = true;
        
        Assert.assertTrue(actionList.undoInReverseOrder());

        try {
            result = actionList.doAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertFalse(result);
        Assert.assertTrue(hasException);
        
        
        try {
            result = actionList.undoAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertFalse(result);
        Assert.assertTrue(hasException);
        
        try {
            result = actionList.doAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertFalse(result);
        Assert.assertTrue(hasException);
    }
}
