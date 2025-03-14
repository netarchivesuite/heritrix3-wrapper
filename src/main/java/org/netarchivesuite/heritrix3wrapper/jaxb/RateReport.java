package org.netarchivesuite.heritrix3wrapper.jaxb;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RateReport {

    @XmlElement(required=true)
    public Double currentDocsPerSecond;

    @XmlElement(required=true)
    public Double averageDocsPerSecond;

    @XmlElement(required=true)
    public Integer currentKiBPerSec;

    @XmlElement(required=true)
    public Integer averageKiBPerSec;

}
