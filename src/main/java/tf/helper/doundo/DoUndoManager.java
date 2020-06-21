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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manager for multiple DoUndoStacks in parallel.
 * Holds a map of key / DoUndoStack pairs for which all DoUndoStack methods are available.
 * @author thomas
 */
public class DoUndoManager implements IDoUndoStack {
    private final static DoUndoManager INSTANCE = new DoUndoManager();

    private final Map<String, DoUndoStack> stackMap = new HashMap<>();
    private final IntegerProperty changeCountProperty = new SimpleIntegerProperty(0);
    
    private DoUndoManager() {
    }

    public static DoUndoManager getInstance() {
        return INSTANCE;
    }
    
    private boolean verifyKey(final boolean mustExist, final boolean nonExistMessage, String... key) {
        // see http://robertmarkbramprogrammer.blogspot.com/2013/03/nulls-and-varargs.html for what to expect
        if (key == null) {
            System.out.println("DoUndoManager called with null key");
            Thread.dumpStack();
            return false;
        }
        if (key.length != 1 || key[0] == null) {
            System.out.println("DoUndoManager called with incorrect key array: " + key + ", " + key.length);
            Thread.dumpStack();
            return false;
        }
        if (mustExist && !stackMap.containsKey(key[0])) {
            if (nonExistMessage) {
                // no message on case of canDo()... calls to avoid that the callers have to check first if we have any do/undo stack at all for a key
                System.out.println("DoUndoManager called for non-existing key: " + key[0]);
                Thread.dumpStack();
            }
            return false;
        }

        return true;
    }

    @Override
    public boolean addDoneAction(IDoUndoAction action, String... key) {
        if (!verifyKey(false, false, key)) {
            return false;
        }
        
        if (!stackMap.containsKey(key[0])) {
            stackMap.put(key[0], new DoUndoStack(action));
        } else {
            final DoUndoStack stack = stackMap.get(key[0]);
            stack.addDoneAction(action, key);
            stackMap.replace(key[0], stack);
        }
        
        changeCountProperty.set(changeCountProperty.get()+1);
        return true;
    }

    @Override
    public boolean addDoneActions(List<IDoUndoAction> actions, String... key) {
        if (!verifyKey(false, false, key)) {
            return false;
        }
        if (!stackMap.containsKey(key[0])) {
            stackMap.put(key[0], new DoUndoStack(actions));
        } else {
            final DoUndoStack stack = stackMap.get(key[0]);
            stack.addDoneActions(actions, key);
            stackMap.replace(key[0], stack);
        }
        
        changeCountProperty.set(changeCountProperty.get()+1);
        return true;
    }

    @Override
    public boolean clear(String... key) {
        // TFE, 20200607: clear without any stack is also OK
        if (!verifyKey(true, false, key)) {
            return false;
        }
        
        final boolean result = stackMap.get(key[0]).clear(key);
        changeCountProperty.set(changeCountProperty.get()+1);
        return result;
    }

    @Override
    public boolean clearAll() {
        stackMap.values().forEach((stack) -> {
            stack.clearAll();
        });
        stackMap.clear();
        
        changeCountProperty.set(changeCountProperty.get()+1);
        return true;
    }

    @Override
    public boolean canDo(String... key) {
        if (!verifyKey(true, false, key)) {
            return false;
        }
        
        return stackMap.get(key[0]).canDo(key);
    }

    @Override
    public boolean canUndo(String... key) {
        if (!verifyKey(true, false, key)) {
            return false;
        }
        
        return stackMap.get(key[0]).canUndo(key);
    }

    @Override
    public int getDoStackSize(String... key) {
        if (!verifyKey(true, false, key)) {
            return 0;
        }
        
        return stackMap.get(key[0]).getDoStackSize(key);
    }

    @Override
    public int getUndoStackSize(String... key) {
        if (!verifyKey(true, false, key)) {
            return 0;
        }
        
        return stackMap.get(key[0]).getUndoStackSize(key);
    }

    @Override
    public boolean singleUndo(String... key) {
        if (!verifyKey(true, true, key)) {
            return false;
        }
        
        final boolean result = stackMap.get(key[0]).singleUndo(key);
        changeCountProperty.set(changeCountProperty.get()+1);
        return result;
    }

    @Override
    public boolean singleDo(String... key) {
        if (!verifyKey(true, true, key)) {
            return false;
        }
        
        final boolean result = stackMap.get(key[0]).singleDo(key);
        changeCountProperty.set(changeCountProperty.get()+1);
        return result;
    }

    @Override
    public boolean rollBack(String... key) {
        if (!verifyKey(true, true, key)) {
            return false;
        }
        
        final boolean result = stackMap.get(key[0]).rollBack(key);
        changeCountProperty.set(changeCountProperty.get()+1);
        return result;
    }

    @Override
    public boolean rollForward(String... key) {
        if (!verifyKey(true, true, key)) {
            return false;
        }
        
        final boolean result = stackMap.get(key[0]).rollForward(key);
        changeCountProperty.set(changeCountProperty.get()+1);
        return result;
    }
    
    @Override
    public IntegerProperty changeCountProperty() {
        return changeCountProperty;
    }

    @Override
    public String getActionDescription(String... key) {
        if (!verifyKey(true, false, key)) {
            return "";
        }
        
        return stackMap.get(key[0]).getActionDescription(key);
    }

    public String getDoActionDescription(String... key) {
        if (!verifyKey(true, false, key)) {
            return "";
        }
        
        return stackMap.get(key[0]).getDoActionDescription(key);
    }

    public String getUndoActionDescription(String... key) {
        if (!verifyKey(true, false, key)) {
            return "";
        }
        
        return stackMap.get(key[0]).getUndoActionDescription(key);
    }
}
