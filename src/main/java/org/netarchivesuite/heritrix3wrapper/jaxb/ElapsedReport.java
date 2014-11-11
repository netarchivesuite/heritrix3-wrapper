package org.netarchivesuite.heritrix3wrapper.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ElapsedReport {

    @XmlElement(required=true)
    public Long elapsedMilliseconds;

    @XmlElement(required=true)
    public String elapsedPretty;

}
