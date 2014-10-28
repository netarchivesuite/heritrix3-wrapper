package org.netarchivesuite.heritrix3.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ScriptEngine {

    @XmlElement(required=true)
    public String engine;

    @XmlElement(required=true)
    public String language;

}
