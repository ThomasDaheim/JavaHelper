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

import com.sun.javafx.collections.ObservableListWrapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import tf.helper.general.ObjectsHelper;
import tf.helper.xstreamfx.FXConverters;

/**
 *
 * @author thomas
 */
public class TestXStreamFX {
    private final static String XML_FILE = "_serialization.xml";
            
    private static final AtomicInteger objectCount = new AtomicInteger(1);
    
    private static Path testpath;
    
    private static TestObject testObject1;
    private static TestObject testObject2;
    private static TestObject testObject3;
    private static TestObject testObject4;
    private static TestObject testObject5;
    private static TestObject testObject6;
    private static TestObject testObject7;
    private static TestObject testObject8;

    @BeforeClass
    public static void setUpClass() {
        // create temp directory for serialization
        try {
            testpath = Files.createTempDirectory("TestXStreamFX");
        } catch (IOException ex) {
            Logger.getLogger(TestXStreamFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        testpath.toFile().setReadable(true, false);
        testpath.toFile().setWritable(true, false);
        testpath.toFile().setExecutable(true, false);

        testObject1 = new TestObject(objectCount.getAndIncrement());
        testObject2 = new TestObject(objectCount.getAndIncrement());
        testObject3 = new TestObject(objectCount.getAndIncrement());
        testObject4 = new TestObject(objectCount.getAndIncrement());
        testObject5 = new TestObject(objectCount.getAndIncrement());
        testObject6 = new TestObject(objectCount.getAndIncrement());
        testObject7 = new TestObject(objectCount.getAndIncrement());
        testObject8 = new TestObject(objectCount.getAndIncrement());

        testObject8.getChildren().addAll(testObject6, testObject7);
        testObject4.getChildren().addAll(testObject5, testObject8);
    }
    
    @AfterClass
    public static void tearDownClass() {
        try {
            // delete temp directory for serialization
            FileUtils.deleteDirectory(testpath.toFile());
        } catch (IOException ex) {
            Logger.getLogger(TestXStreamFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void serializeTestObject(final Object testObject, final String fileName) {
        final XStream xstream = new XStream(new DomDriver("ISO-8859-1"));
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
        XStream.setupDefaultSecurity(xstream);
        final Class<?>[] classes = new Class[] { 
            TestObject.class, 
            ObservableListWrapper.class, 
            SimpleBooleanProperty.class, 
            SimpleIntegerProperty.class, 
            SimpleLongProperty.class, 
            SimpleDoubleProperty.class, 
            SimpleStringProperty.class,
            Boolean.class,
            Integer.class,
            Long.class,
            Double.class,
            String.class
        };
        xstream.allowTypes(classes);

        FXConverters.configure(xstream);
        
        xstream.alias("testObject", TestObject.class);
        xstream.alias("listProp", ObservableListWrapper.class);
        xstream.alias("boolProp", SimpleBooleanProperty.class);
        xstream.alias("intProp", SimpleIntegerProperty.class);
        xstream.alias("longProp", SimpleLongProperty.class);
        xstream.alias("doubleProp", SimpleDoubleProperty.class);
        xstream.alias("stringProp", SimpleStringProperty.class);

        try (
            BufferedOutputStream stdout = new BufferedOutputStream(new FileOutputStream(testpath + File.separator + fileName + XML_FILE));
            Writer writer = new OutputStreamWriter(stdout, "ISO-8859-1");
        ) {
            PrettyPrintWriter printer = new PrettyPrintWriter(writer, new char[]{'\t'});
            writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + System.getProperty("line.separator"));
        
            xstream.marshal(testObject, printer);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestXStreamFX.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestXStreamFX.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestXStreamFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Object deserializeTestObject(final String fileName) {
        Object result = null;
        
        final XStream xstream = new XStream(new DomDriver("ISO-8859-1"));
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
        XStream.setupDefaultSecurity(xstream);
        final Class<?>[] classes = new Class[] { 
            TestObject.class, 
            ObservableListWrapper.class, 
            SimpleBooleanProperty.class, 
            SimpleIntegerProperty.class, 
            SimpleLongProperty.class, 
            SimpleDoubleProperty.class, 
            SimpleStringProperty.class,
            Boolean.class,
            Integer.class,
            Long.class,
            Double.class,
            String.class
        };
        xstream.allowTypes(classes);

        FXConverters.configure(xstream);
        
        xstream.alias("testObject", TestObject.class);
        xstream.alias("listProp", ObservableListWrapper.class);
        xstream.alias("boolProp", SimpleBooleanProperty.class);
        xstream.alias("intProp", SimpleIntegerProperty.class);
        xstream.alias("longProp", SimpleLongProperty.class);
        xstream.alias("doubleProp", SimpleDoubleProperty.class);
        xstream.alias("stringProp", SimpleStringProperty.class);

        try (
            BufferedInputStream stdin = new BufferedInputStream(new FileInputStream(testpath + File.separator + fileName + XML_FILE));
            Reader reader = new InputStreamReader(stdin, "ISO-8859-1");
        ) {
            result = ObjectsHelper.uncheckedCast(xstream.fromXML(reader));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestXStreamFX.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestXStreamFX.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestXStreamFX.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
    
    @Test
    public void singleClass() {
        serializeTestObject(testObject1, "single");
        
        Object deserialized = deserializeTestObject("single");
        Assert.assertTrue(deserialized instanceof TestObject);
        Assert.assertTrue(TestObject.equalTestObjects(testObject1, ObjectsHelper.uncheckedCast(deserialized)));
    }
    
    @Test
    public void listofClass() {
        final List<TestObject> objectList = new ArrayList<>();
        
        objectList.add(testObject1);
        objectList.add(testObject2);
        serializeTestObject(objectList, "list");

        Object deserialized = deserializeTestObject("list");
        Assert.assertTrue(deserialized instanceof ArrayList);
        Assert.assertEquals(objectList.size(), ((ArrayList) deserialized).size());
        for (int i = 0; i < objectList.size(); i++) {
            Object deserializedObject = ((ArrayList) deserialized).get(i);
            Assert.assertTrue(deserializedObject instanceof TestObject);
            Assert.assertTrue(TestObject.equalTestObjects(objectList.get(i), ObjectsHelper.uncheckedCast(deserializedObject)));
        }
    }
    
    @Test
    public void observablelistofClass() {
        final ObservableList<TestObject> objectList = FXCollections.observableArrayList();
        
        objectList.add(testObject1);
        objectList.add(testObject2);
        serializeTestObject(objectList, "observablelist");

        Object deserialized = deserializeTestObject("observablelist");
        Assert.assertTrue(deserialized instanceof ObservableList);
        Assert.assertEquals(objectList.size(), ((ObservableList) deserialized).size());
        for (int i = 0; i < objectList.size(); i++) {
            Object deserializedObject = ((ObservableList) deserialized).get(i);
            Assert.assertTrue(deserializedObject instanceof TestObject);
            Assert.assertTrue(TestObject.equalTestObjects(objectList.get(i), ObjectsHelper.uncheckedCast(deserializedObject)));
        }
    }

    @Test
    public void hierarchyofClass() {
        serializeTestObject(testObject4, "hierarchy");
        
        Object deserialized = deserializeTestObject("hierarchy");
        Assert.assertTrue(deserialized instanceof TestObject);
        Assert.assertTrue(TestObject.equalTestObjects(testObject4, ObjectsHelper.uncheckedCast(deserialized)));
    }
}
