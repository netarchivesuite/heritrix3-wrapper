package org.netarchivesuite.heritrix3wrapper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.http.client.ClientProtocolException;
import org.netarchivesuite.heritrix3wrapper.jaxb.ConfigFile;

public class Heritrix3WrapperTest {

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
        File ksFile = getTestResourceFile("h3client.jks");
        Heritrix3Wrapper h3wrapper = Heritrix3Wrapper.getInstance("192.168.1.101", 6443, ksFile, "h3client", "h3server", "h3server");

        EngineResult engineResult;
        JobResult jobResult;

        try {
            //h3wrapper.gc();
            //h3wrapper.exitJavaProcess();
            //h3wrapper.rescanJobDirectory();
            //h3wrapper.addJobDirectory(jobFile.getPath());
            //h3wrapper.createNewJob("1" + Long.toString(System.currentTimeMillis()));
            //h3wrapper.buildJobConfiguration("11413812062192");
            //h3wrapper.launchJob("11413812062192");
            //h3wrapper.job("11413812062192");
            //h3wrapper.checkpointJob("11413812062192");
            //h3wrapper.unpauseJob("11413812062192");
            //h3wrapper.pauseJob("11413812062192");
            //h3wrapper.teardownJob("11413812062192");
            //h3wrapper.terminateJob("1413821837513");
            //h3wrapper.ExecuteShellScriptInJob("1413821837513", "beanshell", "System.out.println%28%22testing%22%29%3B");
            //h3wrapper.ExecuteShellScriptInJob("1413821837513", "beanshell", "System.out.println(\"testing&\");");
            //h3wrapper.ExecuteShellScriptInJob("1413821837513", "groovy", "this.binding.getVariables().each{ rawOut.println(\"${it.key}=\n ${it.value}\n\") }");
            //h3wrapper.ExecuteShellScriptInJob("1413821837513", "groovy", "this.binding.getVariables().each{ rawOut.println(\"${it.key}= ${it.value}\") }");
/*
            File cxmlFile = getTestResourceFile("crawler-beans.cxml");
            File seedsFile = getTestResourceFile("seeds.txt");

            File jobsFile = new File("/home/nicl/heritrix-3.2.0/jobs/");
            if (!jobsFile.exists()) {
                jobsFile.mkdirs();
            }

            String jobname = Long.toString(System.currentTimeMillis());
            File jobFile = new File(jobsFile, jobname);
            jobFile.mkdirs();

            Heritrix3Wrapper.copyFile( cxmlFile, jobFile );
            Heritrix3Wrapper.copyFile( seedsFile, jobFile );

            //engineResult = h3wrapper.addJobDirectory(jobFile.getPath());
            //System.out.println(new String(engineResult.response, "UTF-8"));

            engineResult = h3wrapper.rescanJobDirectory();
            System.out.println(new String(engineResult.response, "UTF-8"));

            jobResult = h3wrapper.buildJobConfiguration(jobname);
            System.out.println(new String(jobResult.response, "UTF-8"));

            jobResult = h3wrapper.launchJob(jobname);
            System.out.println(new String(jobResult.response, "UTF-8"));

            jobResult = h3wrapper.unpauseJob(jobname);
            System.out.println(new String(jobResult.response, "UTF-8"));

            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
            }

            jobResult = h3wrapper.job(jobname);
            System.out.println(new String(jobResult.response, "UTF-8"));

            jobResult = h3wrapper.pauseJob(jobname);
            System.out.println(new String(jobResult.response, "UTF-8"));

            jobResult = h3wrapper.checkpointJob(jobname);
            System.out.println(new String(jobResult.response, "UTF-8"));

            jobResult = h3wrapper.unpauseJob(jobname);
            System.out.println(new String(jobResult.response, "UTF-8"));

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

            //jobResult = h3wrapper.teardownJob(jobname);
            //System.out.println(new String(jobResult.response, "UTF-8"));

            //jobResult = h3wrapper.job("1413988654119");
            //System.out.println(new String(jobResult.response, "UTF-8"));
*/
/*
            File jobsFile = new File("/home/nicl/heritrix-3.2.0/jobs/");
            if (!jobsFile.exists()) {
                jobsFile.mkdirs();
            }

            String jobname1 = "1414049358711-copyTo";
            File jobFile1 = new File(jobsFile, jobname1);
            jobFile1.mkdirs();

            String jobname2 = "1414049358711-copyToProfile";
            File jobFile2 = new File(jobsFile, jobname2);
            jobFile2.mkdirs();

            jobResult = h3wrapper.copyJob("1414049358711", "1414049358711-copyTo", false);
            System.out.println(new String(jobResult.response, "UTF-8"));

            jobResult = h3wrapper.copyJob("1414049358711", "1414049358711-copyToProfile", true);
            System.out.println(new String(jobResult.response, "UTF-8"));

            engineResult = h3wrapper.rescanJobDirectory();
            System.out.println(new String(engineResult.response, "UTF-8"));
*/
            engineResult = h3wrapper.rescanJobDirectory();
            System.out.println(new String(engineResult.response, "UTF-8"));
/*
            jobResult = h3wrapper.job("1414049358711");
            System.out.println(new String(jobResult.response, "UTF-8"));

            ConfigFile configFile = jobResult.findConfigFile("loggerModule.path");
            jobResult = h3wrapper.test(configFile.url);
            System.out.println(new String(jobResult.response, "UTF-8"));

            configFile = jobResult.findConfigFile("statisticsTracker.reportsDir");
            System.out.println(new String(jobResult.response, "UTF-8"));
*/
            //h3wrapper.ExecuteShellScriptInJob("1414451864048", "groovy", "this.binding.getVariables().each{ rawOut.println(\"${it.key}= ${it.value}\") }");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*
Missing jobname
404
<h1>Page not found</h1>
The page you are looking for does not exist.  You may be able to recover by going <a href='javascript:history.back();void(0);'>back</a>.

pause/unpause
500
<h1>An error occured</h1>
You may be able to recover and try something else by going <a href='javascript:history.back();void(0);'>back</a>.
<h2>Cause: java.lang.NullPointerException</h2>
<pre>java.lang.NullPointerException
*/

}
