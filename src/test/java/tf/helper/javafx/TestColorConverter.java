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
package tf.helper.javafx;

import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author thomas
 */
public class TestColorConverter {
    private static final Color testColor1 = Color.DARKMAGENTA;
    private static final String testColor1_RGBHEX = "8B008B";
    private static final String testColor1_CSS = "#" + testColor1_RGBHEX;
    private static final String testColor1_KML = "FF8B008B";
    
    private static final Color testColor1a = new Color(testColor1.getRed(), testColor1.getGreen(), testColor1.getBlue(), 0.5);
    private static final String testColor1a_RGBHEX = "8B008B80";
    private static final String testColor1a_CSS = "#" + testColor1a_RGBHEX;
    private static final String testColor1a_KML = "808B008B";
    
    private static final Color testColor2 = Color.CHARTREUSE;
    private static final String testColor2_RGBHEX = "7FFF00";
    private static final String testColor2_CSS = "#" + testColor2_RGBHEX;
    private static final String testColor2_KML = "FF00FF7F";
    
    private static final Color testColor2a = new Color(testColor2.getRed(), testColor2.getGreen(), testColor2.getBlue(), 0.5);
    private static final String testColor2a_RGBHEX = "7FFF0080";
    private static final String testColor2a_CSS = "#" + testColor2a_RGBHEX;
    private static final String testColor2a_KML = "8000FF7F";
    
    @Test
    public void testFromToCSS() {
        // test against KML value
        Assert.assertEquals(testColor1_CSS, ColorConverter.JavaFXtoCSS(testColor1));
        Assert.assertEquals(testColor1a_CSS, ColorConverter.JavaFXtoCSS(testColor1a));
        Assert.assertEquals(testColor2_CSS, ColorConverter.JavaFXtoCSS(testColor2));
        Assert.assertEquals(testColor2a_CSS, ColorConverter.JavaFXtoCSS(testColor2a));

        // all colors should be converted to themsevles in the end
        isEqualColor(testColor1, ColorConverter.CSSToJavaFX(ColorConverter.JavaFXtoCSS(testColor1)));
        isEqualColor(testColor1a, ColorConverter.CSSToJavaFX(ColorConverter.JavaFXtoCSS(testColor1a)));
        isEqualColor(testColor2, ColorConverter.CSSToJavaFX(ColorConverter.JavaFXtoCSS(testColor2)));
        isEqualColor(testColor2a, ColorConverter.CSSToJavaFX(ColorConverter.JavaFXtoCSS(testColor2a)));
    }
    
    @Test
    public void testFromToRGBHex() {
        // test against KML value
        Assert.assertEquals(testColor1_RGBHEX, ColorConverter.JavaFXtoRGBHex(testColor1));
        Assert.assertEquals(testColor1a_RGBHEX, ColorConverter.JavaFXtoRGBHex(testColor1a));
        Assert.assertEquals(testColor2_RGBHEX, ColorConverter.JavaFXtoRGBHex(testColor2));
        Assert.assertEquals(testColor2a_RGBHEX, ColorConverter.JavaFXtoRGBHex(testColor2a));

        // all colors should be converted to themsevles in the end
        isEqualColor(testColor1, ColorConverter.RGBHexToJavaFX(ColorConverter.JavaFXtoRGBHex(testColor1)));
        isEqualColor(testColor1a, ColorConverter.RGBHexToJavaFX(ColorConverter.JavaFXtoRGBHex(testColor1a)));
        isEqualColor(testColor2, ColorConverter.RGBHexToJavaFX(ColorConverter.JavaFXtoRGBHex(testColor2)));
        isEqualColor(testColor2a, ColorConverter.RGBHexToJavaFX(ColorConverter.JavaFXtoRGBHex(testColor2a)));
    }
    
    @Test
    public void testFromToKML() {
        // test against KML value
        Assert.assertEquals(testColor1_KML, ColorConverter.JavaFXtoKML(testColor1));
        Assert.assertEquals(testColor1a_KML, ColorConverter.JavaFXtoKML(testColor1a));
        Assert.assertEquals(testColor2_KML, ColorConverter.JavaFXtoKML(testColor2));
        Assert.assertEquals(testColor2a_KML, ColorConverter.JavaFXtoKML(testColor2a));
        
        // all colors should be converted to themsevles in the end
        isEqualColor(testColor1, ColorConverter.KMLToJavaFX(ColorConverter.JavaFXtoKML(testColor1)));
        isEqualColor(testColor1a, ColorConverter.KMLToJavaFX(ColorConverter.JavaFXtoKML(testColor1a)));
        isEqualColor(testColor2, ColorConverter.KMLToJavaFX(ColorConverter.JavaFXtoKML(testColor2)));
        isEqualColor(testColor2a, ColorConverter.KMLToJavaFX(ColorConverter.JavaFXtoKML(testColor2a)));
        
        // opaque colors should be converted to fully opaque ones
        isEqualColor(testColor1, ColorConverter.KMLToJavaFX(ColorConverter.JavaFXtoKML(testColor1a, 1.0)));
        isEqualColor(testColor2, ColorConverter.KMLToJavaFX(ColorConverter.JavaFXtoKML(testColor2a, 1.0)));
    }
    
    private static void isEqualColor(final Color col1, final Color col2) {
        Assert.assertEquals(col1.getRed(), col2.getRed(), 0.01);
        Assert.assertEquals(col1.getGreen(), col2.getGreen(), 0.01);
        Assert.assertEquals(col1.getBlue(), col2.getBlue(), 0.01);
        Assert.assertEquals(col1.getOpacity(), col2.getOpacity(), 0.01);
    }
}
