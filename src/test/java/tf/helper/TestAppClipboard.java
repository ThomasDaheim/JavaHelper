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

import tf.helper.general.AppClipboard;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.input.ClipboardContent;
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
        Assert.assertFalse(AppClipboard.getInstance().hasContentProperty().get());
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
        // 1) start with nothing :-)
        final Map<DataFormat, Object> content = new HashMap<>();
        
        AppClipboard.getInstance().setContent(content);
        // nothing has been put - only an empty map passed!
        Assert.assertFalse(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertFalse(AppClipboard.getInstance().hasFiles());
        Assert.assertFalse(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertFalse(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        // 2) get better
        content.put(DataFormat.FILES, testFiles);
        content.put(DataFormat.HTML, testString1);
        content.put(DataFormat.IMAGE, testImage);
        content.put(DataFormat.RTF, testString1);
        content.put(DataFormat.PLAIN_TEXT, testString1);
        content.put(DataFormat.URL, testString1);
        content.put(TEST_APPCLIPBOARD, testString1);

        AppClipboard.getInstance().setContent(content);
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
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
        
        // 3) change only one
        content.put(DataFormat.PLAIN_TEXT, testString2);
        
        AppClipboard.getInstance().setContent(content);
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
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
        
        // 4) and not throw away everything
        AppClipboard.getInstance().clear();
        
        Assert.assertFalse(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertFalse(AppClipboard.getInstance().hasFiles());
        Assert.assertFalse(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertFalse(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        // single add
        AppClipboard.getInstance().setContent(DataFormat.PLAIN_TEXT, testString1);
        
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertFalse(AppClipboard.getInstance().hasFiles());
        Assert.assertFalse(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertTrue(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        Assert.assertEquals(testString1, AppClipboard.getInstance().getString());

        final ClipboardContent testContent = AppClipboard.getInstance().getAsClipboardContent(DataFormat.PLAIN_TEXT);
        Assert.assertEquals(testString1, testContent.getString());
    }

    @Test
    public void testSetAddClipboardMap() {
        final Map<DataFormat, Object> content = new HashMap<>();

        // 1) set only one
        content.put(DataFormat.FILES, testFiles);
        AppClipboard.getInstance().setContent(content);
        // now we should have files ONLY
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertTrue(AppClipboard.getInstance().hasFiles());
        Assert.assertFalse(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertFalse(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));
        
        // 2) set another one only
        content.clear();
        content.put(DataFormat.HTML, testString1);
        AppClipboard.getInstance().setContent(content);
        // now we should have html ONLY
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertFalse(AppClipboard.getInstance().hasFiles());
        Assert.assertTrue(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertFalse(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        // 3) add only one
        content.clear();
        content.put(DataFormat.FILES, testFiles);
        AppClipboard.getInstance().addContent(content);
        // now we should have files AND html
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertTrue(AppClipboard.getInstance().hasFiles());
        Assert.assertTrue(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertFalse(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        // 3) add the rest
        content.put(DataFormat.HTML, testString1);
        content.put(DataFormat.IMAGE, testImage);
        content.put(DataFormat.RTF, testString1);
        content.put(DataFormat.PLAIN_TEXT, testString1);
        content.put(DataFormat.URL, testString1);
        content.put(TEST_APPCLIPBOARD, testString1);

        AppClipboard.getInstance().addContent(content);
        // and now we should have everything
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertTrue(AppClipboard.getInstance().hasFiles());
        Assert.assertTrue(AppClipboard.getInstance().hasHtml());
        Assert.assertTrue(AppClipboard.getInstance().hasImage());
        Assert.assertTrue(AppClipboard.getInstance().hasRtf());
        Assert.assertTrue(AppClipboard.getInstance().hasString());
        Assert.assertTrue(AppClipboard.getInstance().hasUrl());
        Assert.assertTrue(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));
        
        // 4) clear & add
        AppClipboard.getInstance().clear();
        AppClipboard.getInstance().addContent(content);
        // and now we should have everything
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertTrue(AppClipboard.getInstance().hasFiles());
        Assert.assertTrue(AppClipboard.getInstance().hasHtml());
        Assert.assertTrue(AppClipboard.getInstance().hasImage());
        Assert.assertTrue(AppClipboard.getInstance().hasRtf());
        Assert.assertTrue(AppClipboard.getInstance().hasString());
        Assert.assertTrue(AppClipboard.getInstance().hasUrl());
        Assert.assertTrue(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        // 5) remove only one
        AppClipboard.getInstance().clearContent(DataFormat.FILES);
        // and now we should have everything except files
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertFalse(AppClipboard.getInstance().hasFiles());
        Assert.assertTrue(AppClipboard.getInstance().hasHtml());
        Assert.assertTrue(AppClipboard.getInstance().hasImage());
        Assert.assertTrue(AppClipboard.getInstance().hasRtf());
        Assert.assertTrue(AppClipboard.getInstance().hasString());
        Assert.assertTrue(AppClipboard.getInstance().hasUrl());
        Assert.assertTrue(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));
    }

    @Test
    public void testSetAddClipboardSingle() {
        // 1) set only one
        AppClipboard.getInstance().setContent(DataFormat.FILES, testFiles);
        // now we should have files ONLY
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertTrue(AppClipboard.getInstance().hasFiles());
        Assert.assertFalse(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertFalse(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));
        
        // 2) set another one only
        AppClipboard.getInstance().setContent(DataFormat.HTML, testString1);
        // now we should have html ONLY
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertFalse(AppClipboard.getInstance().hasFiles());
        Assert.assertTrue(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertFalse(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        // 3) add only one
        AppClipboard.getInstance().addContent(DataFormat.FILES, testFiles);
        // now we should have files AND html
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertTrue(AppClipboard.getInstance().hasFiles());
        Assert.assertTrue(AppClipboard.getInstance().hasHtml());
        Assert.assertFalse(AppClipboard.getInstance().hasImage());
        Assert.assertFalse(AppClipboard.getInstance().hasRtf());
        Assert.assertFalse(AppClipboard.getInstance().hasString());
        Assert.assertFalse(AppClipboard.getInstance().hasUrl());
        Assert.assertFalse(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));

        // 3) add the rest
        AppClipboard.getInstance().addContent(DataFormat.HTML, testString1);
        AppClipboard.getInstance().addContent(DataFormat.IMAGE, testImage);
        AppClipboard.getInstance().addContent(DataFormat.RTF, testString1);
        AppClipboard.getInstance().addContent(DataFormat.PLAIN_TEXT, testString1);
        AppClipboard.getInstance().addContent(DataFormat.URL, testString1);
        AppClipboard.getInstance().addContent(TEST_APPCLIPBOARD, testString1);
        // and now we should have everything
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
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
        
        // 4) clear & add
        AppClipboard.getInstance().clear();
        AppClipboard.getInstance().addContent(DataFormat.FILES, testFiles);
        AppClipboard.getInstance().addContent(DataFormat.HTML, testString1);
        AppClipboard.getInstance().addContent(DataFormat.IMAGE, testImage);
        AppClipboard.getInstance().addContent(DataFormat.RTF, testString1);
        AppClipboard.getInstance().addContent(DataFormat.PLAIN_TEXT, testString1);
        AppClipboard.getInstance().addContent(DataFormat.URL, testString1);
        AppClipboard.getInstance().addContent(TEST_APPCLIPBOARD, testString1);
        // and now we should have everything
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
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

        // 5) remove only one
        AppClipboard.getInstance().clearContent(DataFormat.FILES);
        // and now we should have everything except files
        Assert.assertTrue(AppClipboard.getInstance().hasContentProperty().get());
        Assert.assertFalse(AppClipboard.getInstance().hasFiles());
        Assert.assertTrue(AppClipboard.getInstance().hasHtml());
        Assert.assertTrue(AppClipboard.getInstance().hasImage());
        Assert.assertTrue(AppClipboard.getInstance().hasRtf());
        Assert.assertTrue(AppClipboard.getInstance().hasString());
        Assert.assertTrue(AppClipboard.getInstance().hasUrl());
        Assert.assertTrue(AppClipboard.getInstance().hasContent(TEST_APPCLIPBOARD));
    }
}
