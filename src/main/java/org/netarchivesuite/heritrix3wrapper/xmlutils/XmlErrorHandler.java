package org.netarchivesuite.heritrix3wrapper.xmlutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Implements an XML error handler which can be used while parsing/validating XML files.
 */
public class XmlErrorHandler extends XmlErrorHandlerAbstract {

    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(XmlErrorHandler.class.getName());

    @Override
    public void error(SAXParseException exception) throws SAXException {
        ++numberOfErrors;
        errors.add("Line " + exception.getLineNumber() + ", Column " + exception.getColumnNumber() + ": " + exception.getMessage());
        logger.error("SAX parsing error!", "Line " + exception.getLineNumber() + ", Column " + exception.getColumnNumber() + ": " + exception.getMessage(), exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        ++numberOfFatalErrors;
        fatalErrors.add("Line " + exception.getLineNumber() + ", Column " + exception.getColumnNumber() + ": " + exception.getMessage());
        logger.error("SAX parsing error!", "Line " + exception.getLineNumber() + ", Column " + exception.getColumnNumber() + ": " + exception.getMessage(), exception);
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        ++numberOfWarnings;
        warnings.add("Line " + exception.getLineNumber() + ", Column " + exception.getColumnNumber() + ": " + exception.getMessage());
        logger.warn("SAX parsing warning!", "Line " + exception.getLineNumber() + ", Column " + exception.getColumnNumber() + ": " + exception.getMessage(), exception);
    }

}
