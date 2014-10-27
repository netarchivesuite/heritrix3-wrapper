package org.netarchivesuite.heritrix3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class Heritrix3Launcher {

    public ProcessBuilder processBuilder;

    public Map<String, String> env;

    public Process process;

    protected Heritrix3Launcher() {
    }

    public static Heritrix3Launcher getInstance() {
        return new Heritrix3Launcher();
    }

    public void init(File basedir, String[] cmd) {
        processBuilder = new ProcessBuilder(cmd);
        processBuilder.directory(basedir);
        env = processBuilder.environment();
    }

    public void init(File basedir, List<String> cmd) {
        processBuilder = new ProcessBuilder(cmd);
        processBuilder.directory(basedir);
        env = processBuilder.environment();
    }

    public void start() throws IOException {
        process = processBuilder.start();
        Thread waitForThread = new Thread(new WaitForThread());
        waitForThread.start();
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        Thread outputSinkThread = new Thread(new OutputSinkThread(outputReader));
        outputSinkThread.start();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        Thread errorSinkThread = new Thread(new OutputSinkThread(errorReader));
        errorSinkThread.start();
    }

    protected class WaitForThread implements Runnable {
        @Override
        public void run() {
            Integer exitValue = null;
            try {
                exitValue = process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // debug
            System.out.println("exitvalue=" + exitValue);
        }
    }

    protected class OutputSinkThread implements Runnable {
        BufferedReader reader;
        protected OutputSinkThread(BufferedReader reader) {
            this.reader = reader;
        }
        @Override
        public void run() {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    // debug
                    System.out.println(line);
                }
            } catch (IOException e) {
            }
        }
    }

}
