package org.netarchivesuite.heritrix3.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HeapReport {

    @XmlElement(required=true)
    public long usedBytes;

    @XmlElement(required=true)
    public long totalBytes;

    @XmlElement(required=true)
    public long maxBytes;

}
