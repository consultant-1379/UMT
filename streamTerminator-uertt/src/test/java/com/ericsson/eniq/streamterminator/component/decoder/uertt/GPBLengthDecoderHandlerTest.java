package com.ericsson.eniq.streamterminator.component.decoder.uertt;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

public class GPBLengthDecoderHandlerTest {


        private static final byte[] EVENT = new byte[] { 0x00, 0x00, 0x01, 0x21, 0x00, 0x00, 0x00, 0x00, 0x0A, 0x10, 0x08, 0x00, 0x0F, 0x10, 0x09, 0x18,
                0x04, 0x20, 0x0B, 0x28, 0x0C, 0x30, 0x1C, 0x38, 0x00, 0x01, 0x12, 0x00, 0x01, 0x0A, 0x00, 0x01, 0x0A, 0x03, 0x31, 0x2D, 0x31, 0x12, 0x03,
                0x43, 0x4D, 0x31, 0x1A, 0x19, 0x08, 0x00, 0x12, 0x15, 0x33, 0x47, 0x50, 0x50, 0x20, 0x54, 0x53, 0x20, 0x32, 0x35, 0x2E, 0x33, 0x33, 0x31,
                0x20, 0x56, 0x39, 0x2E, 0x32, 0x2E, 0x30, 0x1A, 0x14, 0x08, 0x01, 0x12, 0x10, 0x30, 0x2E, 0x34, 0x2E, 0x30, 0x2E, 0x30, 0x2E, 0x32, 0x30,
                0x2E, 0x33, 0x2E, 0x32, 0x2E, 0x31, 0x1A, 0x14, 0x08, 0x02, 0x12, 0x10, 0x30, 0x2E, 0x34, 0x2E, 0x30, 0x2E, 0x30, 0x2E, 0x32, 0x30, 0x2E,
                0x33, 0x2E, 0x30, 0x2E, 0x31, 0x1A, 0x14, 0x08, 0x04, 0x12, 0x10, 0x30, 0x2E, 0x34, 0x2E, 0x30, 0x2E, 0x30, 0x2E, 0x32, 0x30, 0x2E, 0x33,
                0x2E, 0x31, 0x2E, 0x31, 0x1A, 0x04, 0x08, 0x05, 0x12, 0x00, 0x1A, 0x19, 0x08, 0x06, 0x12, 0x15, 0x33, 0x47, 0x50, 0x50, 0x20, 0x54, 0x53,
                0x20, 0x32, 0x35, 0x2E, 0x34, 0x35, 0x33, 0x20, 0x56, 0x37, 0x2E, 0x36, 0x2E, 0x30, 0x1A, 0x19, 0x08, 0x07, 0x12, 0x15, 0x33, 0x47, 0x50,
                0x50, 0x20, 0x54, 0x53, 0x20, 0x32, 0x35, 0x2E, 0x34, 0x31, 0x39, 0x20, 0x56, 0x36, 0x2E, 0x32, 0x2E, 0x30, 0x12, 0x15, 0x0A, 0x09, 0x20,
                0x0B, 0x28, 0x0C, 0x30, 0x1B, 0x38, 0x00, 0x05, 0x10, 0x00, 0x18, 0x08, 0x20, 0x00, 0x24, 0x28, 0x00, 0x4E, 0x1A, 0x10, 0x08, 0x00, 0x4E,
                0x12, 0x09, 0x20, 0x0B, 0x28, 0x0C, 0x30, 0x1B, 0x38, 0x00, 0x05, 0x20, 0x01, 0x22, 0x15, 0x0A, 0x09, 0x20, 0x0B, 0x28, 0x0C, 0x30, 0x1B,
                0x38, 0x00, 0x05, 0x12, 0x06, 0x52, 0x4E, 0x43, 0x32, 0x35, 0x32, 0x1A, 0x00, 0x1A, 0x24, 0x0A, 0x04, 0x00, 0x04, 0x00, 0x00, 0x10, 0x00,
                0x00, 0x00, 0x2C, 0x18, 0x00, 0x03, 0x22, 0x14, 0x08, 0x00, 0x24, 0x10, 0x08, 0x22, 0x0D, 0x00, 0x18, 0x0A, 0x0A, 0x08, 0x24, 0x09, 0x01,
                0x10, 0x09, 0x00, 0x11, 0x00 };





    @Test
    public void testGPBLength() {

        GPBLengthDecoderHandler handler = new GPBLengthDecoderHandler();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(handler);
        ByteBuf sendData = Unpooled.wrappedBuffer(EVENT);
        embeddedChannel.write(sendData);


    }

}
