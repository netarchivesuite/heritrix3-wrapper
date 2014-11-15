package org.netarchivesuite.heritrix3wrapper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import org.netarchivesuite.heritrix3wrapper.Heritrix3Wrapper.CrawlControllerState;
import org.netarchivesuite.heritrix3wrapper.unzip.UnzipUtils;

public class HarvestWorkflow {

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
        HarvestWorkflow p = new HarvestWorkflow();
        p.Main(args);
    }

    PrintWriter outputPrinter;
    PrintWriter errorPrinter;

    public void Main(String[] args) {
        String zipFileStr = "/home/nicl/workspace/heritrix3-wrapper/NetarchiveSuite-heritrix3-bundler-5.0-SNAPSHOT.zip";
        String unpackDirStr = "/home/nicl/heritrix3-wrapper-test/";
        String basedirStr = unpackDirStr + "heritrix-3.2.0/";
        String[] cmd = {
                "./bin/heritrix",
                "-b 192.168.1.101",
                "-p 6443",
                "-a h3server:h3server",
                "-s h3server.jks,h3server,h3server"
        };

        UnzipUtils unzipUtils = new UnzipUtils();
        CommandLauncher h3launcher;
        Heritrix3Wrapper h3wrapper;
        EngineResult engineResult;
        JobResult jobResult;

        try {
            unzipUtils.unzip(zipFileStr, unpackDirStr);
            File basedir = new File(basedirStr);

            File h3serverjksFile = getTestResourceFile("h3server.jks");
            Heritrix3Wrapper.copyFile( h3serverjksFile, basedir );

            h3launcher = CommandLauncher.getInstance();

            outputPrinter = new PrintWriter(new File(basedir, "heritrix3.out"), "UTF-8");
            errorPrinter = new PrintWriter(new File(basedir, "heritrix3.err"), "UTF-8");

            h3launcher.init(basedir, cmd);
            h3launcher.env.put("FOREGROUND", "true");
            h3launcher.start(new LaunchResultHandlerAbstract() {
                @Override
                public void exitValue(int exitValue) {
                    // debug
                    System.out.println("exitValue=" + exitValue);
                }
                @Override
                public void output(String line) {
                    outputPrinter.println(line);
                }
                @Override
                public void closeOutput() {
                    outputPrinter.close();
                }
                @Override
                public void error(String line) {
                    errorPrinter.println(line);
                }
                @Override
                public void closeError() {
                    errorPrinter.close();
                }
            });

            //File ksFile = getTestResourceFile("h3client.jks");
            h3wrapper = Heritrix3Wrapper.getInstance("192.168.1.101", 6443, null, null, "h3server", "h3server");

            engineResult = h3wrapper.waitForEngineReady(60, 1000);
            // debug
            System.out.println(engineResult.status + " - " + ResultStatus.OK);

            File cxmlFile = getTestResourceFile("crawler-beans.cxml");
            File seedsFile = getTestResourceFile("seeds3.txt");

            File jobsFile = new File(basedirStr, "jobs/");
            if (!jobsFile.exists()) {
                jobsFile.mkdirs();
            }

            String jobname = Long.toString(System.currentTimeMillis());
            File jobFile = new File(jobsFile, jobname);
            jobFile.mkdirs();

            Heritrix3Wrapper.copyFile( cxmlFile, jobFile );
            Heritrix3Wrapper.copyFileAs( seedsFile, jobFile, "seeds.txt" );

            engineResult = h3wrapper.rescanJobDirectory();
            //System.out.println(new String(engineResult.response, "UTF-8"));

            jobResult = h3wrapper.buildJobConfiguration(jobname);
            //System.out.println(new String(jobResult.response, "UTF-8"));

            jobResult = h3wrapper.waitForJobState(jobname, CrawlControllerState.NASCENT, 60, 1000);

            jobResult = h3wrapper.launchJob(jobname);
            //System.out.println(new String(jobResult.response, "UTF-8"));

            jobResult = h3wrapper.waitForJobState(jobname, CrawlControllerState.PAUSED, 60, 1000);

            jobResult = h3wrapper.unpauseJob(jobname);
            //System.out.println(new String(jobResult.response, "UTF-8"));

            boolean bFinished = false;
            while (!bFinished) {
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                }
                jobResult = h3wrapper.job(jobname);
                System.out.println(jobResult.job.isRunning);
                if (!jobResult.job.isRunning) {
                    System.out.println(new String(jobResult.response, "UTF-8"));
                    bFinished = true;
                }
            }

            jobResult = h3wrapper.teardownJob(jobname);
            //System.out.println(new String(jobResult.response, "UTF-8"));

            engineResult = h3wrapper.exitJavaProcess(null);
            h3launcher.process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
