/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eb.ocr;

import de.eb.ocr.util.Util;
import java.util.Properties;
import org.apache.log4j.Level;

/**
 *
 * @author Erich
 */
public class OcrConfig {

    private static final OcrConfig ocrConfig = new OcrConfig();
    private static final Properties props = Util.readProps("C:/Users/a403163/Documents/NetBeansProjects/JavaTestProjekte/webocrtest/ocr.xml");

    private OcrConfig() {
    }

    public static OcrConfig getInstance() {  
        return ocrConfig;
    }

    public String getDBInstPath() {  // Hier liegen die DBs
        return (String) props.getProperty("DBINSTPATH");
    }

    public String getLogFile() {
        return (String) props.getProperty("LOG_FILE");
    }

    public Level getLogLevel() {
        String s = (String) props.getProperty("LOG_LEVEL");
        if (s.equals("INFO")) return Level.INFO;
        if (s.equals("WARN")) return Level.WARN;
        if (s.equals("DEBUG")) return Level.DEBUG;
        if (s.equals("ERROR")) return Level.ERROR;
        if (s.equals("FATAL")) return Level.FATAL;
        return Level.OFF;
    }

    public String getLogPatternLayout() {
        return (String) props.getProperty("LOG_PATTERNLAYOUT");
    }
}
