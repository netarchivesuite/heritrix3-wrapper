package org.netarchivesuite.heritrix3wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class CommandLauncher {

    public ProcessBuilder processBuilder;

    public Map<String, String> env;

    public Process process;

    protected CommandLauncher() {
    }

    public static CommandLauncher getInstance() {
        return new CommandLauncher();
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

    public void start(LaunchResultHandlerAbstract resultHandler) throws IOException {
        process = processBuilder.start();
        Thread waitForThread = new Thread(new WaitForThread(resultHandler));
        waitForThread.start();
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        Thread outputSinkThread = new Thread(new OutputSinkThread(outputReader, resultHandler));
        outputSinkThread.start();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        Thread errorSinkThread = new Thread(new ErrorSinkThread(errorReader, resultHandler));
        errorSinkThread.start();
    }

    protected class WaitForThread implements Runnable {
        LaunchResultHandlerAbstract resultHandler;
        public WaitForThread(LaunchResultHandlerAbstract resultHandler) {
            this.resultHandler = resultHandler;
        }
        @Override
        public void run() {
            Integer exitValue = null;
            try {
                exitValue = process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (resultHandler != null) {
                resultHandler.exitValue(exitValue);
            }
        }
    }

    protected class OutputSinkThread implements Runnable {
        LaunchResultHandlerAbstract resultHandler;
        BufferedReader reader;
        protected OutputSinkThread(BufferedReader reader, LaunchResultHandlerAbstract resultHandler) {
            this.reader = reader;
            this.resultHandler = resultHandler;
        }
        @Override
        public void run() {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    if (resultHandler != null)  {
                        resultHandler.output(line);
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    protected class ErrorSinkThread implements Runnable {
        LaunchResultHandlerAbstract resultHandler;
        BufferedReader reader;
        protected ErrorSinkThread(BufferedReader reader, LaunchResultHandlerAbstract resultHandler) {
            this.reader = reader;
            this.resultHandler = resultHandler;
        }
        @Override
        public void run() {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    if (resultHandler != null)  {
                        resultHandler.error(line);
                    }
                }
            } catch (IOException e) {
            }
        }
    }

}
