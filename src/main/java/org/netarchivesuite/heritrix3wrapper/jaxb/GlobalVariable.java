package org.netarchivesuite.heritrix3wrapper.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GlobalVariable {

    @XmlElement(required=true)
    public String variable;

    @XmlElement(required=true)
    public String description;

}
