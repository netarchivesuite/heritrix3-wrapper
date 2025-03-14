package org.netarchivesuite.heritrix3wrapper.jaxb;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Report {

    @XmlElement(required=true)
    public String className;

    @XmlElement(required=true)
    public String shortName;

}
