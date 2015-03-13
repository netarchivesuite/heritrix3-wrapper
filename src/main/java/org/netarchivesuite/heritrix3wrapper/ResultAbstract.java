package org.netarchivesuite.heritrix3wrapper;

import org.netarchivesuite.heritrix3wrapper.xmlutils.XmlValidationResult;

public abstract class ResultAbstract {

    public int status;

    public Throwable t;

    public int responseCode;

    public byte[] response;

    public XmlValidationResult result = new XmlValidationResult();

}
