package org.netarchivesuite.heritrix3wrapper;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class StreamResult extends ResultAbstract implements Closeable {

    public long contentLength;

    public ByteRange byteRange;

    public InputStream in;

    @Override
    public void close() throws IOException {
        if (in != null) {
            in.close();
            in = null;
        }
    }

}
