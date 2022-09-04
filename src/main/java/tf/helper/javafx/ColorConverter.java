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

/**
 * Helper for various conversions between colors from
 * JavaFX, css, kml
 * With the help of https://stackoverflow.com/a/56733608
 * @author thomas
 */
public class ColorConverter {
    private final static ColorConverter INSTANCE = new ColorConverter();
    
    private ColorConverter() {
    }

    public static ColorConverter getInstance() {
        return INSTANCE;
    }
    
    // two char hex 0..255 from double value 0..1
    private static String doubleToHex(double val) {
        final String in = Integer.toHexString((int) Math.round(val * 255)).toUpperCase();
        
        return in.length() == 1 ? "0" + in : in;
    }

    // https://stackoverflow.com/a/56733608
    // TFE, 20211201
    // https://stackoverflow.com/a/13036015 - opacity is encoded in the KML color!
    public static String JavaFXtoKML(final Color color) {
        return JavaFXtoKML(color, color.getOpacity());
    }
    public static String JavaFXtoKML(final Color color, final Double opacity) {
        // kml uses alpha + BGR
        return (doubleToHex(opacity) + doubleToHex(color.getBlue()) + doubleToHex(color.getGreen()) + doubleToHex(color.getRed())).toUpperCase();
    }

    public static String JavaFXtoCSS(final Color color) {
        return "#" + JavaFXtoRGBHex(color);
    }

    public static String JavaFXtoRGBHex(final Color color) {
        if (color.getOpacity() == 1.0) {
            return (doubleToHex(color.getRed()) + doubleToHex(color.getGreen()) + doubleToHex(color.getBlue())).toUpperCase();
        } else {
            return (doubleToHex(color.getRed()) + doubleToHex(color.getGreen()) + doubleToHex(color.getBlue()) + doubleToHex(color.getOpacity())).toUpperCase();
        }
    }
    
    // TFE, 2021012: lets do the other way too
    public static Color KMLToJavaFX(final String color) {
        String inColor = color;
        if (inColor == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        if (inColor.length() < 8) {
            throw new IllegalArgumentException("Argument is to short: " + inColor);
        }
        
        // handle opacity separately
        final int hexOp = Integer.decode("#" + inColor.substring(0, 2));
        final double o = (hexOp & 0xFF) / 255.0;

        final int hexColor = Integer.decode("#" + inColor.substring(2));
        final double b = ((hexColor & 0xFF0000) >> 16) / 255.0;
        final double g = ((hexColor & 0xFF00) >> 8) / 255.0;
        final double r = (hexColor & 0xFF) / 255.0;

        return new Color(r, g, b, o);
    }
    
    public static Color CSSToJavaFX(final String color) {
        if (!color.startsWith("#")) {
            throw new IllegalArgumentException("Argument doesn't start with '#'");
        }
            
        return RGBHexToJavaFX(color);
    }

    public static Color RGBHexToJavaFX(final String color) {
        String inColor = color;
        if (inColor == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        if (inColor.startsWith("#")) {
            inColor = inColor.substring(1);
        }
        if (inColor.length() < 6) {
            throw new IllegalArgumentException("Argument is to short: " + inColor);
        }
        
        // handle opacity separately - its optional
        double o = 1.0;
        if (inColor.length() == 8) {
            final int hexOp = Integer.decode("#" + inColor.substring(6));
            o = (hexOp & 0xFF) / 255.0;
        }

        final int hexColor = Integer.decode("#" + inColor.substring(0, 6));
        final double r = ((hexColor & 0xFF0000) >> 16) / 255.0;
        final double g = ((hexColor & 0xFF00) >> 8) / 255.0;
        final double b = (hexColor & 0xFF) / 255.0;

        return new Color(r, g, b, o);
    }
}
