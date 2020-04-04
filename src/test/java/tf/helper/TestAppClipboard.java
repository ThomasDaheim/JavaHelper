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
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS" + LatLongHelper.MIN + "" + LatLongHelper.MIN + " AND ANY EXPRESS OR
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

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.input.DataFormat;
import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author thomas
 */
public class TestAppClipboard {
    public static final DataFormat TEST_APPCLIPBOARD = new DataFormat("application/javahelper");
    
    public static final String testString1 = "TEST-STRING #1";
    public static final String testString2 = "TEST-STRING #2";
    public static final List<File> testFiles = Arrays.asList(Files.currentFolder());
    // can use Image here since not running in JavaFX thread...
    public static final String testImage = testString1;

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
    public void testEmptyAppClipboard() {
        Assert.assertNotNull(AppClipboard.getInstance());
        Assert.assertFalse(AppClipboard.getInstance().contentPut());
        Assert.assertFalse(AppClipboard.getInstance().hasFiles());
        Assert.assertFalse(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertFalse(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));
    }

    @Test
    public void testAppClipboard() {
        // start with nothing :-)
        final Map<DataFormat, Object> content = new HashMap<>();
        
        AppClipboard.getInstance().setContent(content);
        Assert.assertTrue(AppClipboard.getInstance().contentPut());
        Assert.assertFalse(AppClipboard.getInstance().hasFiles());
        Assert.assertFalse(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertFalse(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        // get better
        content.put(DataFormat.FILES, testFiles);
        content.put(DataFormat.HTML, testString1);
        content.put(DataFormat.IMAGE, testImage);
        content.put(DataFormat.RTF, testString1);
        content.put(DataFormat.PLAIN_TEXT, testString1);
        content.put(DataFormat.URL, testString1);
        content.put(TEST_APPCLIPBOARD, testString1);

        AppClipboard.getInstance().setContent(content);
        Assert.assertTrue(AppClipboard.getInstance().contentPut());
        Assert.assertTrue(AppClipboard.getInstance().hasFiles());
        Assert.assertTrue(AppClipboard.getInstance().hasHtml());
        Assert.assertTrue(AppClipboard.getInstance().hasImage());
        Assert.assertTrue(AppClipboard.getInstance().hasRtf());
        Assert.assertTrue(AppClipboard.getInstance().hasString());
        Assert.assertTrue(AppClipboard.getInstance().hasUrl());
        Assert.assertTrue(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        Assert.assertEquals(AppClipboard.getInstance().getFiles().get(0), testFiles.get(0));
        Assert.assertEquals(testString1, AppClipboard.getInstance().getHtml());
        // Image can't be tested here since not in JavaFX thread
//        Assert.assertEquals(testImage, AppClipboard.getInstance().getImage());
        Assert.assertEquals(testString1, AppClipboard.getInstance().getRtf());
        Assert.assertEquals(testString1, AppClipboard.getInstance().getString());
        Assert.assertEquals(testString1, AppClipboard.getInstance().getUrl());
        Assert.assertEquals(testString1, AppClipboard.getInstance().getContent(TEST_APPCLIPBOARD));
        
        // change only one
        content.put(DataFormat.PLAIN_TEXT, testString2);
        
        AppClipboard.getInstance().setContent(content);
        Assert.assertTrue(AppClipboard.getInstance().contentPut());
        Assert.assertTrue(AppClipboard.getInstance().hasFiles());
        Assert.assertTrue(AppClipboard.getInstance().hasHtml());
        Assert.assertTrue(AppClipboard.getInstance().hasImage());
        Assert.assertTrue(AppClipboard.getInstance().hasRtf());
        Assert.assertTrue(AppClipboard.getInstance().hasString());
        Assert.assertTrue(AppClipboard.getInstance().hasUrl());
        Assert.assertTrue(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        Assert.assertEquals(AppClipboard.getInstance().getFiles().get(0), testFiles.get(0));
        Assert.assertEquals(testString1, AppClipboard.getInstance().getHtml());
        // Image can't be tested here since not in JavaFX thread
//        Assert.assertEquals(testImage, AppClipboard.getInstance().getImage());
        Assert.assertEquals(testString1, AppClipboard.getInstance().getRtf());
        Assert.assertEquals(testString2, AppClipboard.getInstance().getString());
        Assert.assertEquals(testString1, AppClipboard.getInstance().getUrl());
        Assert.assertEquals(testString1, AppClipboard.getInstance().getContent(TEST_APPCLIPBOARD));
        
        // and not throw away everything
        AppClipboard.getInstance().clear();
        
        Assert.assertFalse(AppClipboard.getInstance().contentPut());
        Assert.assertFalse(AppClipboard.getInstance().hasFiles());
        Assert.assertFalse(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertFalse(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        // single add
        AppClipboard.getInstance().setContent(DataFormat.PLAIN_TEXT, testString1);
        
        Assert.assertTrue(AppClipboard.getInstance().contentPut());
        Assert.assertFalse(AppClipboard.getInstance().hasFiles());
        Assert.assertFalse(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertTrue(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        Assert.assertEquals(testString1, AppClipboard.getInstance().getString());
        
    }
}
