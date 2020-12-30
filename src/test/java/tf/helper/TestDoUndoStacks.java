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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tf.helper.doundo.DoUndoManager;
import tf.helper.doundo.DoUndoStack;
import tf.helper.doundo.IDoUndoAction;
import tf.helper.doundo.IDoUndoStack;

/**
 *
 * @author thomas
 */
public class TestDoUndoStacks {
    private final IDoUndoAction action1 = TestActions.getInstance().getNumberedAction(1);
    private final IDoUndoAction action2 = TestActions.getInstance().getNumberedAction(2);
    private final IDoUndoAction action3 = TestActions.getInstance().getNumberedAction(3);
    private final IDoUndoAction action4 = TestActions.getInstance().getNumberedAction(4);
    private final IDoUndoAction noCanUndoAction = TestActions.getInstance().getNoCanUndoAction(11);
    private final IDoUndoAction noCanDoAction = TestActions.getInstance().getNoCanDoAction(41);

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
    
    // general stack tests
    private void testEmptyStack(final IDoUndoStack stack, final String key) {
        // nothing to do yet
        Assert.assertFalse(stack.canDo(key));
        Assert.assertFalse(stack.canUndo(key));
        Assert.assertEquals(0, stack.getDoStackSize(key));
        Assert.assertEquals(0, stack.getUndoStackSize(key));

        Assert.assertFalse(stack.singleDo(key));
        Assert.assertFalse(stack.rollForward(key));
        Assert.assertFalse(stack.singleUndo(key));
        Assert.assertFalse(stack.rollBack(key));
    }
    private void testClearStack(final IDoUndoStack stack, final String key) {
        // clear up
        Assert.assertTrue(stack.clear(key));
        Assert.assertFalse(stack.canDo(key));
        Assert.assertFalse(stack.canUndo(key));
        Assert.assertEquals(0, stack.getDoStackSize(key));
        Assert.assertEquals(0, stack.getUndoStackSize(key));

        Assert.assertFalse(stack.singleDo(key));
        Assert.assertFalse(stack.rollForward(key));
        Assert.assertFalse(stack.singleUndo(key));
        Assert.assertFalse(stack.rollBack(key));
    }

    // tests on "good" stack with good actions
    private void testGoodFilledStack(final IDoUndoStack stack, final String key) {
        // add some good actions
        stack.addDoneAction(action1, key);
        stack.addDoneActions(Arrays.asList(action2, action3, action4), key);
        Assert.assertFalse(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(0, stack.getDoStackSize(key));
        Assert.assertEquals(4, stack.getUndoStackSize(key));

        Assert.assertFalse(stack.singleDo(key));
        Assert.assertFalse(stack.rollForward(key));
    }
    private void testUndoGoodStack(final IDoUndoStack stack, final String key) {
        // undo some til all
        Assert.assertTrue(stack.singleUndo(key));
        Assert.assertTrue(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(1, stack.getDoStackSize(key));
        Assert.assertEquals(3, stack.getUndoStackSize(key));

        Assert.assertTrue(stack.rollBack(key));
        Assert.assertTrue(stack.canDo(key));
        Assert.assertFalse(stack.canUndo(key));
        Assert.assertEquals(4, stack.getDoStackSize(key));
        Assert.assertEquals(0, stack.getUndoStackSize(key));
    }
    private void testDoGoodStack(final IDoUndoStack stack, final String key) {
        // do some til all
        Assert.assertTrue(stack.singleDo(key));
        Assert.assertTrue(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(3, stack.getDoStackSize(key));
        Assert.assertEquals(1, stack.getUndoStackSize(key));

        Assert.assertTrue(stack.rollForward(key));
        Assert.assertFalse(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(0, stack.getDoStackSize(key));
        Assert.assertEquals(4, stack.getUndoStackSize(key));
    }
    private void testDoUndoGoodStack(final IDoUndoStack stack, final String key) {
        // undo / do back and forth
        Assert.assertTrue(stack.singleUndo(key)); // 1 do, 3 undo
        Assert.assertTrue(stack.singleUndo(key)); // 2 do, 2 undo
        Assert.assertTrue(stack.singleDo(key)); // 3 do, 1 undo
        Assert.assertTrue(stack.singleUndo(key)); // 2 do, 2 undo
        Assert.assertTrue(stack.singleUndo(key)); // 3 do, 1 undo
        Assert.assertTrue(stack.singleDo(key)); // 2 do, 2 undo
        Assert.assertTrue(stack.singleDo(key)); // 1 do, 3 undo
        Assert.assertTrue(stack.singleDo(key)); // 0 do, 4 undo

        Assert.assertFalse(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(0, stack.getDoStackSize(key));
        Assert.assertEquals(4, stack.getUndoStackSize(key));

        Assert.assertTrue(stack.singleUndo(key)); // 1 do, 3 undo
        Assert.assertTrue(stack.singleDo(key)); // 0 do, 4 undo
        Assert.assertFalse(stack.singleDo(key)); // 0 do, 4 undo + FAILS
        Assert.assertTrue(stack.singleUndo(key)); // 1 do, 3 undo
        Assert.assertTrue(stack.singleUndo(key)); // 2 do, 2 undo
        Assert.assertTrue(stack.singleUndo(key)); // 3 do, 1 undo
        Assert.assertTrue(stack.singleUndo(key)); // 4 do, 0 undo
        Assert.assertFalse(stack.singleUndo(key)); // 4 do, 0 undo + FAILS
        Assert.assertTrue(stack.singleDo(key)); // 3 do, 1 undo
        Assert.assertTrue(stack.singleDo(key)); // 2 do, 2 undo
        Assert.assertTrue(stack.singleDo(key)); // 1 do, 3 undo

        Assert.assertTrue(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(1, stack.getDoStackSize(key));
        Assert.assertEquals(3, stack.getUndoStackSize(key));
    }
    
    // tests on "bad" stack with actions that can't do / undo
    // 1st: good
    // 2nd: can't do
    // 3rd: good
    // 4th can't undo
    private void testBadFilledStack(final IDoUndoStack stack, final String key) {
        // add some good actions
        stack.addDoneAction(noCanUndoAction, key);
        stack.addDoneActions(Arrays.asList(action2, noCanDoAction, action4), key);
        Assert.assertFalse(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(0, stack.getDoStackSize(key));
        Assert.assertEquals(4, stack.getUndoStackSize(key));

        Assert.assertFalse(stack.singleDo(key));
        Assert.assertFalse(stack.rollForward(key));
    }
    private void testUndoBadStack(final IDoUndoStack stack, final String key) {
        // undo some til all
        Assert.assertTrue(stack.singleUndo(key));
        Assert.assertTrue(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(1, stack.getDoStackSize(key));
        Assert.assertEquals(3, stack.getUndoStackSize(key));

        // fails on 4th action
        Assert.assertFalse(stack.rollBack(key));
        Assert.assertTrue(stack.canDo(key));
        // still some undo left
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(3, stack.getDoStackSize(key));
        Assert.assertEquals(1, stack.getUndoStackSize(key));
    }
    private void testDoBadStack(final IDoUndoStack stack, final String key) {
        // do some til all
        Assert.assertTrue(stack.singleDo(key));
        Assert.assertTrue(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(2, stack.getDoStackSize(key));
        Assert.assertEquals(2, stack.getUndoStackSize(key));

        // fails on 3rd action
        Assert.assertFalse(stack.rollForward(key));
        // stil some do left
        Assert.assertTrue(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(2, stack.getDoStackSize(key));
        Assert.assertEquals(2, stack.getUndoStackSize(key));
    }
    private void testDoUndoBadStack(final IDoUndoStack stack, final String key) {
        // undo / do back and forth
        Assert.assertTrue(stack.singleUndo(key)); // 3 do, 1 undo
        Assert.assertFalse(stack.singleUndo(key)); // 3 do, 1 undo + FAILS
        Assert.assertTrue(stack.singleDo(key)); // 2 do, 2 undo
        Assert.assertTrue(stack.singleUndo(key)); // 3 do, 1 undo
        Assert.assertFalse(stack.singleUndo(key)); // 3 do, 1 undo + FAILS
        Assert.assertTrue(stack.singleDo(key)); // 2 do, 2 undo
        Assert.assertFalse(stack.singleDo(key)); // 2 do, 2 undo + FAILS

        Assert.assertTrue(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(2, stack.getDoStackSize(key));
        Assert.assertEquals(2, stack.getUndoStackSize(key));

        Assert.assertTrue(stack.singleUndo(key)); // 3 do, 1 undo
        Assert.assertTrue(stack.singleDo(key)); // 2 do, 2 undo
        Assert.assertFalse(stack.singleDo(key)); // 2 do, 2 undo + FAILS
        Assert.assertTrue(stack.singleUndo(key)); // 3 do, 1 undo
        Assert.assertFalse(stack.singleUndo(key)); // 3 do, 1 undo + FAILS
        Assert.assertTrue(stack.singleDo(key)); // 2 do, 2 undo
        Assert.assertFalse(stack.singleDo(key)); // 2 do, 2 undo + FAILS

        Assert.assertTrue(stack.canDo(key));
        Assert.assertTrue(stack.canUndo(key));
        Assert.assertEquals(2, stack.getDoStackSize(key));
        Assert.assertEquals(2, stack.getUndoStackSize(key));
    }
    
    private void testGoodStack(final IDoUndoStack stack, final String key) {
        testEmptyStack(stack, key);
        testGoodFilledStack(stack, key);
        
        testUndoGoodStack(stack, key);
        testDoGoodStack(stack, key);
        testDoUndoGoodStack(stack, key);

        testClearStack(stack, key);
    }
    
    private void testBadStack(final IDoUndoStack stack, final String key) {
        testEmptyStack(stack, key);
        testBadFilledStack(stack, key);
        
        testUndoBadStack(stack, key);
        testDoBadStack(stack, key);
        testDoUndoBadStack(stack, key);

        testClearStack(stack, key);
    }
    
    @Test
    public void testGoodStack() {
        final IDoUndoStack stack = new DoUndoStack();
        
        // test do & undo in various combinations
        testGoodStack(stack, null);
    }
    
    @Test
    public void testBadStack() {
        final IDoUndoStack stack = new DoUndoStack();
        
        // test do & undo in various combinations
        testBadStack(stack, null);
    }
    
    @Test
    public void testGoodStackManager() {
        // don't confuse with thread dumps
        System.setErr(new PrintStream(new OutputStream() {
                public void write(int b) {
                    //DO NOTHING
                }
            }));

        // test improper keys in various combinations
        Assert.assertFalse(DoUndoManager.getInstance().canDo());
        Assert.assertFalse(DoUndoManager.getInstance().canDo(null));
        Assert.assertFalse(DoUndoManager.getInstance().canDo((String) null));
        Assert.assertFalse(DoUndoManager.getInstance().canDo((String[]) null));
        final String[] strArray1 = {"A","B"}; 
        Assert.assertFalse(DoUndoManager.getInstance().canDo(strArray1));
        final String[] strArray2 = {null,"B"}; 
        Assert.assertFalse(DoUndoManager.getInstance().canDo(strArray2));
        Assert.assertFalse(DoUndoManager.getInstance().canDo("1"));
        
        // add one stack and test it
        testGoodStack(DoUndoManager.getInstance(), "Stack1");
        
        // add second stack and test it
        testGoodStack(DoUndoManager.getInstance(), "Stack2");
        
        // test with both stacks filled
        testGoodFilledStack(DoUndoManager.getInstance(), "Stack1");
        testGoodFilledStack(DoUndoManager.getInstance(), "Stack2");
        
        Assert.assertTrue(DoUndoManager.getInstance().singleUndo("Stack1"));
        Assert.assertTrue(DoUndoManager.getInstance().canDo("Stack1"));
        Assert.assertTrue(DoUndoManager.getInstance().canUndo("Stack1"));
        Assert.assertEquals(1, DoUndoManager.getInstance().getDoStackSize("Stack1"));
        Assert.assertEquals(3, DoUndoManager.getInstance().getUndoStackSize("Stack1"));
        // second stack is unchanged
        Assert.assertEquals(0, DoUndoManager.getInstance().getDoStackSize("Stack2"));
        Assert.assertEquals(4, DoUndoManager.getInstance().getUndoStackSize("Stack2"));
        
        Assert.assertTrue(DoUndoManager.getInstance().singleUndo("Stack2"));
        Assert.assertTrue(DoUndoManager.getInstance().canDo("Stack2"));
        Assert.assertTrue(DoUndoManager.getInstance().canUndo("Stack2"));
        Assert.assertEquals(1, DoUndoManager.getInstance().getDoStackSize("Stack2"));
        Assert.assertEquals(3, DoUndoManager.getInstance().getUndoStackSize("Stack2"));
        // first stack is unchanged
        Assert.assertEquals(1, DoUndoManager.getInstance().getDoStackSize("Stack1"));
        Assert.assertEquals(3, DoUndoManager.getInstance().getUndoStackSize("Stack1"));

        // clear all
        Assert.assertTrue(DoUndoManager.getInstance().clearAll());
        Assert.assertEquals(0, DoUndoManager.getInstance().getDoStackSize("Stack1"));
        Assert.assertEquals(0, DoUndoManager.getInstance().getUndoStackSize("Stack1"));
        Assert.assertEquals(0, DoUndoManager.getInstance().getDoStackSize("Stack2"));
        Assert.assertEquals(0, DoUndoManager.getInstance().getUndoStackSize("Stack2"));
    }
    
    @Test
    public void testBadStackManager() {
        // don't confuse with thread dumps
        System.setErr(new PrintStream(new OutputStream() {
                public void write(int b) {
                    //DO NOTHING
                }
            }));

        // add one stack and test it
        testBadStack(DoUndoManager.getInstance(), "Stack1");
        
        // add second stack and test it
        testBadStack(DoUndoManager.getInstance(), "Stack2");

        // clear all
        Assert.assertTrue(DoUndoManager.getInstance().clearAll());
        Assert.assertEquals(0, DoUndoManager.getInstance().getDoStackSize("Stack1"));
        Assert.assertEquals(0, DoUndoManager.getInstance().getUndoStackSize("Stack1"));
        Assert.assertEquals(0, DoUndoManager.getInstance().getDoStackSize("Stack2"));
        Assert.assertEquals(0, DoUndoManager.getInstance().getUndoStackSize("Stack2"));
    }
}
