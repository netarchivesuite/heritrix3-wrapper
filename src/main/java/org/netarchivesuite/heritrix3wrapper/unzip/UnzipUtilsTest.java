package org.netarchivesuite.heritrix3wrapper.unzip;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.netarchivesuite.heritrix3wrapper.Heritrix3WrapperTest;

public class UnzipUtilsTest {

    protected static ClassLoader clsLdr = Heritrix3WrapperTest.class.getClassLoader();

    public static final File getTestResourceFile(String fname) {
        URL url = clsLdr.getResource(fname);
        String path = url.getFile();
        path = path.replaceAll("%5b", "[");
        path = path.replaceAll("%5d", "]");
        File file = new File(path);
        return file;
    }

    public static void main(String[] args) {
        String zipFilePath = "/home/nicl/workspace/heritrix3-wrapper/NetarchiveSuite-heritrix3-bundler-5.0-SNAPSHOT.zip";
        String destDirectory = "/home/nicl/heritrix-3.2.0-unzip-2/";
        UnzipUtils unzipUtils = new UnzipUtils();
        try {
            unzipUtils.unzip(zipFilePath, destDirectory);
            unzipUtils.unzip(zipFilePath, 1, destDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
