package de.eb.ocr;

//~--- non-JDK imports --------------------------------------------------------
import de.eb.ocr.image.Image;
import de.eb.ocr.image.ImageLoader;
import de.eb.ocr.image.Matrix;
import de.eb.ocr.util.Debug;

public class Recm {

    public CharDatabase db;

    // //////////////////////////////////////////////////////////////////////////////// //
    public Recm(int dimr, int dimc, String dbname, String charstr) {
        db = new CharDatabase(dimr, dimc);
        db.loadDatabase(dbname, charstr);
    }

    // ////////////////////////////////////////////////////////////////////// //
    SearchResult recmmatrix(Matrix m, char sugg) {
        if (m == null)
            return null;
        db.resetSimilar();

        SearchResult resS = db.search_dbS(m);
        SearchResult resAbsM2 = db.search_AbsM2(m);
        boolean similar = resAbsM2.getMinDist() < 20 * m.getSize();
        if (resS.getMQ() > 2.4) {
            if (similar)
                resS.conf = 1;
            return resS;
        }

        SearchResult resB = db.search_Bases(m);
        if (resB.getMQ() > 2.4) {
            if (similar)
                resB.conf = 1;
            return resB;
        }

        SearchResult resCOL = db.search_db_COL(m);
        if (resCOL.getMQ() > 2.4) {
            if (similar)
                resCOL.conf = 1;
            return resCOL;
        }

        SearchResult resROW = db.search_db_ROW(m);
        if (resROW.getMQ() > 2.4) {
            if (similar)
                resROW.conf = 1;
            return resROW;
        }

        SearchResult resQUAD = db.search_db_QUAD(m);
        if (resQUAD.getMQ() > 2.4) {
            if (similar)
                resQUAD.conf = 1;
            return resQUAD;
        }

        Voter v = new Voter();
        v.add(resB);
        v.add(resCOL);
        v.add(resROW);
        v.add(resQUAD);
        return v.vote(similar);
    }

    Matrix getMatrix(Image x, int kind) {
        // m_cntchars++;
        Image z = null;
        switch (kind) {
            case 0:
                z = x.getSmallImage0();
                break;
            case 1:
                z = x.getSmallImage1();
                break;
            case 2:
                z = x.getSmallImage2();
                break;
            case 3:
                z = x.getSmallImage3();
                break;
            case 4:
                z = x.getSmallImage4();
                break;
            case 5:
                z = x.getSmallImage5();
                break;
        }
        if ((z == null) || (z.getSize() < db.m_DIMR * db.m_DIMC))
            return null;

        return z.computeMatrix(db.m_DIMR, db.m_DIMC);
    }

    SearchResult helpF(Matrix m, Image img, Voter v, int kind, char sugg) {
        Matrix m1 = getMatrix(img, kind);
        if (m.equals(m1))
            return null;

        SearchResult res = recmmatrix(m1, sugg);
        if (res != null && res.getConf() >= 1)
            return res;
        v.add(res);
        return null;
    }

    SearchResult recm1(Image img, char sugg) {

        Matrix m = getMatrix(img, 0);
        SearchResult sr = recmmatrix(m, sugg);
        if (sr.getConf() >= 1)
            return sr;

        Voter v = new Voter();
        v.add(sr);

        sr = helpF(m, img, v, 1, sugg);
        if (sr != null)
            return sr;
        sr = helpF(m, img, v, 2, sugg);
        if (sr != null)
            return sr;
        sr = helpF(m, img, v, 3, sugg);
        if (sr != null)
            return sr;
        sr = helpF(m, img, v, 4, sugg);
        if (sr != null)
            return sr;
        sr = helpF(m, img, v, 5, sugg);
        if (sr != null)
            return sr;
        return v.vote(false);
    }

    public SearchResult recm(Image img, char sugg) {
        if (img == null)
            return null;

        Image Y = null;
        if (img.getNr() < db.m_DIMR)
            Y = img.scaleImage(4, 4);
        if (img.getNc() <= 10)
            Y = img.scaleImage(4, 4);
        return recm1((Y == null) ? img : Y, sugg);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////
    public SearchResult recmfile(String fname, int x, int y, int w, int h, char sugg /* ='a' */) {
        Image img = ImageLoader.loadImage(fname, x, y, w, h);
        // if( true )return new SearchResult();
        if (img == null) {
            Debug.error("Cant load TiffFile:<%s>", fname);
            return null;
        }
        return recm(img, sugg);
    }
    // String recmseg(String fname, int x, int y, int w, int h, int nsegs ){
    // String res;
    //
    // Image img = new Image( fname, x, y, w, h );
    // if( img==null ) return "";
    // //static int n = 0; String s; s.Format( "c:\\temp\\aaa%2.2d.tif",n++ ); saveTiff( img, s );
    //
    // int nr = img.getNr();
    // int nc = img.getNc();
    // Image digs[] = new Image[128];
    // int nglyphs = segmentate4( img, digs );
    //
    // for( int i=0; i<nglyphs; i++ ){ const double thresh = 0.3;
    // AllResults resvec;
    // String c = recm( digs[i], 1, resvec, '+' );
    //
    // //if( resvec.conf<1.0 ) Util.DebugMsg( "INFO:conf:%lf", resvec.conf );
    // if( resvec.conf<thresh ){
    // Util.DebugMsg( "INFO:c:%c secbest:%c conf:%lf", c, resvec.secbest, resvec.conf );
    // //String s; s.Format("c:\\tempo\\%c_%c_%d_%s.tif", resvec.conf>0.5?c:'+', c, i, TmpFileNameLoc() ); saveTiff(
    // digs[i], s );
    // c = recm( digs[i], 2, resvec, '+' );
    // }
    // if( resvec.conf<thresh ){
    // Util.DebugMsg( "INFO:c:%c secbest:%c conf:%lf", c, resvec.secbest, resvec.conf );
    // //String s; s.Format("c:\\tempo\\%c_%c_%d_%s.tif", resvec.conf>0.5?c:'+', c, i, TmpFileNameLoc() ); saveTiff(
    // digs[i], s );
    // c = recm( digs[i], 3, resvec, '+' );
    // }
    //
    // res += resvec.conf>thresh?c:'+';
    // freeimage( digs[i] );
    // }
    // //if( res.Find("+")>0 || res.GetLength()<12 ){ String sn; sn.Format( "c:\\temp\\AAAA_%s.tif", res ); saveTiff(
    // img, sn ); }
    // freeimage( img );
    // return res;
    // }
}
