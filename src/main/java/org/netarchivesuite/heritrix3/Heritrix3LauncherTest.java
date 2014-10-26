package org.netarchivesuite.heritrix3;

import java.io.File;
import java.net.URL;

public class Heritrix3LauncherTest {

    protected static ClassLoader clsLdr = Heritrix3WrapperTest.class.getClassLoader();

    public static final File getTestResourceFile(String fname) {
        URL url = clsLdr.getResource(fname);
        String path = url.getFile();
        path = path.replaceAll("%5b", "[");
        path = path.replaceAll("%5d", "]");
        File file = new File(path);
        return file;
    }

	// /home/nicl/workspace/heritrix3-wrapper/NetarchiveSuite-heritrix3-bundler-5.0-SNAPSHOT.zip

    public static void main(String[] args) {
    }

}
