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
package tf.helper.general;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.apache.commons.codec.binary.Base64;

/**
 * Collection of various methods around image manipulation
 * @author thomas
 */
public class ImageHelper {
    /**
     * Converts a given Image into a BufferedImage
     * https://stackoverflow.com/a/13605411
     *
     * @param img The Image to be converted
     * @param type The type to be used to create new buffered image, see BufferedImage for values
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(final Image img, final int type) {
        if (img == null || img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        final BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), type);

        // Draw the image on to the buffered image
        final Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static BufferedImage toBufferedImage(final Image img) {
        return toBufferedImage(img, BufferedImage.TYPE_INT_RGB);
    }
    
    // TFE, 20240224: converting base64 strings to buffered images
    public static BufferedImage toBufferedImage(final String imageBase64) {
        BufferedImage result = null;
        
        byte[] imageByte = null; 
        try {
            imageByte = Base64.decodeBase64(imageBase64);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ImageHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (imageByte == null) {
            return result;
        }
        
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            result = ImageIO.read(bis);
        } catch (IOException | NullPointerException | IllegalArgumentException ex) {
            Logger.getLogger(ImageHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    public static String compressBase64Image(final String imageBase64, final String imageType, int scaleWidth, int scaleHeight) {
        String result = imageBase64;

        // TODO: only works for png & jpeg with single internal image
        // gif: animations get lost - would need something like
        // https://github.com/DhyanB/Open-Imaging to read gif frames
        // https://codereview.stackexchange.com/q/113998 writer.writeToSequence

        // https://www.tutorialspoint.com/java_dip/image_compression_technique.htm
        byte[] imageByte = null; 
        try {
            imageByte = Base64.decodeBase64(imageBase64);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ImageHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (imageByte == null) {
            return result;
        }

        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(bos)) {
            final BufferedImage bufferedImage = ImageIO.read(bis);
            if (scaleWidth == -1) {
                scaleWidth = bufferedImage.getWidth();
            }
            if (scaleHeight == -1) {
                scaleHeight = bufferedImage.getHeight();
            }

            // TODO: handle multiple images (e.g. animated gifs)
            
            // a) rescale image to actual size used in note - if size has changed
            BufferedImage scaledBufferedImage = bufferedImage;
            if ((scaleWidth > -1) || (scaleHeight > -1)) {
                scaledBufferedImage = ImageHelper.toBufferedImage(bufferedImage.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH));
            }

            // b) compress image by reducing quality - if size too big
            // https://stackoverflow.com/a/8351216
            float compression = 1f;

            if (imageByte.length > 1024*1024) {
                compression = 0.25f;
            }

            if ((scaleWidth > -1) || (scaleHeight > -1) || (compression < 1f)) {
                // we actual have done something
                final ImageWriter writer = ImageIO.getImageWritersByFormatName(imageType).next();
                writer.setOutput(ios);
                final ImageWriteParam param = writer.getDefaultWriteParam();
                // TODO: something fancy to compress png
                if (param.canWriteCompressed() && (compression < 1f)) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(compression);
                }

                writer.write(null, new IIOImage(scaledBufferedImage, null, null), param);
                writer.dispose();
                ios.flush();
                bos.flush();

                final byte[] imageBytes = bos.toByteArray();
                result = Base64.encodeBase64String(imageBytes);
            }
        } catch (IOException | NullPointerException | IllegalArgumentException ex) {
            Logger.getLogger(ImageHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
}
