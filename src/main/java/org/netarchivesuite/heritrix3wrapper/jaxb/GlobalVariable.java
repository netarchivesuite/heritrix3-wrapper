package org.netarchivesuite.heritrix3wrapper.jaxb;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GlobalVariable {

    @XmlElement(required=true)
    public String variable;

    @XmlElement(required=true)
    public String description;

}
