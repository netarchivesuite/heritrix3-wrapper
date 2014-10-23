package org.netarchivesuite.heritrix3.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ElapsedReport {

    @XmlElement(required=true)
    public long elapsedMilliseconds;

    @XmlElement(required=true)
    public String elapsedPretty;

}
