package org.netarchivesuite.heritrix3.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RateReport {

    @XmlElement(required=true)
    public double currentDocsPerSecond;

    @XmlElement(required=true)
    public double averageDocsPerSecond;

    @XmlElement(required=true)
    public int currentKiBPerSec;

    @XmlElement(required=true)
    public int averageKiBPerSec;

}
