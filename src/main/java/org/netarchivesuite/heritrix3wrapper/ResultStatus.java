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

    protected ResultStatus() {
    }

    public static String toString(int resultStatus) {
    	switch (resultStatus) {
    	case OK:
    		return "HTTP response code 200";
    	case OFFLINE:
    		return "NoHttpResponseException";
    	case RESPONSE_EXCEPTION:
    		return "ClientProtocolException";
    	case NO_RESPONSE:
    		return "HTTP response empty";
    	case NOT_FOUND:
    		return "HTTP response code 404";
    	case INTERNAL_ERROR:
    		return "HTTP response code 500";
    	case XML_EXCEPTION:
    		return "XMLStreamException";
    	case JAXB_EXCEPTION:
    		return "JAXBException";
   		default:
   			return "Unknown result status!";
    	}
    }

}
