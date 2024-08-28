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
package com.ericsson.eniq.streamterminator.component.decoder.uertt;

import io.netty.channel.ChannelHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.mediation.netty.component.AbstractComponent;

/**
 * EventDecoderHandler - first handler in the pipeline. used to decode events from the stream <b>warning: decoder is NOT sharable</b>
 * 
 * @author xnarrah
 */
public class GPBLengthDecoderComponent extends AbstractComponent{

    private static final Logger LOGGER = LoggerFactory.getLogger(GPBLengthDecoderComponent.class);

    public GPBLengthDecoderComponent() {
        super("1.0");
    }

    @Override
    public void stop() {
        LOGGER.debug("shutting down ...");
    }

    /*
     * 
     * @see com.ericsson.oss.mediation.netty.component.AbstractComponent#getHandler()
     */
    @Override
    protected ChannelHandler getHandler() {
        return new GPBLengthDecoderHandler();
    }
}
