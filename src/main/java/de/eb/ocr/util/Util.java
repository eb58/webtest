package de.eb.ocr.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.util.Properties;
import org.apache.log4j.Logger;

public class Util {

  private static final Logger LOG = Logger.getRootLogger();

  public static String geTempFileName() {
    try {
      File temp = File.createTempFile("OCR", ".tif");
      temp.deleteOnExit();
      return temp.getName();
    } catch (IOException e) {
      LOG.error(e);
    }
    return null;
  }

  public static String[] readDir(String dirname, final String extension) {
    File file = new File(dirname);
    if (!file.isDirectory()) {
      return new String[0];
    }
    return file.list(new FileListFilter(null, extension));
  }

  public static Properties readProps(final String fname) {
    try {
//      InputStream propInFile = Properties.class.getResourceAsStream(fname);
      FileInputStream fis = new FileInputStream(fname);
      Properties p = new Properties();
      p.loadFromXML(fis);
      return p;
    } catch (FileNotFoundException e) {
      LOG.error(e);
    } catch (IOException e) {
      LOG.error(e);
    }
    return null;
  }

  public static boolean storeProps(final String fname) {
    try {
      Properties p = new Properties(System.getProperties());
      if (new File("/media/Volume/Usr/OCR/DB").exists()) {
        p.setProperty("DBINSTPATH", "/media/Volume/Usr/OCR/DB");
      } else {
        p.setProperty("DBINSTPATH", "d:/Usr/OCR/DB");
      }
      p.storeToXML(new FileOutputStream(fname), "Eine Insel mit zwei Bergen");
    } catch (FileNotFoundException e) {
      LOG.error(e);
    } catch (IOException e) {
      LOG.error(e);
    }
    return true;
  }

  public static String readFile(String file) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    StringBuilder stringBuilder = new StringBuilder();
    String ls = System.getProperty("line.separator");

    try {
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
        stringBuilder.append(ls);
      }
      return stringBuilder.toString();
    } finally {
      reader.close();
    }
  }
  
}
