package de.eb.ocr.image;

import de.eb.ocr.Recm;
import de.eb.ocr.SearchResult;
import de.eb.servlets.OcrServlet;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

public class TTT {
  static private Recm recm;


  static int[] byteToInt(byte[] data) {
    int[] res = new int[data.length / 4];
    for (int i = 0, j = 0; i < data.length;) {
      int g = (data[i++] + data[i++] + data[i++]) / 3;
      i++;
      res[j++] = (byte) g;
    }
    return res;
  }
  
  static Image imgFromBase64Data( String t ){
    try {
      BufferedImage bi = ImageIO.read(new ByteArrayInputStream(decodeBase64(t)));
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      //ImageIO.write(bi, "TIF", new File("/temp/saved.tif"));
      ImageIO.write(bi, "TIF", output);
      int data[] = byteToInt(output.toByteArray());
      Image i = new Image( bi.getHeight(), bi.getWidth(), data);
      return i;
    } catch (IOException ex) {
      Logger.getLogger(OcrServlet.class.getName()).log(Level.SEVERE, null, ex);
      System.out.println(ex.getMessage());
    }
    return null;
  }

  public static void main(String... args) throws IOException {
    recm = new Recm(8, 6, "m", "0123456789");

    String data = "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";
    Image img = imgFromBase64Data(data);
    SearchResult s = recm.recm(img, '0');

  }
}
