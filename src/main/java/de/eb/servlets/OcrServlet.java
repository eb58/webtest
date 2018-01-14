package de.eb.servlets;

import de.eb.ocr.Recm;
import de.eb.ocr.SearchResult;
import de.eb.ocr.image.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

@WebServlet(urlPatterns = "/OcrServlet", loadOnStartup = 1)
public class OcrServlet extends HttpServlet {

  private Recm recm;

  @Override
  public void init() throws ServletException {
    super.init(); //To change body of generated methods, choose Tools | Templates.
    recm = new Recm(8, 6, "m", "0123456789");
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      String jsondata = request.getParameter("imgdata");

      JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(jsondata);
      int nr = ((Long) jsonObj.get("nr")).intValue();
      int nc = ((Long) jsonObj.get("nc")).intValue();;
      JSONArray jsonarray = (JSONArray) jsonObj.get("data");
      int[] data = new int[nr * nc];

      int i = 0;
      for (Object l : jsonarray) {
        data[i++] = ((Long) l).intValue();
      }

      Image img = new Image(nr, nc, data);

      SearchResult sr = recm.recm(img, '0');
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(sr.getBestChar());

    } catch (ParseException ex) {
      Logger.getLogger(OcrServlet.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>

  public static void main(String... args) throws IOException {
    String t = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCACGAFoDAREAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFAEBAAAAAAAAAAAAAAAAAAAAAP/EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAMAwEAAhEDEQA/AJ/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/9k";
    byte[] imageBytes = decodeBase64(t);
    try {
      BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imageBytes));
    } catch (IOException ex) {
      Logger.getLogger(OcrServlet.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

}
