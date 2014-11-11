package org.netarchivesuite.heritrix3wrapper.xmlutils;

import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements an XSL error listener which can be used while transforming XML files.
 */
public class XslErrorListener extends XslErrorListenerAbstract {

    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(XslErrorListener.class.getName());

    @Override
    public void error(TransformerException exception) throws TransformerException {
        ++numberOfErrors;
        errors.add(exception.getMessageAndLocation());
        logger.error("XLST processing error!", exception.getMessageAndLocation(), exception);
    }

    @Override
    public void fatalError(TransformerException exception) throws TransformerException {
        ++numberOfFatalErrors;
        fatalErrors.add(exception.getMessageAndLocation());
        logger.error("XLST processing error!", exception.getMessageAndLocation(), exception);
    }

    @Override
    public void warning(TransformerException exception) throws TransformerException {
        ++numberOfWarnings;
        warnings.add(exception.getMessageAndLocation());
        logger.warn("XLST processing warning!", exception.getMessageAndLocation(), exception);
    }

}
