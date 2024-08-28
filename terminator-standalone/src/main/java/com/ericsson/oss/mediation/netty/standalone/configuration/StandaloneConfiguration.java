/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.mediation.netty.standalone.configuration;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * standalone configuration lists all datapath to start at astartup
 * 
 */
@XmlRootElement
public class StandaloneConfiguration implements Serializable {

    private static final long serialVersionUID = 7303887218845375565L;
    private StartDatapath startDatapath;

    public StartDatapath getStartDatapath() {
        return this.startDatapath;
    }

    @XmlElement
    public void setStartDatapath(final StartDatapath startDatapath) {
        this.startDatapath = startDatapath;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("StandaloneConfiguration [startDatapath=");
        builder.append(this.startDatapath);
        builder.append("]");
        return builder.toString();
    }

}
