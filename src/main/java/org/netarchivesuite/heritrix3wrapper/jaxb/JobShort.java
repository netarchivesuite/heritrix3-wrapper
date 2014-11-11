package org.netarchivesuite.heritrix3wrapper.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.netarchivesuite.heritrix3wrapper.jaxb.DateFormatLastLaunch.LastLaunchAdadapter;

@XmlRootElement
public class JobShort {

    @XmlElement(required=true)
    public String shortName;

    @XmlElement(required=true)
    public String url;

    @XmlElement(required=true)
    public Boolean isProfile;

    @XmlElement(required=true)
    public Integer launchCount;

    // 2014-10-22T16:37:34.654+02:00
    @XmlElement(required=true)
    @XmlJavaTypeAdapter(LastLaunchAdadapter.class)
    public Long lastLaunch;

    @XmlElement(required=true)
    public Boolean hasApplicationContext;

    @XmlElement(required=true)
    public String statusDescription;

    @XmlElement(required=true)
    public Boolean isLaunchInfoPartial;

    @XmlElement(required=true)
    public String primaryConfig;

    @XmlElement(required=true)
    public String primaryConfigUrl;

    @XmlElement(required=false)
    public String crawlControllerState;

    @XmlElement(required=false)
    public String crawlExitStatus;

    @XmlElement(required=true)
    public String key;

}
