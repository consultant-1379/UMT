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
package com.ericsson.eniq.streamterminator.configuration;

import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;

public class StartDatapath implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4589559717030090323L;
    private String[] datapath;

    public String[] getDatapath() {
        return this.datapath;
    }

    @XmlElement
    public void setDatapath(final String[] datapath) {
        this.datapath = datapath;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("StartDatapath [datapath=");
        builder.append(Arrays.toString(this.datapath));
        builder.append("]");
        return builder.toString();
    }
}
