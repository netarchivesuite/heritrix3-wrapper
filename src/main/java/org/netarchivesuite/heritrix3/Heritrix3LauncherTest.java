package org.netarchivesuite.heritrix3;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.http.client.ClientProtocolException;

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

    public static void main(String[] args) {
        String basedirStr = "/home/nicl/heritrix-3.2.0-unzip/heritrix-3.2.0/";
        String[] cmd = {
                "./bin/heritrix",
                "-b 192.168.1.101",
                "-p 6443",
                "-a h3server:h3server",
                "-s h3server.jks,h3server,h3server"
        };
        Heritrix3Launcher h3launcher;
        Heritrix3Wrapper h3wrapper;
        EngineResult engineResult;
        JobResult jobResult;
        try {
            h3launcher = Heritrix3Launcher.getInstance();

            File basedir = new File(basedirStr);
            h3launcher.init(basedir, cmd);
            h3launcher.env.put("FOREGROUND", "true");
            h3launcher.start();
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
            }

            File ksFile = getTestResourceFile("h3client.jks");
            h3wrapper = Heritrix3Wrapper.getInstance("192.168.1.101", 6443, ksFile, "h3client", "h3server", "h3server");

            engineResult = h3wrapper.rescanJobDirectory();
            System.out.println(new String(engineResult.response, "UTF-8"));

            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
            }

            engineResult = h3wrapper.exitJavaProcess();

            h3launcher.process.destroy();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

}
