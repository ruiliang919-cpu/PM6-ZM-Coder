package com.ruoyi.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ModbusTcpDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        String HEXES = "0123456789ABCDEF";
        byte[] req = new byte[msg.readableBytes()];
        msg.readBytes(req);
        final StringBuilder hex = new StringBuilder(2 * req.length);

        for (int i = 0; i < req.length; i++) {
            byte b = req[i];
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                .append(HEXES.charAt((b & 0x0F)));
        }
        // System.out.println("~~~~~decode~~~~~~"+hex.toString());
        out.add(hex.toString());
    }
}

