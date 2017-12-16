package eb.compress;

import de.eb.compress.EBCompress;
import junit.framework.TestCase;


public class EBCompressTest extends TestCase {
  
  public EBCompressTest(String testName) {
    super(testName);
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test of compressStringBase64 method, of class EBCompress.
   * @throws java.lang.Exception
   */
  public void testCompress() throws Exception {
    System.out.println("compressStringBase64");
    String s = "Hallo Welt";
    String expResult = "Hallo Welt";
    String result = EBCompress.decompressStringBase64((EBCompress.compressStringBase64(s)));
    assertEquals(expResult, result);
  }

  
}
