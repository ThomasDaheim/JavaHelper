/*
 *  Copyright (c) 2014ff Thomas Feuster
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tf.helper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author thomas
 */
public class TestObject {
    private final BooleanProperty boolProp;
    private final IntegerProperty intProp;
    private final LongProperty longProp;
    private final DoubleProperty doubleProp;
    private final StringProperty stringProp;

    private final Boolean boolVar;
    private final Integer intVar;
    private final Long longVar;
    private final Double doubleVar;
    private final String stringVar;

    private final ObservableList<TestObject> children = FXCollections.observableArrayList();
    
    private TestObject() {
        boolProp = null;
        intProp = null;
        longProp = null;
        doubleProp = null;
        stringProp = null;

        boolVar = null;
        intVar = null;
        longVar = null;
        doubleVar = null;
        stringVar = null;
    }
    
    public TestObject(final int counter) {
        boolProp = new SimpleBooleanProperty(true);
        intProp = new SimpleIntegerProperty(counter);
        longProp = new SimpleLongProperty(counter);
        doubleProp = new SimpleDoubleProperty(counter);
        stringProp = new SimpleStringProperty(String.valueOf(counter));

        boolVar = false;
        intVar = -counter;
        longVar = Long.valueOf(-counter);
        doubleVar = Double.valueOf(-counter);
        stringVar = String.valueOf(-counter);
    }
    
    public ObservableList<TestObject> getChildren() {
        return children;
    }
    
    public static boolean equalTestObjects(final TestObject test1, final TestObject test2) {
        if (test1.boolProp.get() != test2.boolProp.get()) {
            return false;
        }
        if (test1.intProp.get() != test2.intProp.get()) {
            return false;
        }
        if (test1.longProp.get() != test2.longProp.get()) {
            return false;
        }
        if (test1.doubleProp.get() != test2.doubleProp.get()) {
            return false;
        }
        if (!test1.stringProp.get().equals(test2.stringProp.get())) {
            return false;
        }
        
        if (!test1.boolVar.equals(test2.boolVar)) {
            return false;
        }
        if (!test1.intVar.equals(test2.intVar)) {
            return false;
        }
        if (!test1.longVar.equals(test2.longVar)) {
            return false;
        }
        if (!test1.doubleVar.equals(test2.doubleVar)) {
            return false;
        }
        if (!test1.stringVar.equals(test2.stringVar)) {
            return false;
        }
        
        // and now for the children...
        if (test1.children.size() != test2.children.size()) {
            return false;
        }
        for (int i = 0; i < test1.children.size(); i++) {
            if (!equalTestObjects(test1.children.get(i), test2.children.get(i))) {
                return false;
            }
        }
        
        return true;
    }
}
