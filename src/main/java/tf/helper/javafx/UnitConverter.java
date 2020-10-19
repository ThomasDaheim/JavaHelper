/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.helper.javafx;

import javafx.application.Platform;
import javafx.stage.Screen;

/**
 * Convert for transformation betwen various units.
 * To be extended as required. Or to use existing implementation of other converters, e.g. javax.measure.
 * 
 * @author thomas
 */
public class UnitConverter {
    private final static UnitConverter INSTANCE = new UnitConverter();
    
    private final static double INCH_PER_MILLIMETER = 25.4;
    
    private final double screenDPI;
    private final double screenScaleX;
    private final double screenScaleY;
    
    private UnitConverter() {
        super();
        
        if (Platform.isFxApplicationThread()) {
            // get screen info for dpi & scaling
            // https://stackoverflow.com/a/49364643
            final Screen screen = Screen.getPrimary();
            screenDPI = screen.getDpi();
            screenScaleX = screen.getOutputScaleX();
            screenScaleY = screen.getOutputScaleX();
        } else {
            // not running in UI... pick any value
            screenDPI = 1.0;
            screenScaleX = 1.0;
            screenScaleY = 1.0;
        }
    }
    
    public static UnitConverter getInstance() {
        return INSTANCE;
    }
    
    public double millimeterToPixel(final double mm) {
        // https://stackoverflow.com/a/18184754
        return mm * screenDPI / INCH_PER_MILLIMETER;
    }
    
    public double pixelToMillimeter(final double pixel) {
        return pixel / screenDPI * INCH_PER_MILLIMETER;
    }
}
