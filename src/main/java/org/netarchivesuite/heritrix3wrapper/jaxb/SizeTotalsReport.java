package org.netarchivesuite.heritrix3wrapper.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SizeTotalsReport {

    @XmlElement(required=true)
    public Long dupByHash;

    @XmlElement(required=true)
    public Long dupByHashCount;

    @XmlElement(required=true)
    public Long novel;

    @XmlElement(required=true)
    public Long novelCount;

    @XmlElement(required=true)
    public Long notModified;

    @XmlElement(required=true)
    public Long notModifiedCount;

    @XmlElement(required=true)
    public Long total;

    @XmlElement(required=true)
    public Long totalCount;
    
    @XmlElement(required=true)
    public Long sizeOnDisk;

}
