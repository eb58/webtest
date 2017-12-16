/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eb.ocr.image;

import java.io.IOException;

/**
 *
 * @author de1usrs0
 */
public class ImageLoader {

    private static String fname = null;
    private static Image image = null;

    static public Image loadImage(String fname, int x, int y, int w, int h) {
        if (!fname.equals(ImageLoader.fname)) {
            ImageLoader.fname = fname;
            try {
                image = new Image(fname);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return image.getpartimage(y, x, w, h);
    }

    static public Image loadImage(String fname) {
        if (!fname.equals(ImageLoader.fname)) {
            ImageLoader.fname = fname;
            try {
                return image = new Image(fname);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
