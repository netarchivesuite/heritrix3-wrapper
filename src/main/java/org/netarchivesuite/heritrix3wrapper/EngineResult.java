package org.netarchivesuite.heritrix3wrapper;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.netarchivesuite.heritrix3wrapper.jaxb.Engine;
import org.netarchivesuite.heritrix3wrapper.xmlutils.XmlValidationResult;
import org.netarchivesuite.heritrix3wrapper.xmlutils.XmlValidator;

    /*
     *      <statusDescription>Finished: CREATED</statusDescription>
     *      <statusDescription>Unbuilt</statusDescription>
     *
     *      <crawlControllerState>NASCENT</crawlControllerState>
     *      <statusDescription>Ready</statusDescription>
Launched
  <crawlControllerState>PREPARING</crawlControllerState>
  <statusDescription>Active: PREPARING</statusDescription>
Launched in paused state
  <crawlControllerState>PAUSED</crawlControllerState>
  <statusDescription>Active: PAUSED</statusDescription>
Running
  <crawlControllerState>RUNNING</crawlControllerState>
  <statusDescription>Active: RUNNING</statusDescription>
Finished
  <crawlControllerState>FINISHED</crawlControllerState>
  <crawlExitStatus>FINISHED</crawlExitStatus>
  <statusDescription>Finished: FINISHED</statusDescription>
Terminated
  <crawlControllerState>FINISHED</crawlControllerState>
  <crawlExitStatus>ABORTED</crawlExitStatus>
  <statusDescription>Ready</statusDescription>
    */
public class EngineResult {

    public int status;

    public Throwable t;

    public int responseCode;

    public byte[] response;

    public XmlValidationResult result = new XmlValidationResult();

    public Engine engine;

    public void parse(XmlValidator xmlValidator) throws JAXBException, XMLStreamException {
        ByteArrayInputStream bIn;
        result = new XmlValidationResult();
        bIn = new ByteArrayInputStream(response);
        xmlValidator.testStructuralValidity(bIn, null, null, result);
        bIn = new ByteArrayInputStream(response);
        engine = Engine.unmarshall(bIn);
    }

}
