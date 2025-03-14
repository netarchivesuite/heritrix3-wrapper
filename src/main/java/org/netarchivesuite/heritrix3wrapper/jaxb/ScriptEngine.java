package org.netarchivesuite.heritrix3wrapper.jaxb;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ScriptEngine {

    @XmlElement(required=true)
    public String engine;

    @XmlElement(required=true)
    public String language;

}
