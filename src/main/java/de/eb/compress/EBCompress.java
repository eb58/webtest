package de.eb.compress;

import de.eb.ocr.util.StopWatch;
import de.eb.ocr.util.Util;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;

public class EBCompress {

  public static String compressStringBase64(String s) {
    try {
      ByteArrayOutputStream bs = new ByteArrayOutputStream();
      GZIPOutputStream gzip = new GZIPOutputStream(bs);
      gzip.write(s.getBytes());
      gzip.close();
      return Base64.encodeBase64String(bs.toByteArray());
    } catch (IOException ex) {

    }
    return s;
  }

  public static String decompressStringBase64(String s) throws IOException {
    BufferedReader bf = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(Base64.decodeBase64(s)))));
    StringBuilder sb = new StringBuilder();
    char cbuf[] = new char[100000];
    int len;
    while ((len = bf.read(cbuf)) > 0) {
      sb.append(cbuf, 0, len);
    }
    return sb.toString();
  }

  public static void main(String... args) throws IOException {
    String s = Util.readFile("c:/temp/new1.txt");
    StopWatch sw = new StopWatch();
    String xx = compressStringBase64("Hallo Welt");
    System.out.println(decompressStringBase64(compressStringBase64("Hallo Welt")));
    System.out.println(decompressStringBase64(compressStringBase64("Hallo vcfvcvvcyxvvg f gvklfdj gkldfjg lkd fjgfkldskjflksad jkflkj sdfflk dfsjflkjdsdlkjsadlk jdsdkljdskfd jTest fdk")));

    System.out.println("Len:" + s.length());
    String a = compressStringBase64(s);
    System.out.println("After Compress:" + sw.getElapsedTimeInMilliSecs() + "ms, Len:" + a.length()+ ' ' + a );

    String b = decompressStringBase64(a);
    System.out.println("After Decompress:" + sw.getElapsedTimeInMilliSecs() + "ms, Len:" + b.length());

  }

}
