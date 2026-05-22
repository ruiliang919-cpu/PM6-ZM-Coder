package com.ruoyi.netty;

import com.ruoyi.netty.handler.RtuHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * description: <h1>通道初始化</h1>
 *
 * @author bo
 * @version 1.0
 * @date 2024/2/27 16:13
 */
public class CarTcpNettyChannelInitializer<SocketChannel> extends ChannelInitializer<Channel> {

    private RtuHandler rtuHandler;

    public CarTcpNettyChannelInitializer(RtuHandler rtuHandler) {
        this.rtuHandler = rtuHandler;
    }

    @Override
    protected void initChannel(Channel ch) {

        // ByteBuf delemiter = Unpooled.buffer();
        // delemiter.writeBytes("$".getBytes());
        // 这里就是解决数据过长问题，而且数据是以$结尾的
        ch.pipeline().addLast(new ModbusTcpDecoder());

        // 自定义ChannelInboundHandlerAdapter
        ch.pipeline().addLast(new CarTcpNettyChannelInboundHandlerAdapter(rtuHandler));

    }

}
