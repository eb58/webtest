package de.eb.ocr;

import de.eb.ocr.image.Image;
import de.eb.ocr.image.Matrix;
import de.eb.ocr.util.Debug;
import de.eb.ocr.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;

class CharRep {

    boolean issimilar;
    Matrix m;    // Matrixrepräsentationen des Zeichens

    public CharRep() {
        issimilar = true;
    }

    CharRep(Matrix m) {
        this.m = m;
        issimilar = true;
    }
}

class CharDB {

    public char C;    // Zeichenwert
    public CharRep charReps[];

    public CharDB(char c) {
        C = c;
    }
}

public class CharDatabase {

    public static final int DIFFMAXVAL = Integer.MAX_VALUE / 1024;
    CharDB cdbvec[];
    String db_instpath;
    int m_DIMC;
    int m_DIMR;
    // //////////////////////////////////////////////////////////////
    public CharDatabase(int dimr, int dimc) {
        m_DIMR = dimr;
        m_DIMC = dimc;
        db_instpath = OcrConfig.getInstance().getDBInstPath();   // wo die m8x6, mprint... etc. sind!
        if (!new File(db_instpath).exists()) db_instpath = "/media/Volume/Usr/OCR/DB";
    }

    //////////////////////////////////////////////////////////////////////////////////// /
    // DB-Functions
    //////////////////////////////////////////////////////////////////////////////////// /
    void resetSimilar() {
        for (CharDB cdb : cdbvec)
            for (CharRep crp : cdb.charReps)
                crp.issimilar = true;
    }

    SearchResult search_dbS(final Matrix m) {
        SearchResult sr = new SearchResult();

        for (CharDB cdb : cdbvec)
            for (int i = 0; i < 15; i++) {
                CharRep crp = cdb.charReps[i];
                int dist = Matrix.diffSQR(crp.m, m, 3 * sr.getMinDist());
                sr.update(cdb.C, dist);
                if (dist >= 29 * sr.getMinDist() / 10 && dist > 1000)
                    crp.issimilar = false;
            }
        for (CharDB cdb : cdbvec)
            for (int i = 15; i < 30; i++) {
                CharRep crp = cdb.charReps[i];
                int dist = Matrix.diffSQR(crp.m, m, 3 * sr.getMinDist());
                sr.update(cdb.C, dist);
                if (dist >= 29 * sr.getMinDist() / 10 && dist > 1000)
                    crp.issimilar = false;
            }
        for (CharDB cdb : cdbvec)
            for (int i = 30; i < cdb.charReps.length; i++) {
                CharRep crp = cdb.charReps[i];
                if (!crp.issimilar)
                    continue;
                int dist = Matrix.diffSQR(crp.m, m, 3 * sr.getMinDist());
                sr.update(cdb.C, dist);
                if (dist >= 29 * sr.getMinDist() / 10 && dist > 1000)
                    crp.issimilar = false;
            }

        // jetzt - nachdem wir mindist kennen - können wir issimiliar am besten setzen
        for (CharDB cdb : cdbvec)
            for (CharRep crp : cdb.charReps) {
                if (!crp.issimilar)
                    continue;
                int dist = Matrix.diffSQR(crp.m, m, 3 * sr.getMinDist());
                if ((dist >= 29 * sr.getMinDist() / 10) && (dist > 1000))
                    crp.issimilar = false;
            }
        return sr;
    }

    SearchResult search_AbsM2(final Matrix m) {
        SearchResult sr = new SearchResult();

        for (CharDB cdb : cdbvec)
            for (CharRep crp : cdb.charReps) {
                if (!crp.issimilar) continue;
                int dist = Matrix.diffAbs(crp.m, m, 3 * sr.getMinDist());
                sr.update(cdb.C, dist);
            }
        return sr;
    }

    SearchResult search_SqrM2(Matrix m) {
        SearchResult sr = new SearchResult();

        for (CharDB cdb : cdbvec)
            for (CharRep crp : cdb.charReps) {
                if (!crp.issimilar) continue;
                int dist = Matrix.diffSqrM2(crp.m, m, 3 * sr.getMinDist());
                sr.update(cdb.C, dist);
            }
        return sr;
    }
    // //////////////////////////////////////////////////////////////////////////// //

    SearchResult search_Bases(Matrix m) {
        SearchResult sr = new SearchResult();

        for (CharDB cdb : cdbvec)
            for (CharRep crp : cdb.charReps) {
                if (!crp.issimilar) continue;
                int dist = Matrix.diffBased(crp.m, m, 3 * sr.getMinDist());
                sr.update(cdb.C, dist);
                if ((dist >= 29 * sr.getMinDist() / 10) && (dist > 1000))
                    crp.issimilar = false;
            }
        return sr;
    }

    // // ////////////////////////////////////////////////////////////////////////// //
    SearchResult search_db_ROW(Matrix m) {
        SearchResult sr = new SearchResult();

        for (CharDB cdb : cdbvec) {
            int min[] = new int[m_DIMR];
            Arrays.fill(min, DIFFMAXVAL);

            for (CharRep crp : cdb.charReps) {
                if (!crp.issimilar) continue;

                for (int r = 0; r < m_DIMR; r++) {
                    int dist = Matrix.diffRows(crp.m, m, r, 3 * min[r]);
                    if (dist < min[r]) min[r] = dist;
                }

                int dist = 0;
                for (int r = 0; r < m_DIMR; r++)
                    dist += min[r];
                sr.update(cdb.C, dist);
            }
        }
        return sr;
    }

    // // ////////////////////////////////////////////////////////////////////////////////// /
    SearchResult search_db_COL(Matrix m) {
        SearchResult sr = new SearchResult();

        for (CharDB cdb : cdbvec) {
            int min[] = new int[m_DIMC];
            Arrays.fill(min, DIFFMAXVAL);

            for (CharRep crp : cdb.charReps) {
                if (!crp.issimilar) continue;

                for (int c = 0; c < m_DIMC; c++) {
                    int dist = Matrix.diffCols(crp.m, m, c, 3 * min[c]);
                    if (dist < min[c])
                        min[c] = dist;
                }
            }
            int dist = 0;
            for (int c = 0; c < m_DIMC; c++)
                dist += min[c];
            sr.update(cdb.C, dist);
        }
        return sr;
    }

    // ////////////////////////////////////////////////////////////////////////////////// /
    SearchResult search_db_QUAD(Matrix m) {
        SearchResult sr = new SearchResult();
        int sz = m_DIMR * m_DIMC;

        for (CharDB cdb : cdbvec) {
            int min[] = new int[sz];
            Arrays.fill(min, DIFFMAXVAL);

            for (CharRep crp : cdb.charReps) {
                if (!crp.issimilar) continue;

                for (int r = 0; r < m_DIMR; r++) {
                    int rs = r * m_DIMC;
                    for (int c = 0; c < m_DIMC; c++) {
                        int a = rs + c;
                        int distloc = Matrix.diffQuad(crp.m, m, r, c, 3 * min[a]);
                        if (distloc < min[a])
                            min[a] = distloc;
                    }
                }
            }

            int dist = 0;
            for (int c = 0; c < sz; c++)
                dist += min[c];
            sr.update(cdb.C, dist);
        }
        return sr;
    }

    // ////////////////////////////////////////////////////////////////////
    private String translateCharName(char c) {
        if (c == '/') return "slash";
        if (c == '+') return "plus";
        if (c == ',') return "comma";
        if (c == '*') return "times";
        if (c == '.') return "dot";
        if (c == ':') return "colon";
        if (c == '?') return "question";
        if (c == '!') return "exclaim";
        if (c == ';') return "semicolon";
        if (c == '-') return "minus";
        if (c == '(') return "lbracket";
        if (c == ')') return "rbracket";
        char[] carr = {c};
        return new String(carr);
    }

    boolean loadDatabase(String dbname, String charstr) {
        String s = String.format("%s%dx%d", dbname, m_DIMR, m_DIMC);

        Debug.info("Loading Database:%s ", s);

        ArrayList<CharDB> arr = new ArrayList<>();
        int cnt = 0;
        for (char c : charstr.toCharArray()) {
            CharDB cdb = loadDBChar(s, c);
            arr.add(cdb);
            cnt += cdb.charReps.length;
        }
        this.cdbvec = arr.toArray(new CharDB[0]);
        Debug.debug("Database:%s has %d symbols", s, cnt);
        return true;
    }

    // ////////////////////////////////////////////////////////////////////////////
    int saveCharDB(String dbname, CharDB cdb) {
        String dbpath = String.format("%s/%s%dx%d", db_instpath, dbname, m_DIMR, m_DIMC);
        String charName = translateCharName(cdb.C);
        String fname = dbpath + "/" + charName + ".db";

        try {
            FileWriter w = new FileWriter(fname);
            for (CharRep crp : cdb.charReps) {
                Matrix m = crp.m;
                for (int v : m.m)
                    w.write(String.format("%3d ", v));
                w.write("\n");
            }
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
            Debug.error("Cant open database <%s> for writing!", fname);
            return 0;
        }
        return 1;
    }

    CharDB loadDBChar(String dbname, char C) {
        String charName = translateCharName(C);
        String cdbname = db_instpath + "/" + dbname + "/" + charName + ".db";

        Debug.info("Loading LoadDatabaseChar:%s", cdbname);
        CharDB cdb = new CharDB(C);
        BufferedReader r = null;
        try {
            ArrayList<CharRep> arr = new ArrayList<CharRep>();
            r = new BufferedReader(new FileReader(cdbname));
            String line;
            while ((line = r.readLine()) != null) {
                Matrix m = new Matrix(line, m_DIMR, m_DIMC);
                arr.add(new CharRep(m));
            }
            cdb.charReps = arr.toArray(new CharRep[0]);
            return cdb;
        } catch (FileNotFoundException ex) {
            Debug.error(ex.getMessage());
        } catch (IOException ex) {
            Debug.error(ex.getMessage());
        } finally {
            try {
                if (r != null) r.close();
            } catch (IOException ex) {
                Debug.error(ex.getMessage());
            }
        }
        return null;
    }
/////////////////////////////////////////////////////////

    private CharDB makeCharDB(String imgpath, char C) throws IOException {
        String charName = translateCharName(C);
        String dir = new String(imgpath + "/img") + charName + "/";
        String farr[] = Util.readDir(dir, ".tif");

        Debug.info("MakeDB %dx%d working on %s", m_DIMR, m_DIMC, dir);

        CharDB cdb = new CharDB(C);
        ArrayList<CharRep> charReps = new ArrayList<CharRep>();
        for (String fname : farr) {
            Image img = new Image(fname);
            Matrix m = img.computeMatrix(m_DIMR, m_DIMC);
            charReps.add(new CharRep(m));
        }
        cdb.charReps = charReps.toArray(new CharRep[0]);
        return cdb;
    }

    public void makeDataBase(String imgpath, String dbname, String charset) throws IOException {
        for (char c : charset.toCharArray()) {
            CharDB cdb = makeCharDB(imgpath, c);
            saveCharDB(dbname, cdb);
        }
    }
}
