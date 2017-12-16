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

@WebServlet("/OcrServlet")
public class OcrServlet extends HttpServlet {

  private Recm recm;

  @Override
  public void init() throws ServletException {
    super.init(); //To change body of generated methods, choose Tools | Templates.
    recm = new Recm(8, 6, "m", "0123456789");
  }

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
    try {
      String jsondata = new ArrayList<>(request.getParameterMap().keySet()).get(0);

      JSONObject jsonObj = null;
      jsonObj = (JSONObject) JSONValue.parseWithException(jsondata);
      Long nr = (Long) jsonObj.get("nr");
      Long nc = (Long) jsonObj.get("nc");
      JSONArray jsonarray = (JSONArray) jsonObj.get("data");
      Iterator<Boolean> iterator = jsonarray.iterator();
      int[] data = new int[nr.intValue() * nc.intValue()];
      int i = 0;
      while (iterator.hasNext()) {
        boolean b = iterator.next();
        data[i++] = b ? 1 : 0;
      }

      Image img = new Image(nr.intValue(), nc.intValue(), data);

      SearchResult sr = recm.recm(img, '0');
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(sr.getBestChar());

    } catch (ParseException ex) {
      Logger.getLogger(OcrServlet.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
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
