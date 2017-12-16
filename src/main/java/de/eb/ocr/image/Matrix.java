package de.eb.ocr.image;

import de.eb.ocr.util.SimpleMath;
import java.util.StringTokenizer;

public class Matrix {

  public int DIMC;
  public int DIMR;
  public int m2[];
  public int m[];
  private int sz;

  public Matrix(int DIMR, int DIMC) {
    this.DIMR = DIMR;
    this.DIMC = DIMC;
    sz = DIMR * DIMC;
    m = new int[sz];
    m2 = new int[sz];
  }

  public Matrix(String line, int DIMR, int DIMC) {
    this.DIMR = DIMR;
    this.DIMC = DIMC;
    sz = DIMR * DIMC;
    m = new int[sz];
    m2 = new int[sz];

    StringTokenizer st = new StringTokenizer(line);
    for (int i = 0; i < sz; i++) {
      String s = st.nextToken(" ");
      m[i] = Integer.parseInt(s);
    }
    initM2();
  }

  public boolean equals(Matrix m) {
    if (m == null) {
      return false;
    }

    for (int i = 0; i < sz; i++) {
      if (this.m[i] != m.m[i]) {
        return false;
      }
    }
    return true;
  }

  public int getSize() {
    return sz;
  }

  int computeBasedLocal(int row, int col) {
    int sum = 0;
    int cnt = 0;
    int minr = Math.max(0, row - 1);
    int minc = Math.max(0, col - 1);
    int maxr = Math.min(DIMR, row + 1);
    int maxc = Math.min(DIMC, col + 1);
    for (int r = minr; r < maxr; r++) {
      int rr = r * DIMC;
      for (int c = minc; c < maxc; c++) {
        sum += m[rr + c];
        cnt++;
      }
    }
    return sum / cnt;
  }

  public void initM2() {
    for (int r = 0; r < DIMR; r++) {
      int rr = r * DIMC;
      for (int c = 0; c < DIMC; c++) {
        m2[rr + c] = computeBasedLocal(r, c);
      }
    }
  }

  public void save(String imgname) {
    getImageFromMatrix().saveTiff(imgname);
  }

  Image getImageFromMatrix() {
    Image x = new Image(DIMR * 10, DIMC * 10);
    for (int r = 0; r < DIMR; r++) {
      for (int c = 0; c < DIMC; c++) {
        int n = (m[r * DIMC + c] > 0) ? 1 : 0;
        for (int i = 0; i < 10; i++) {
          for (int j = 0; j < 10; j++) {
            x.set(r * 10 + i, c * 10 + j, n);
          }
        }
      }
    }
    return x;
  }

  public void trace() {
    // return; // !!!!!!
    for (int r = 0; r < DIMR; r++) {
      StringBuilder line = new StringBuilder();
      for (int c = 0; c < DIMC; c++) {
        String s = String.format((m[r * DIMC + c] == 0)
                ? "    "
                : "%3d ", m[r * DIMC + c]);
        line.append(s);
      }
      System.out.println(String.format("INFO:%d %s", r, line));
    }
    System.out.println();
  }

  // ///////////////////////////////////////////////////////////////////////////////////////
  public int[] getSortVec() {
    // Liefert einen Vektor von Indizes in die Matrix, mit absteigenden
    // Werten der Matrix.
    // Dadurch wird bei Matrix-Diff möglichst schnell eine großer Wert gefunden!
    int sortvec[] = new int[sz];
    for (int j = 0; j < sz; j++) {
      sortvec[j] = j;
    }

    for (int i = 0; i < sz; i++) {
      for (int j = i + 1; j < sz; j++) {
        if (m[sortvec[i]] < m[sortvec[j]]) {
          int tmp = sortvec[i];
          sortvec[i] = sortvec[j];
          sortvec[j] = tmp;
        }
      }
    }
    return sortvec;
  }

  // ///////////////////////////////////////////////////////////////////////////////////////
  static public int diffABS(final Matrix m1, final Matrix m2) {
    int res = 0;
    for (int i = 0; i < m1.sz; i++) {
      res += Math.abs(m1.m[i] - m2.m[i]);
    }
    return res;
  }

  static public int diffAbs(final Matrix m1, final Matrix m2, final int min) {
    int res = 0;

    for (int i = 0; i < m1.sz; i++) {
      res += Math.abs(m1.m2[i] - m2.m2[i]);
      if (res >= min) {
        return res;
      }

    }
    return res;
  }

  static public int diffSQR(final Matrix m1, final Matrix m2, final int min) {
    int res = 0;

    for (int i = 0; i < m1.sz; i++) {
      int d = m1.m[i] - m2.m[i];
      res += d * d;
      if (res >= min) {
        return res;
      }
    }
    return res;
  }

  static public int diffSQR(final Matrix m1, final Matrix m2) {
    int res = 0;

    for (int i = 0; i < m1.sz; i++) {
      int d = m1.m[i] - m2.m[i];
      res += d * d;
    }
    return res;
  }

  static public int diffSQR(final Matrix m1, final Matrix m2, final int sortvec[], final int min) {
    int res = 0;

    for (int i = 0; i < m1.sz; i++) {
      final int n = sortvec[i];
      final int d = (m1.m[n] - m2.m[n]);
      res += d * d;
      if (res > min) {
        return res;
      }
    }
    return res;
  }

  static public int diffSimple(final Matrix m1, final Matrix m2) {
    int res = 0;

    for (int i = 0; i < m1.sz; i++) {
      boolean b1 = Math.abs(((m1.m[i] > 0) ? 1 : 0) - ((m2.m[i] > 0) ? 1 : 0)) > 0;
      boolean b2 = Math.abs(m1.m[i] - m2.m[i]) > 30;
      res += (b1 || b2) ? 1 : 0;
    }
    return res;
  }

  static public int diffBased(final Matrix m1, final Matrix m2, final int min) {
    int res = 0;
    for (int n = 0; n < m1.sz; n++) {
      int d1 = Math.abs(m1.m[n] - m2.m[n]);
      int d2 = Math.abs(m1.m2[n] - m2.m2[n]);
      res += d1 * (1 + d2);
      if (res > min) {
        return res;
      }
    }
    return res;
  }

  static public int diffSqrM2(final Matrix m1, final Matrix m2, final int min) {
    int res = 0;
    for (int i = 0; i < m1.sz; i++) {
      res += (m1.m2[i] - m2.m2[i]) * (m1.m2[i] - m2.m2[i]);
      if (res > min) {
        return res;
      }
    }
    return res;
  }

//////////////////////////////////////////////////////////////////////////
  static public int diffRows(final Matrix m1, final Matrix m2, final int row, final int min) {
    int res = 0;
    final int n = m1.DIMR / 6;
    final int a = Math.max(0, row - n);
    final int e = Math.min(m1.DIMR - 1, row + n);

    for (int r = a; r <= e; r++) {
      int rs = r * m1.DIMC;
      int drow = 0;

      for (int c = 0; c < m1.DIMC; c++) {
        int nn = rs + c;
        int d = m1.m[nn] - m2.m[nn];
        drow += d * d;
      }
      res += (r == row) ? 2 * drow : drow;
      if (res > min) {
        return res;
      }
    }
    return res;
  }

  static public int diffCols(final Matrix m1, final Matrix m2, final int col, final int min) {
    int res = 0;
    final int n = m1.DIMC / 4;
    final int a = Math.max(0, col - n);
    final int e = Math.min(m1.DIMC - 1, col);

    for (int r = 0; r < m1.DIMR; r++) {
      int rs = r * m1.DIMC;
      int dcol = 0;
      int c;

      for (c = a; c <= e; c++) {
        int nn = rs + c;
        int d = m1.m[nn] - m2.m[nn];
        dcol += d * d;
      }
      res += (c == col) ? 2 * dcol : dcol;
      if (res > min) {
        return res;
      }
    }

    return res;
  }

  static public int diffQuad(final Matrix m1, final Matrix m2, final int row, final int col, final int min) {
    int res = 0;
    final int nr = m1.DIMR / 6;
    final int nc = m1.DIMC / 4;
    final int ac = Math.max(0, col - nc - 1);
    final int ec = Math.min(m1.DIMC - 1, col + nc);
    final int ar = Math.max(0, row - nr - 1);
    final int er = Math.min(m1.DIMR - 1, row + nr);

    for (int r = ar; r <= er; r++) {
      int rs = r * m1.DIMC;

      for (int c = ac; c <= ec; c++) {
        int n = rs + c;
        int d = (m1.m[n] - m2.m[n]);
        int sqrd = d * d;

        if ((r == 0) && (c == 0)) {
          sqrd *= 2;
        }
        res += sqrd;
        if (res > min) {
          return res;
        }
      }
    }
    return res;
  }
}
