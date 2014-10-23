package org.netarchivesuite.heritrix3.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Report {

    @XmlElement(required=true)
    public String className;

    @XmlElement(required=true)
    public String shortName;

}
