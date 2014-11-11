package org.netarchivesuite.heritrix3wrapper.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ThreadReport {

    @XmlElement(required=true)
    public Integer toeCount;

    @XmlElementWrapper(name="steps")
    @XmlElement(name="value", required=false)
    public List<String> steps;

    @XmlElementWrapper(name="processors")
    @XmlElement(name="value", required=true)
    public List<String> processors;

}
