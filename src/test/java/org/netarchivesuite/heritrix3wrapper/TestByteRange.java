package org.netarchivesuite.heritrix3wrapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestByteRange {

    @Test
    public void test_byterange() {
        ByteRange byteRange;

        byteRange = ByteRange.parse(null);
        Assert.assertNull(null, byteRange);

        byteRange = ByteRange.parse("");
        Assert.assertNull(null, byteRange);

        byteRange = ByteRange.parse("bytes1-2/3");
        Assert.assertNull(null, byteRange);

        byteRange = ByteRange.parse("bytes");
        Assert.assertNull(null, byteRange);

        byteRange = ByteRange.parse("1-2/3");
        Assert.assertNull(null, byteRange);

        byteRange = ByteRange.parse("bytes 1-2");
        Assert.assertNull(null, byteRange);

        byteRange = ByteRange.parse("bytes 1/3");
        Assert.assertNull(null, byteRange);

        byteRange = ByteRange.parse("bytes -2/3");
        Assert.assertNull(null, byteRange);

        byteRange = ByteRange.parse("bytes 1-/3");
        Assert.assertNull(null, byteRange);

        byteRange = ByteRange.parse("bytes 3");
        Assert.assertNull(null, byteRange);

        byteRange = ByteRange.parse("bytes 1-2/3");
        Assert.assertNotNull(null, byteRange);
        Assert.assertEquals("bytes", byteRange.type);
        Assert.assertEquals(1L, byteRange.from);
        Assert.assertEquals(2L, byteRange.to);
        Assert.assertEquals(3L, byteRange.contentLength);

        byteRange = ByteRange.parse("bytes 100-126501/126502");
        Assert.assertNotNull(null, byteRange);
        Assert.assertEquals("bytes", byteRange.type);
        Assert.assertEquals(100L, byteRange.from);
        Assert.assertEquals(126501L, byteRange.to);
        Assert.assertEquals(126502L, byteRange.contentLength);

        byteRange = ByteRange.parse("bytes 1024-2047/128144");
        Assert.assertNotNull(null, byteRange);
        Assert.assertEquals("bytes", byteRange.type);
        Assert.assertEquals(1024L, byteRange.from);
        Assert.assertEquals(2047L, byteRange.to);
        Assert.assertEquals(128144L, byteRange.contentLength);
    }

}
