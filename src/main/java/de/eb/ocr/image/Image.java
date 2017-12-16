package de.eb.ocr.image;

import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import de.eb.ocr.util.Counter;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

public class Image {

  static final int BLACK = 1;
  static final int WHITE = 0;
  static final int GLYPHPART_MINSIZE = 3; // ?? was ist der optimale Wert??
  private final int nr; // # rows of image
  private final int nc; // # cols of image
  private final int[] data;
  // private final BufferedImage bi;
  // private int dpi;

  boolean isZero(double x) {
    return x <= 0.0001 && x >= -0.0001;
  }

  boolean inRange(int r, int c) {
    return r >= 0 && c >= 0 && r < nr && c < nc;
  }

  public int getSize() {
    return nr * nc;
  }

  public int get(int c, int r) {
    return data[c + r * nc];
  }

  public void set(int c, int r, int v) {
    data[c + r * nc] = v;
  }

  public int getNc() {
    return nc;
  }

  public int getNr() {
    return nr;
  }

  private BufferedImage getBufferedImage(String filename) {
    InputStream is = null;
    try {
      is = new BufferedInputStream(new FileInputStream(filename));
      return ImageIO.read(is);
    } catch (FileNotFoundException ex) {
      System.err.println(ex.getMessage());
    } catch (IOException ex) {
      System.err.println(ex.getMessage());
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException ex) {
        System.err.println(ex.getMessage());
      }
    }
    return null;
  }

  private BufferedImage getBufferedImageSanselan(String filename) {
    InputStream is = null;
    try {
      is = new BufferedInputStream(new FileInputStream(filename));
      return Sanselan.getBufferedImage(is);
    } catch (ImageReadException ex) {
      Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, ex);
    } catch (FileNotFoundException ex) {
      System.err.println(ex.getMessage());
    } catch (IOException ex) {
      System.err.println(ex.getMessage());
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException ex) {
        System.err.println(ex.getMessage());
      }
    }
    return null;
  }

  // //////////////////////////////////////////////////////////////////
  public Image(int nr, int nc) { // constructor with undefined values
    this.nr = nr;
    this.nc = nc;
    data = new int[nr * nc];
  }

  public Image(String fname) throws IOException, RuntimeException {
    BufferedImage bi = getBufferedImage(fname);
    nr = bi.getHeight();
    nc = bi.getWidth();
    int x = nr * nc;
    data = new int[x];
    bi.getData().getPixels(0, 0, nc, nr, data);
  }

  public Image(  int nr, int nc, int data[]){
    this.nr = nr;
    this.nc = nc;
    this.data = data;
  }

  //////////////////////////////////////////////////////////////////////////////////////
  public void invert() {
    int sz = nr * nc;
    for (int i = 0; i < sz; i++) {
      data[i] = data[i] == 0 ? 1 : 0;
    }
  }

  public boolean isInverted() {
    int cntBlack = 0;
    for (int r = 10; r < nr - 10; r++) {
      int rr = r * nc;
      for (int c = 10; c < nc - 10; c++) {
        if (data[rr + c] == BLACK) {
          cntBlack++;
        }
      }
    }
    return cntBlack > nr * nc / 2;
  }

  Image createImageWithMargin() {
    final double MAXRATIO = 4;
    double r1 = (double) nc / (double) nr;
    if (r1 < MAXRATIO && r1 > 1 / MAXRATIO) {
      return this;
    }
    // Aspect ratio too large/small
    int newnr, newnc;
    if (r1 >= MAXRATIO) {
      newnc = nc;
      newnr = nc * 6 / 8;
    } else {
      newnr = nr;
      newnc = nr * 6 / 8;
    }
    Image X = new Image(newnr, newnc);
    if (r1 < MAXRATIO) {
      for (int r = 0; r < newnr; r++) {
        for (int c = 0; c < nc; c++) {
          X.set((newnc - nc) / 2 + c, r, get(c, r));
        }
      }
    } else {
      for (int c = 0; c < newnc; c++) {
        for (int r = 0; r < nr; r++) {
          X.set(c, (newnr - nr) / 2 + r, get(c, r));
        }
      }
    }
    // saveTiff( newimg, "c:\\temp\\newimg.tif" );
    return X;
  }

  // /////////////////////////////////////////////////////////////
  public boolean saveTiff(String fname) {
    Iterator iter = ImageIO.getImageWritersByFormatName("TIFF");
    ImageWriter writer = (ImageWriter) iter.next();
    try {
      BufferedImage bi = new BufferedImage(nc, nr, BufferedImage.TYPE_BYTE_BINARY);
      bi.getRaster().setPixels(0, 0, nc, nr, this.data);
      ImageOutputStream ios = ImageIO.createImageOutputStream(new File(fname));
      TIFFImageWriteParam writeParam = (TIFFImageWriteParam) writer.getDefaultWriteParam();
      writeParam.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
      writeParam.setCompressionType("CCITT T.6");
      writer.setOutput(ios);
      IIOImage iioi = new IIOImage(bi, null, null);
      writer.write(null, iioi, writeParam);
      ios.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return false;
    }
    return true;
  }

  public void trace() {
    return; // !!!!!!
//      for (int r = 0; r < this.getNr(); r++) {
//         StringBuilder line = new StringBuilder();
//         for (int c = 0; c < getNc(); c++) {
//            String s = (data[r * getNc() + c] == 0 ) ? " "  : "#";
//            line.append(s);
//         }
//         System.out.println(String.format("INFO:%d %s", r, line));
//      }
//      System.out.println();
  }

  public Matrix computeMatrix(int DIMR, int DIMC) {
    Image X = getSmallImage0();
    Image Y = null;
    if (X.getNr() < DIMR) {
      Y = X.scaleImage(5, 5);
    } else if (X.getNc() <= 10) {
      Y = X.scaleImage(5, 5);
    }
    if (Y != null) {
      X = Y;
    }
    int lnr = X.getNr();
    int lnc = X.getNc();
    //X.trace();
    Matrix m = new Matrix(DIMR, DIMC);
    for (int r = 0; r < lnr; r++) {
      int sr = (r * DIMR / lnr) * DIMC;
      int rr = X.nc * r;
      for (int c = 0; c < lnc; c++) {
        if (X.data[c + rr] == Image.BLACK) {
          m.m[sr + c * DIMC / lnc]++;
        }
      }
    }
    //m.trace();
    int MM = Math.max(1, lnr * lnc / m.getSize());
    final int NN = 128;
    for (int n = 0; n < m.getSize(); n++) {
      if (m.m[n] != 0) {
        m.m[n] = m.m[n] * NN / MM;
      }
    }
    //m.trace();
    m.initM2();
    return m;
  }

  // //////////////////////////////////////////////////////////////////
  public void despeckle(final int N) {     // Flecken kleiner als N Pixel werden gesubert
    for (int r = 2; r < nr - 2; r++) {
      int rr = r * nc;
      for (int c = 2; c < nc - 2; c++) {
        if (data[rr + c] != BLACK) {
          continue;
        }
        int cnt = 0;
        for (int i = -2; i <= 2; i++) {
          for (int j = -2; j <= 2; j++) {
            if (data[(r + i) * nc + c + j] == BLACK) {
              cnt++;
            }
          }
        }
        if (cnt <= N) {
          for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
              data[(r + i) * nc + c + j] = WHITE;
            }
          }
        }
      }
    }
  }

  public Image rotate(double angle) {
    Image dest = new Image(nr, nc);
    // DebugMsg( "INFO:RotateImage alpha=%lf deg alpha=%lf", angle,angle/(2*M_PI)*360 );
    final int SCALE = 1 << 12;
    // Rotation assumes that the origin is the center of src-image
    final int sina = Math.round((float) (Math.sin(angle) * SCALE));
    final int cosa = Math.round((float) (Math.cos(angle) * SCALE));
    final int cr = nr / 2;
    final int cc = nc / 2;
    for (int r = 0; r < nr; r++) {
      final int a = (r - cr) * cosa;
      final int b = (r - cr) * sina;
      for (int c = 0; c < nc; c++) {
        int r1 = (a - (c - cc) * sina) / SCALE + cr;
        int c1 = (b + (c - cc) * cosa) / SCALE + cc;
        if (r1 >= 0 && r1 < nr && c1 >= 0 && c1 < nc) {
          int x = get(c1, r1);
          if (x != 0) {
            dest.set(c, r, x);
          }
        }
      }
    }
    // DebugMsg( "INFO:rotate end" );
    return dest;
  }

  public Image rotate90() {
    Image rot = new Image(nc, nr);
    for (int r = 0; r < nr; r++) {
      for (int c = 0; c < nc; c++) {
        int x = get(c, nr - 1 - r);
        if (x != 0) {
          rot.set(r, c, x);
        }
      }
    }
    return rot;
  }

  public Image rotate180() {
    Image rot = new Image(nr, nc);
    for (int r = 0; r < nr; r++) {
      for (int c = 0; c < nc; c++) {
        int x = get(c, r);
        if (x != 0) {
          rot.set(nc - 1 - c, nr - 1 - r, x);
        }
      }
    }
    return rot;
  }

  public Image scaleImage(double sx, double sy) {
    final int SCALE = 4096;
    // Util.DebugMsg( "DEBUG:scaleImage sx=%lf sy=%lf", sx, sy );
    int scalex = (int) (1 / sx * SCALE);
    int scaley = (int) (1 / sy * SCALE);
    Image X = new Image((int) (nr * sy), (int) (nc * sx));
    for (int r = 0; r < nr; r++) {
      int rr = scaley * r / SCALE;
      if (rr >= 0 && rr < nr) {
        for (int c = 0; c < nc; c++) {
          int cc = scalex * c / SCALE;
          if (cc >= 0 && cc < nc) {
            X.set(cc, rr, get(c, r));
          }
        }
      }
    }
    return X;
  }

  // ///////////////////////////////////////////////////////////////////////////////
  Rectangle box(final int val) {
    // Berechne umschreibendes Rechteck von Glyph
    int rmin = nr - 1;
    int rmax = 0;
    int cmin = nc - 1;
    int cmax = 0;
    for (int r = 0; r < nr; r++) {
      int rr = r * nc;
      for (int c = 0; c < nc; c++) {
        if (data[c + rr] == val) {
          if (r < rmin) {
            rmin = r;
          } else if (r > rmax) {
            rmax = r;
          }
          if (c < cmin) {
            cmin = c;
          } else if (c > cmax) {
            cmax = c;
          }
        }
      }
    }
    return new Rectangle(cmin, rmin, cmax - cmin, rmax - rmin);
  }

  int cntarea(final int val) {// Count the number of pixels having value val
    int cnt = 0;
    for (int v : data) {
      if (v == val) {
        cnt++;
      }
    }
    return cnt;
  }

  int cntarea(int rmin, int rmax, int cmin, int cmax, int val) {
    // Count the number of pixels having value VAL in RECT!
    final int rdr = nr / 10; // ~15
    final int rdc = nc / 10; // ~10
    final int stepr = nr / 7;
    final int stepc = nc / 10;
    rmin = rmin - stepr;
    cmin = cmin - stepc;
    rmax = rmax + stepr;
    cmax = cmax + stepc;
    int cnt = 0;
    int ra = Math.max(rdr, rmin);
    int ca = Math.max(rdc, cmin);
    int re = Math.min(rmax, nr - rdr);
    int ce = Math.min(cmax, nc - rdc);
    for (int r = ra; r < re; r++) {
      int rr = r * nc;
      for (int c = ca; c < ce; c++) {
        if (data[c + rr] == val) {
          cnt++;
        }
      }
    }
    return cnt;
  }

  int cntarea(Rectangle r, int val) {
    // Count the number of pixels having value VAL in RECT!
    return cntarea(r.x, r.x + r.height, r.y, r.y + r.width, val);
  }

  int cntBlackInNeighbourhood(int val) {
    int cnt = 0;
    int r = 0, c = 0;
    for (r = 0; r < nr; r++) {
      for (c = 0; c < nc; c++) {
        if (get(c, r) == val) {
          break;
        }
      }
      if (get(c, r) == val) {
        break;
      }
    }
    for (int i = -2; i <= 2; i++) {
      for (int j = -2; j <= 2; j++) {
        int rr = r + i;
        int cc = c + j;
        if (rr >= 0 && rr < nr && cc >= 0 && cc < nc) {
          if (get(cc, rr) != WHITE) {
            cnt++;
          }
        }
      }
    }
    return cnt;
  }

  Image getpartimage(int row, int col, int w, int h) {
    if (row > nr || col > nc) {
      return null;
    }
    row = Math.max(0, row);
    col = Math.max(0, col);
    h = Math.min(h, nr - row);
    w = Math.min(w, nc - col);
    Image z = new Image(h, w);
    for (int r = 0; r < h; r++) {
      System.arraycopy(data, col + (row + r) * nc, z.data, r * z.nc, w);
    }
    z.invert();
    return z;
  }

  void mark8(int r, int c, int val, Counter cnt) {
    if (get(c, r) != BLACK) {
      return;
    }
    if (cnt.getCnt() > 3000) {
      return; // to avoid Stack Overflow
    }
    set(c, r, val);
    cnt.inc();
    if (r > 0 && r < nr - 1 && c > 0 && c < nc - 1) {
      mark8(r + 1, c + 1, val, cnt);
      mark8(r + 1, c, val, cnt);
      mark8(r + 1, c - 1, val, cnt);
      mark8(r, c + 1, val, cnt);
      mark8(r, c - 1, val, cnt);
      mark8(r - 1, c + 1, val, cnt);
      mark8(r - 1, c, val, cnt);
      mark8(r - 1, c - 1, val, cnt);
    } else {
      if (inRange(r + 1, c)) {
        mark8(r + 1, c, val, cnt);
      }
      if (inRange(r + 1, c - 1)) {
        mark8(r + 1, c - 1, val, cnt);
      }
      if (inRange(r, c + 1)) {
        mark8(r, c + 1, val, cnt);
      }
      if (inRange(r, c - 1)) {
        mark8(r, c - 1, val, cnt);
      }
      if (inRange(r - 1, c + 1)) {
        mark8(r - 1, c + 1, val, cnt);
      }
      if (inRange(r - 1, c)) {
        mark8(r - 1, c, val, cnt);
      }
      if (inRange(r - 1, c - 1)) {
        mark8(r - 1, c - 1, val, cnt);
      }
    }
  }

  void mark8(Image x, int r, int c, int val, Counter c1, Counter c2) {
    if (!x.inRange(r, c)) {
      return;
    }
    if (x.get(c, r) != BLACK) {
      return;
    }
    set(c, r, val);
    if (c < c1.getCnt()) {
      c1.setCnt(c);
    }
    if (c > c2.getCnt()) {
      c2.setCnt(c);
    }
    mark8(x, r + 1, c + 1, val, c1, c2);
    mark8(x, r + 1, c, val, c1, c2);
    mark8(x, r + 1, c - 1, val, c1, c2);
    mark8(x, r, c + 1, val, c1, c2);
    mark8(x, r, c - 1, val, c1, c2);
    mark8(x, r - 1, c + 1, val, c1, c2);
    mark8(x, r - 1, c, val, c1, c2);
    mark8(x, r - 1, c - 1, val, c1, c2);
  }

  void mark8seg4(Image x, int r, int c, int val, Rectangle rect, Counter cnt) {
    if (!x.inRange(r, c)) {
      return;
    }
    if (x.get(c, r) != BLACK) {
      return;
    }
    if (cnt.getCnt() > 3000) {
      return; // to avoid Stack Overflow
    }
    cnt.inc();
    x.set(c, r, val);
    if (r < rect.y) {
      rect.y = r;
    }
    if (r > rect.width) {
      rect.width = r;
    }
    if (c < rect.x) {
      rect.x = c;
    }
    if (c > rect.height) {
      rect.height = c;
    }
    final int N = 7;// 2*x->dpi/100 + 1;
    final int M = 4;// x->dpi/100 + 1;
    for (int i = -N; i <= N; i++) {
      for (int j = -M; j <= M; j++) {
        if (j == 0 && i == 0) {
          continue;
        }
        if (!x.inRange(r + i, c + j)) {
          continue;
        }
        if (x.get(c + j, r + i) != BLACK) {
          continue;
        }
        mark8seg4(x, r + i, c + j, val, rect, cnt);
      }
    }
  }

  int region8(int val) {
    // Locate a black region and mark it with val. 8-connected
    for (int r = 0; r < nr; r++) {
      int rr = r * nc;
      for (int c = 0; c < nc; c++) {
        if (data[c + rr] == BLACK) {
          Counter cnt = new Counter(0);
          mark8(r, c, val, cnt);
          return cnt.getCnt();
        }
      }
    }
    return 0;
  }

  int region8vert(int val) {
    // Locate a black region and mark it with val. 8-connected
    // Look columns first
    for (int c = 0; c < nc; c++) {
      int cc = c * nr;
      for (int r = 0; r < nr; r++) {
        if (data[r + cc] == BLACK) {
          Counter cnt = new Counter(0);
          mark8(r, c, val, cnt);
          return cnt.getCnt();
        }
      }
    }
    return 0;
  }

  int region8(Rectangle r, int val) {
    // Locate a black region and mark it with val. 8-connected
    if (r.x < 0) {
      r.x = 0;
    }
    if (r.height >= nr) {
      r.height = nr - 1;
    }
    if (r.y < 0) {
      r.y = 0;
    }
    if (r.width >= nc) {
      r.width = nc - 1;
    }
    for (int y = r.y; y <= r.height; y++) {
      for (int x = r.x; x < r.width; x++) {
        if (get(x, y) == BLACK) {
          Counter cnt = new Counter(0);
          mark8(y, x, val, cnt);
          return cnt.getCnt();
        }
      }
    }
    return 0;
  }

  void remark(int v1, int v2) { // Change all pixels with value V1 to value V2.
    int n = getSize();
    for (int i = 0; i < n; i++) {
      if (data[i] == v1) {
        data[i] = v2;
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  Image extglyph0() { // Extract a glyph
    int max = 0;
    int na = 0;
    int cntparts = 0;
    // Find biggest region
    while ((na = region8(9)) > 0) {
      if (na <= GLYPHPART_MINSIZE) {
        remark(9, 12); // So kleine Flecken werden ignoriert!
      } else {
        cntparts++;
        if (na > max) {
          if (max > 0) {
            remark(10, 11);
          }
          max = na;
          remark(9, 10);
        } else {
          remark(9, 11);
        }
      }
    }
    if (cntparts == 1) {
      remark(10, BLACK);
      return this;
    }
    int cnt = max;
    remark(11, BLACK);
    // int rmin = -1, cmin = -1, rmax = -1, cmax = -1;
    while ((na = region8(9)) > 0) {
      if (na > max / 6) {// && rmax > 5 && cmax > 5 && rmin <z->nr-5 && cmin < z->nc-5 ){
        cnt += na;
        remark(9, 10);
      } else {
        Rectangle rec = box(10);
        int n = cntarea(rec, 9);
        if (cntparts > 2 && n > GLYPHPART_MINSIZE) {
          cnt += na;
          remark(9, 10);
        } else {
          remark(9, 12);
        }
      }
    }
    double q = ((double) cnt) / (nc * nr);
    if (q < 0.005) {
      return null; // das kann kein Zeichen sein!
    }
    remark(10, BLACK);
    return this;
  }

  Image extglyph1(int n) { //
    int na_max = 0;
    int na = 0;
    // Find biggest region
    while ((na = region8(9)) > 0) {
      if (na > na_max) {
        if (na_max > 0) {
          remark(10, 11);
        }
        na_max = na;
        remark(9, 10);
      } else {
        remark(9, 11);
      }
    }
    remark(11, BLACK);
    while (true) {
      Rectangle r1 = box(10);
      Rectangle r2 = new Rectangle(r1.x - n, r1.x + r1.height + n, r1.y - n, r1.y + r1.height + n);
      int cnt = region8(r2, 9);
      if (cnt == 0) {
        break;
      }
      na_max += cnt;
    }
    remark(BLACK, 12);
    remark(9, BLACK);
    remark(10, BLACK);
    if (((double) na_max) / (nc * nr) < 0.005) {
      return null; // das kann kein Zeichen sein!
    }
    return this;
  }

  Image extglyph2() {
    int cnt = 0;
    int na = 0;
    while ((na = region8(9)) > 0) {
      if (na > GLYPHPART_MINSIZE) {
        cnt += na;
        remark(9, 10);
      } else {
        int n = cntBlackInNeighbourhood(9);
        cnt += n;
        remark(9, n < GLYPHPART_MINSIZE ? 11 : 10);
      }
    }
    remark(10, BLACK);
    if (((double) cnt) / (nc * nr) < 0.005) {
      return null; // das kann kein Zeichen sein!
    }
    return this;
  }

  Image extglyph3() { // biggest Region
    int max = 0;
    int na = 0;
    // Find biggest region
    while ((na = region8(9)) > 0) {
      if (na > max) {
        if (max > 0) {
          remark(10, 11);
        }
        max = na;
        remark(9, 10);
      } else {
        remark(9, 11);
      }
    }
    remark(10, BLACK);
    if (((double) max) / (nc * nr) < 0.005) {
      return null; // das kann kein Zeichen sein!
    }
    return this;
  }

  Image extglyph4() { // Find biggest region
    Counter n = new Counter(0);
    for (int c = 0; c < nc; c++) {
      if (get(c, 0) == BLACK) {
        mark8(0, c, 11, n);
      }
    }
    for (int c = 0; c < nc; c++) {
      if (get(c, nr - 1) == BLACK) {
        mark8(nr - 1, c, 11, n);
      }
    }
    for (int r = 0; r < nr; r++) {
      if (get(0, r) == BLACK) {
        mark8(r, 0, 11, n);
      }
    }
    for (int r = 0; r < nr; r++) {
      if (get(nc - 1, r) == BLACK) {
        mark8(r, nc - 1, 11, n);
      }
    }
    return extglyph0();
  }

  Image extglyph6() {
    int na = region8vert(10);
    if (((double) na) / (nc * nr) < 0.005) {
      return null; // das kann kein Zeichen sein!
    }
    return this;
  }

  public int cntglyphparts() {
    int cnt = 0;
    int na;
    while ((na = region8(9)) > 0) {
      if (na > GLYPHPART_MINSIZE) {
        cnt++;
      }
    }
    remark(9, BLACK);
    return cnt;
  }

  Image extract(final int val) {
    Rectangle rec = box(val);
    Image z = new Image(rec.height, rec.width);
    if (z == null) {
      return null;
    }
    int lnr = z.nr;
    int lnc = z.nc;
    for (int r = 0; r < lnr; r++) // Copy VAL pixels into Z
    {
      for (int c = 0; c < lnc; c++) {
        z.set(c, r, get(rec.x + c, rec.y + r) == val ? val : WHITE);
      }
    }
    return z;
  }

  public Image getSmallImage0() {
    if (extglyph0() == null) {
      return null;
    }
    Image z = extract(BLACK); // Kopiere nur Pixel, die schwarz sind
    remark(11, BLACK);
    remark(12, BLACK);
    return z.createImageWithMargin();
  }

  public Image getSmallImage1() {
    Image x = extglyph1(12);
    if (x == null) {
      return null;
    }
    Image z = extract(BLACK); // Kopiere nur Pixel, die schwarz sind
    remark(11, BLACK);
    remark(12, BLACK);
    return z.createImageWithMargin();
  }

  public Image getSmallImage2() {
    Image x = extglyph2();
    if (x == null) {
      return null;
    }
    Image z = extract(BLACK); // Kopiere nur Pixel, die schwarz sind
    remark(11, BLACK);
    return z.createImageWithMargin();
  }

  public Image getSmallImage3() {
    Image z = extract(BLACK); // Kopiere nur Pixel, die schwarz sind
    remark(11, BLACK);
    remark(12, BLACK);
    return z.createImageWithMargin();
  }

  public Image getSmallImage4() { // Alle Pixels
    Image x = extglyph4(); // biggest Region
    if (x == null) {
      return null;
    }
    Image z = extract(BLACK); // Kopiere nur Pixel, die schwarz sind
    remark(11, BLACK);
    remark(12, BLACK);
    return z.createImageWithMargin();
  }

  public Image getSmallImage5() { // Alle Pixels
    Image z = extract(BLACK); // Kopiere nur Pixel, die schwarz sind
    return z.createImageWithMargin();
  }

  public Image getSmallImage6() { // First from left
    Image x = extglyph6();
    if (x == null) {
      return null;
    }
    Image z = extract(10); // Kopiere nur Pixel, die 10 sind
    remark(10, BLACK);
    return z.createImageWithMargin();
  }
}
