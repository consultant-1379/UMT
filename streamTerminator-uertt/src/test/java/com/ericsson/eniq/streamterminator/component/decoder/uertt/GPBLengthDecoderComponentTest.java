package com.ericsson.eniq.streamterminator.component.decoder.uertt;

import com.ericsson.oss.mediation.netty.component.Component;
import com.ericsson.oss.mediation.netty.helper.LocatorFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class GPBLengthDecoderComponentTest {

    @Test
    public void findGPBLengthDecoderComponentViaSPI() {
        final List<Component> components = LocatorFactory.instance().load(Component.class);

        boolean found = false;
        for (final Component c : components) {
            if (c.getClass().equals(GPBLengthDecoderComponent.class)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(String.format("Failed to find [%s] via SPI, only found [%s]", GPBLengthDecoderComponent.class, components), found);
    }

    @Test
    public void checkGPBLengthDecoderHandler() {

        GPBLengthDecoderComponent component = new GPBLengthDecoderComponent();

        Assert.assertEquals(GPBLengthDecoderHandler.class, component.getHandler().getClass());
    }

}
