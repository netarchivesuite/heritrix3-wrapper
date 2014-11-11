package org.netarchivesuite.heritrix3wrapper;

public interface LaunchResultHandlerAbstract {

    public void exitValue(int exitValue);

    public void output(String line);

    public void closeOutput();

    public void error(String line);

    public void closeError();

}
