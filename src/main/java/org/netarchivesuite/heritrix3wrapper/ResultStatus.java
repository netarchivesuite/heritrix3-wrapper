package org.netarchivesuite.heritrix3wrapper;

public class ResultStatus {

    public static final int OK = 0;
    public static final int OFFLINE = -1;
    public static final int RESPONSE_EXCEPTION = -2;
    public static final int NO_RESPONSE = -3;
    public static final int NOT_FOUND = -4;
    public static final int INTERNAL_ERROR = -5;
    public static final int XML_EXCEPTION = -6;
    public static final int JAXB_EXCEPTION = -7;

}
