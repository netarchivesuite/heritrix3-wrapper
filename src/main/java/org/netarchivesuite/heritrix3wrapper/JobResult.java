package org.netarchivesuite.heritrix3wrapper;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.netarchivesuite.heritrix3wrapper.jaxb.ConfigFile;
import org.netarchivesuite.heritrix3wrapper.jaxb.Job;
import org.netarchivesuite.heritrix3wrapper.xmlutils.XmlValidationResult;
import org.netarchivesuite.heritrix3wrapper.xmlutils.XmlValidator;

public class JobResult extends ResultAbstract {

    public Job job;

    public void parse(XmlValidator xmlValidator) throws JAXBException, XMLStreamException {
        ByteArrayInputStream bIn;
        result = new XmlValidationResult();
        bIn = new ByteArrayInputStream(response);
        xmlValidator.testStructuralValidity(bIn, null, null, result);
        bIn = new ByteArrayInputStream(response);
        job = Job.unmarshall(bIn);
    }

    public ConfigFile findConfigFile(String key) {
        ConfigFile configFile = null;
        int idx = 0;
        List<ConfigFile> configFiles = job.configFiles;
        ConfigFile tmpConfigFile;
        while (configFile == null && idx < configFiles.size()) {
            tmpConfigFile = configFiles.get(idx);
            if (key.equalsIgnoreCase(tmpConfigFile.key)) {
                configFile = tmpConfigFile;
            } else {
                ++idx;
            }
        }
        return configFile;
    }

}
