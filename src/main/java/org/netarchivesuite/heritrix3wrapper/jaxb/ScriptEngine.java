package org.netarchivesuite.heritrix3wrapper.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ScriptEngine {

    @XmlElement(required=true)
    public String engine;

    @XmlElement(required=true)
    public String language;

}
