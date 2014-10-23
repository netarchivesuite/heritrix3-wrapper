package org.netarchivesuite.heritrix3.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SizeTotalsReport {

    @XmlElement(required=true)
    public long dupByHash;

    @XmlElement(required=true)
    public long dupByHashCount;

    @XmlElement(required=true)
    public long novel;

    @XmlElement(required=true)
    public long novelCount;

    @XmlElement(required=true)
    public long notModified;

    @XmlElement(required=true)
    public long notModifiedCount;

    @XmlElement(required=true)
    public long total;

    @XmlElement(required=true)
    public long totalCount;

}
