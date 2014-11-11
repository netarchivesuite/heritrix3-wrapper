package org.netarchivesuite.heritrix3wrapper;

public class ConsoleLaunchResultHandler implements LaunchResultHandlerAbstract {

    @Override
    public void exitValue(int exitValue) {
        System.out.println("exitValue=" + exitValue);
    }

    @Override
    public void output(String line) {
        System.out.println(line);
    }

    @Override
    public void closeOutput() {
    }

    @Override
    public void error(String line) {
        System.err.println(line);
    }

    @Override
    public void closeError() {
    }

}
