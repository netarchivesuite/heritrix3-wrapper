package org.netarchivesuite.heritrix3wrapper.jaxb;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ConfigFile {

    @XmlElement(required=true)
    public String key;

    @XmlElement(required=true)
    public String name;

    @XmlElement(required=true)
    public String path;

    @XmlElement(required=true)
    public String url;

    @XmlElement(required=true)
    public Boolean editable;

}
