package org.netarchivesuite.heritrix3wrapper.jaxb;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ElapsedReport {

    @XmlElement(required=true)
    public Long elapsedMilliseconds;

    @XmlElement(required=true)
    public String elapsedPretty;

}
