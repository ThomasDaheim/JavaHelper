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

import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TestDoUndoActions {
    private final IDoUndoAction action1 = TestActions.getInstance().getNumberedAction(1);
    private final IDoUndoAction action2 = TestActions.getInstance().getNumberedAction(2);
    private final IDoUndoAction action3 = TestActions.getInstance().getNumberedAction(3);
    private final IDoUndoAction action4 = TestActions.getInstance().getNumberedAction(4);
    private final IDoUndoAction noUndoAction = TestActions.getInstance().getNoUndoAction(11);
    private final IDoUndoAction noUndoOnlyOnceAction = TestActions.getInstance().getNoUndoOnlyOnceAction(21);
    private final IDoUndoAction noUndoStuckAction = TestActions.getInstance().getNoUndoStuckAction(31);
    private final IDoUndoAction noDoAction = TestActions.getInstance().getNoDoAction(41);
    private final IDoUndoAction noDoUndoAction = TestActions.getInstance().getNoDoUndoAction(51);

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
        // don't confuse with exception logging
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
        // don't confuse with exception logging
        Logger.getLogger(AbstractDoUndoAction.class.getName()).setLevel(Level.OFF);

        final DoUndoActionList actionList = new DoUndoActionList();

        actionList.addAction(action1);
        actionList.addAction(action2);
        actionList.addAction(action3);
        actionList.addAction(noDoUndoAction);

        Assert.assertTrue(actionList.undoInReverseOrder());
        
        boolean hasException = false;
        boolean result = true;
        try {
            result = actionList.doAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertFalse(result);
        Assert.assertFalse(hasException);
        
        hasException = false;
        result = true;
        try {
            result = actionList.undoAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertFalse(result);
        Assert.assertFalse(hasException);
        
        hasException = false;
        result = true;
        try {
            result = actionList.doAction();
        } catch (DoUndoException ex) {
            result = false;
            hasException = true;
        }
        Assert.assertFalse(result);
        Assert.assertFalse(hasException);
    }
}
